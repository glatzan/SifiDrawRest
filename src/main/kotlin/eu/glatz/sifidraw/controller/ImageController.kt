package eu.glatz.sifidraw.controller

import com.google.gson.Gson
import eu.glatz.sifidraw.config.ProjectSettings
import eu.glatz.sifidraw.model.Image
import eu.glatz.sifidraw.model.SAImage
import eu.glatz.sifidraw.repository.ImageRepository
import eu.glatz.sifidraw.service.ImageService
import eu.glatz.sifidraw.service.SAImageService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.*
import org.springframework.web.multipart.MultipartFile
import java.io.File
import java.nio.charset.Charset
import java.util.*


@CrossOrigin
@RestController
@RequestMapping("SifiDrawRest")
class ImageController @Autowired constructor(
        private val imageRepository: ImageRepository,
        private val imageService: ImageService,
        private val saImageService: SAImageService,
        private val projectSettings: ProjectSettings) {

    @GetMapping("/image")
    fun getImageData(@RequestParam("id") id: Optional<String>, @RequestParam("format") format: Optional<String>): SAImage {
        return saImageService.loadImage(id.orElse(""), true, format.orElse("png"))
    }

    @PutMapping("/image/update")
    fun updateImageData(@RequestBody image: SAImage): SAImage {
        return saImageService.saveImage(image)
    }









    @PutMapping("/image/update/checked")
    fun updateAndCheckImageData(@RequestBody image: Image): Image {
        return if (imageService.imageExist(image.id)) {
            println("image ok update" + image.name)
            return imageService.updateImage(image)
        } else {
            val img = Gson().toJson(image)
            println("Image not found!")
            println(img)
            Image("-", "-")
        }
    }

    @PostMapping("/image/{type}")
    fun createImageData(@RequestBody image: Image, @PathVariable type: String){

        if (!type.matches(Regex("jpg|png|tif|bmp")))
            throw IllegalArgumentException("Please provide Type")

        imageService.addNewImageToPath(image, type)
    }


    @DeleteMapping("/image/delete/{id}")
    fun deleteImageData(@PathVariable id: String): String {
        val decodedID = String(Base64.getDecoder().decode(id), Charset.forName("UTF-8"))
        imageService.deleteImage(decodedID)
        return ""
    }

    @GetMapping("/image/clone/{id}")
    fun cloneImage(@PathVariable id: String, @RequestParam("targetDir") targetDir: Optional<String>): Image {
        val decodedID = String(Base64.getDecoder().decode(id), Charset.forName("UTF-8"))
        return if (targetDir.isPresent) {
            imageService.cloneImage(decodedID, String(Base64.getDecoder().decode(targetDir.get()), Charset.forName("UTF-8")))
        } else {
            imageService.cloneImage(decodedID)
        }
    }

    @PostMapping("/image/upload/{path}")
    fun handlePicture(@RequestParam("file") multipartFile: MultipartFile?, @PathVariable path: String?, @RequestParam("format") format: Optional<String>): String {

        if (!format.isPresent || !format.get().matches(Regex("jpg|png|tif|bmp")))
            return "Please provide Type";

        if (multipartFile != null && !path.isNullOrEmpty() && multipartFile.size != 0L) {

            val imageName = multipartFile.originalFilename ?: "noName"
            val decodedPath = String(Base64.getDecoder().decode(path), Charset.forName("UTF-8"))
            val newFileID = File(decodedPath, imageName).path.replace("\\","/");
            val newEncodedID = String(Base64.getEncoder().encodeToString(newFileID.toByteArray()).toByteArray(), Charset.forName("UTF-8"))

            val displayName = if (imageName.matches(Regex("^.*\\.([a-zA-Z]{3,4})$"))) imageName.substringBeforeLast(".") else imageName
            val img = Image(newEncodedID, displayName)
            img.fileExtension = format.get()
            img.data = String(Base64.getEncoder().encodeToString(multipartFile.bytes).toByteArray())
            imageService.addNewImageToPath(img, format.get())

            return "Success"
        }

        return "Error: Failed"
    }

    @GetMapping("/image/rename/{id}")
    fun renameImage(@PathVariable id: String, @RequestParam("newName") newName: Optional<String>) {
        if (!newName.isPresent)
            return;

        val result = imageRepository.findById(id);

        if (result.isPresent) {
            result.get().name = String(Base64.getDecoder().decode(newName.get()), Charset.forName("UTF-8"))
            result.get().concurrencyCounter++;
            imageRepository.save(result.get())
        }
    }
}
