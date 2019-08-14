package eu.glatz.sifidraw.controller

import eu.glatz.sifidraw.config.ProjectSettings
import eu.glatz.sifidraw.model.Image
import eu.glatz.sifidraw.repository.ImageRepository
import eu.glatz.sifidraw.util.ImageUtil
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.*
import java.io.File


@CrossOrigin
@RestController
class ImageController @Autowired constructor(
        private val imageRepository: ImageRepository,
        private val projectSettings: ProjectSettings) {

    @GetMapping("/image/{id}")
    fun getImageData(@PathVariable id: String): Image {
        val img = imageRepository.findById(id).orElse(Image(id, id.substringAfterLast("_|_")))
        img.data = ImageUtil.readImgAsBase64(File(projectSettings.dir, id.replace("_|_", "/")))
        return img
    }

    @PutMapping(value = "/image")
    fun modifyImageData(@RequestBody image: Image): Image {
        println("put")
        image.data = ""
        return imageRepository.save(image)
    }

    @DeleteMapping(value = "/image/{id}")
    fun deleteImageData(@PathVariable id: String) {

        val obj = imageRepository.findById(id);
        if (!obj.isPresent)
            return
        imageRepository.delete(obj.get())
    }

    @PostMapping("/image/new/{id}")
    fun addImage(@PathVariable id: String, @RequestBody image: Image) {
        ImageUtil.writeBase64Img(image.data, File(id));
    }
}
