package eu.glatz.sifidraw.controller

import eu.glatz.sifidraw.config.ProjectSettings
import eu.glatz.sifidraw.model.Dataset
import eu.glatz.sifidraw.model.Image
import eu.glatz.sifidraw.model.ProjectData
import eu.glatz.sifidraw.repository.ImageRepository
import eu.glatz.sifidraw.service.ProjectService
import eu.glatz.sifidraw.util.ImageReader
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.CrossOrigin
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.bind.annotation.RequestBody
import javax.validation.Valid
import org.springframework.web.bind.annotation.RequestMethod
import org.springframework.web.bind.annotation.PathVariable
import java.io.File


@CrossOrigin
@RestController
class ImageController @Autowired constructor(
        private val imageRepository: ImageRepository,
        private val projectSettings: ProjectSettings) {

    @RequestMapping("/image/{id}")
    fun getImageData(@PathVariable id: String): Image {
        val img =  imageRepository.findById(id).orElse(Image(id,id.substringAfterLast("_|_")))
        img.data = ImageReader.readImgAsBase64(File(projectSettings.dir, id.replace("_|_","/")))
        return img
    }

    @RequestMapping(value = "/image/{id}", method = [RequestMethod.PUT])
    fun modifyImageData(@PathVariable("id") id: String, @Valid @RequestBody image: Image) {
        image.id = id;
        imageRepository.save(image)
    }

    @RequestMapping(value = "/image/{id}", method = [RequestMethod.DELETE])
    fun deleteImageData(@PathVariable id: String) {

        val obj = imageRepository.findById(id);
        if (!obj.isPresent)
            return
        imageRepository.delete(obj.get())
    }
}
