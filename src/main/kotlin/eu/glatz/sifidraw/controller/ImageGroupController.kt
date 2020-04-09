package eu.glatz.sifidraw.controller

import com.fasterxml.jackson.annotation.JsonView
import eu.glatz.sifidraw.model.SImageGroup
import eu.glatz.sifidraw.repository.SAImageRepository
import eu.glatz.sifidraw.service.SImageGroupService
import eu.glatz.sifidraw.util.JsonViews
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.*
import java.nio.charset.Charset
import java.util.*

@CrossOrigin
@RestController
@RequestMapping("SifiDrawRest")
class ImageGroupController @Autowired constructor(
        private val sImageGroupService: SImageGroupService,
        private val saImageRepository: SAImageRepository) {

    @PostMapping("/imagegroup/create/{name}")
    fun createImageGroup(@PathVariable name: String, @RequestParam("parentID") parentID: Optional<String>): SImageGroup {
        val decodedName = String(Base64.getDecoder().decode(name), Charset.forName("UTF-8"))
        if (name.isEmpty() || !parentID.isPresent)
            throw IllegalArgumentException("Arguments not valid")
        return sImageGroupService.createImageGroup(decodedName, parentID.get()).first
    }

    @JsonView(JsonViews.OnlyDatasetData::class)
    @GetMapping("/imagegroup/minimize/{id}")
    fun getMinimizedImageGroup(@PathVariable id: String, @RequestParam("format") format: Optional<String>): SImageGroup {
        return saImageRepository.findById(id).orElseThrow { IllegalArgumentException("Dataset not found (ID: $id)") } as SImageGroup
    }

    @GetMapping("/imagegroup/{id}")
    fun getImageGroup(@PathVariable id: String, @RequestParam("format") format: Optional<String>): SImageGroup {
        return sImageGroupService.loadImageGroup(id, true)
    }

    @GetMapping("/imagegroup/clone/{id}")
    fun cloneImageGroup(@PathVariable id: String, @RequestParam("parentID") parentID: Optional<String>): SImageGroup {
        return sImageGroupService.cloneImageGroup(id, parentID.orElse("")).first
    }

    @PutMapping("/imagegroup/update")
    fun updateImageGroup(@RequestBody imageGroup: SImageGroup) {
        return sImageGroupService.updateImageGroup(imageGroup)
    }

    @DeleteMapping("/imagegroup/delete/{id}")
    fun deleteImageGroup(@PathVariable id: String) {
        sImageGroupService.deleteImageGroup(id)
    }
}