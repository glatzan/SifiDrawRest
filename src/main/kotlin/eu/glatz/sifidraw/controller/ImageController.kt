package eu.glatz.sifidraw.controller

import eu.glatz.sifidraw.config.ProjectSettings
import eu.glatz.sifidraw.model.Image
import eu.glatz.sifidraw.repository.ImageRepository
import eu.glatz.sifidraw.util.ImageUtil
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.*
import java.io.File
import java.nio.charset.Charset
import java.util.*


@CrossOrigin
@RestController
class ImageController @Autowired constructor(
        private val imageRepository: ImageRepository,
        private val projectSettings: ProjectSettings) {

    @GetMapping("/image/{id}")
    fun getImageData(@PathVariable id: String): Image {
        val decodedID = String(Base64.getDecoder().decode(id), Charset.forName("UTF-8"))
        val img = imageRepository.findById(id).orElse(Image(id, decodedID.substringAfterLast("/")))
        img.data = ImageUtil.readImgAsBase64(ImageUtil.findImage(projectSettings.dir, decodedID))
        return img
    }

    @PutMapping(value = "/image")
    fun modifyImageData(@RequestBody image: Image): Image {
        println("put")
        image.data = ""
        return imageRepository.save(image)
    }

    @PostMapping(value = "/image/{type}")
    fun createImageData(@RequestBody image: Image, @PathVariable type: String) {

        if (!type.matches(Regex("jpg|png|tif")))
            return;

        val decodedID = String(Base64.getDecoder().decode(image.id), Charset.forName("UTF-8"))

        val dir = File(projectSettings.dir,decodedID.substringBeforeLast("/"));

        if(!dir.isDirectory)
            dir.mkdirs();

        ImageUtil.writeBase64Img(image.data, File(projectSettings.dir, "${decodedID}.tiff"))

        println(decodedID +" " +image.layers)

        if (image.layers != null && image.layers.isNotEmpty()) {
            imageRepository.save(image)
        }
    }


    @DeleteMapping(value = "/image/{id}")
    fun deleteImageData(@PathVariable id: String) {

        val decodedID = String(Base64.getDecoder().decode(id), Charset.forName("UTF-8"))

        val obj = imageRepository.findById(decodedID);
        if (!obj.isPresent)
            return
        imageRepository.delete(obj.get())
    }
}
