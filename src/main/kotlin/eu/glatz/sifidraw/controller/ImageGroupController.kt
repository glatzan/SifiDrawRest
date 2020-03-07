package eu.glatz.sifidraw.controller

import eu.glatz.sifidraw.model.Image
import eu.glatz.sifidraw.model.ImageGroup
import eu.glatz.sifidraw.repository.ImageGroupRepository
import eu.glatz.sifidraw.repository.ImageRepository
import eu.glatz.sifidraw.service.ImageGroupService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.*
import java.nio.charset.Charset
import java.util.*

@CrossOrigin
@RestController
class ImageGroupController @Autowired constructor(
        private val imageGroupService: ImageGroupService,
        private val imageGroupRepository: ImageGroupRepository,
        private val imageRepository: ImageRepository) {

    @PostMapping("/imagegroup/create")
    fun createImageGroup(@RequestBody request: ImageGroupRequest?): ImageGroup? {

        if (request == null)
            throw IllegalArgumentException("Arguments not valid")

        return imageGroupService.createImageGroup(request.datasetpath, request.groupName)
    }

    @PostMapping("/imagegroup/addImage")
    fun addImageToGroup(@RequestBody request: ImageAddRequest?) {
        if (request == null)
            throw IllegalArgumentException("Arguments not valid")

        imageGroupService.addImageToGroup(request.group, request.image)
    }

    @GetMapping("/imagegroup/{id}")
    fun getImageGroupData(@PathVariable id: String, @RequestParam("format") format: Optional<String>): ImageGroup {
        val decodedID = String(Base64.getDecoder().decode(id), Charset.forName("UTF-8"))
        return imageGroupService.getImageGroup(decodedID, true, format.orElse("png"))
    }

    @PutMapping("/imagegroup/update")
    fun updateImageData(@RequestBody group: ImageGroup): ImageGroup {
        group.images.forEach {
            imageRepository.save(it)
        }
        return imageGroupRepository.save(group)
    }

    @DeleteMapping("/imagegroup/delete/{id}")
    fun deleteImageGroupData(@PathVariable id: String) {
        val decodedID = String(Base64.getDecoder().decode(id), Charset.forName("UTF-8"))
        imageGroupService.deleteImageGroup(decodedID)
    }

    class ImageGroupRequest {
        var datasetpath: String = ""
        var groupName: String = ""
    }

    class ImageAddRequest {
        lateinit var image: Image
        lateinit var group: ImageGroup
    }
}