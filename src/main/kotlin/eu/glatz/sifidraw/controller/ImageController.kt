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

        val img = imageRepository.findById(id).orElse(Image(id, id.substringAfterLast("/")))
        img.data = ImageUtil.readImgAsBase64(File(projectSettings.dir, decodedID))
        return img
    }

    @PutMapping(value = "/image")
    fun modifyImageData(@RequestBody image: Image): Image {
        println("put")
        image.data = ""
        return imageRepository.save(image)
    }

    @PostMapping(value = "/image")
    fun createImageData(@RequestBody image: Image) {
        println("put")
        ImageUtil.writeBase64Img(image.data, File(projectSettings.dir, image.id))
        if (image.layers != null && image.layers.isEmpty()) {
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

    @PostMapping("/image/new/{id}")
    fun addImage(@PathVariable id: String, @RequestBody image: Image) {
        val decodedID = String(Base64.getDecoder().decode(id), Charset.forName("UTF-8"))
        ImageUtil.writeBase64Img(image.data, File(decodedID));
    }
}
