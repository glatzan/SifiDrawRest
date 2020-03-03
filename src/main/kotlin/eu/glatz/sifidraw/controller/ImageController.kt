package eu.glatz.sifidraw.controller

import eu.glatz.sifidraw.config.ProjectSettings
import eu.glatz.sifidraw.model.Image
import eu.glatz.sifidraw.repository.ImageRepository
import eu.glatz.sifidraw.service.ImageService
import eu.glatz.sifidraw.util.ImageUtil
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.*
import org.springframework.web.multipart.MultipartFile
import java.io.File
import java.nio.charset.Charset
import java.util.*


@CrossOrigin
@RestController
class ImageController @Autowired constructor(
        private val imageRepository: ImageRepository,
        private val imageService: ImageService,
        private val projectSettings: ProjectSettings) {

    @GetMapping("/image/{id}")
    fun getImageData(@PathVariable id: String): Image {
        val decodedID = String(Base64.getDecoder().decode(id), Charset.forName("UTF-8"))
        return imageService.getImage(decodedID,true)
    }

    @PutMapping("/image/update")
    fun updateImageData(@RequestBody image: Image): Image {
        println("put")
        return imageRepository.save(image)
    }

    @PostMapping("/image/{type}")
    fun createImageData(@RequestBody image: Image, @PathVariable type: String) {

        if (!type.matches(Regex("jpg|png|tif")))
            return;

        imageService.addNewImageToPath(image, ".$type")
    }


    @DeleteMapping("/image/{id}")
    fun deleteImageData(@PathVariable id: String) {

        val decodedID = String(Base64.getDecoder().decode(id), Charset.forName("UTF-8"))

        val obj = imageRepository.findById(decodedID);
        if (!obj.isPresent)
            return
        imageRepository.delete(obj.get())
    }

    @PostMapping("/image/upload/{path}&{overwrite}")
    fun handlePicture(@RequestParam("file") multipartFile: MultipartFile?, @PathVariable path: String?, @PathVariable overwrite: String?) {
        if (multipartFile != null && !path.isNullOrEmpty() && multipartFile.size != 0L) {
            val overwriteB = overwrite.isNullOrEmpty() && overwrite.equals("o")
            val decodedPath = String(Base64.getDecoder().decode(path), Charset.forName("UTF-8"))
            val decodedPathAbs = File(projectSettings.dir, decodedPath)
            decodedPathAbs.mkdirs()


            var fileToWrite = File(decodedPathAbs, multipartFile.originalFilename);

            if(!overwriteB) {
                var i = -1;
                do {
                    i++;
                    val tmp: String = if (i <= 0) multipartFile.originalFilename
                            ?: "noName" else "${(i+96).toChar()}_${multipartFile.originalFilename ?: "noName"}"
                    fileToWrite = File(decodedPathAbs, tmp)
                } while (fileToWrite.exists() && i < 100)
            }
            println("Upload to ${fileToWrite.absolutePath}")

            ImageUtil.writeImg(multipartFile.bytes, fileToWrite)
        }
    }
}
