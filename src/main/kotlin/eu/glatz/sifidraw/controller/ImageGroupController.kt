package eu.glatz.sifidraw.controller

import eu.glatz.sifidraw.model.Image
import eu.glatz.sifidraw.model.ImageGroup
import eu.glatz.sifidraw.service.ImageGroupService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.CrossOrigin
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController

@CrossOrigin
@RestController
class ImageGroupController @Autowired constructor(
        private val imageGroupService: ImageGroupService) {

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

    class ImageGroupRequest {
        var datasetpath: String = ""
        var groupName: String = ""
    }

    class ImageAddRequest {
        lateinit var image: Image
        lateinit var group: ImageGroup
    }
}