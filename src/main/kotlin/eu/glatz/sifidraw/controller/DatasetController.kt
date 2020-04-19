package eu.glatz.sifidraw.controller

import com.fasterxml.jackson.annotation.JsonView
import eu.glatz.sifidraw.model.SDataset
import eu.glatz.sifidraw.repository.SDatasetRepository
import eu.glatz.sifidraw.service.SDatasetService
import eu.glatz.sifidraw.util.ImageSorter
import eu.glatz.sifidraw.util.JsonViews
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.*
import java.nio.charset.Charset
import java.util.*

@CrossOrigin
@RestController
@RequestMapping("SifiDrawRest")
class DatasetController @Autowired constructor(
        private val sDatasetService: SDatasetService,
        private val sDatasetRepository: SDatasetRepository) {

    @JsonView(JsonViews.OnlyDatasetData::class)
    @GetMapping("/dataset/minimized/{id}")
    fun getMinimizedDataset(@PathVariable id: String): SDataset {
        val dataset = sDatasetRepository.findById(id).orElseThrow { IllegalArgumentException("Dataset not found (ID: $id)") }
        return ImageSorter.sort(dataset) as SDataset
    }

    @JsonView(JsonViews.AllDatasetData::class)
    @GetMapping("/dataset/{id}")
    fun getDataset(@PathVariable id: String): SDataset {
        val dataset = sDatasetRepository.findById(id).orElseThrow { IllegalArgumentException("Dataset not found (ID: $id)") }
        return ImageSorter.sort(dataset) as SDataset
    }

    @GetMapping("/dataset/create/{name}")
    fun createDataset(@PathVariable name: String, @RequestParam("projectID") projectID: Optional<String>): SDataset {
        val decodedName = String(Base64.getDecoder().decode(name), Charset.forName("UTF-8"))
        if (name.isEmpty() || !projectID.isPresent)
            throw IllegalArgumentException("Arguments not valid")
        return sDatasetService.createDataset(decodedName, projectID.orElse("")).first
    }

    @DeleteMapping("/dataset/delete/{id}")
    fun deleteDataset(@PathVariable id: String): Boolean {
        return sDatasetService.deleteDataset(id)
    }

    @GetMapping("/dataset/rename/{id}")
    fun renameDataset(@PathVariable id: String, @RequestParam("newName") newName: Optional<String>): SDataset {
        val result = sDatasetRepository.findById(id).orElseThrow { IllegalArgumentException("Dataset not found!") }
        result.name = String(Base64.getDecoder().decode(newName.orElseThrow { IllegalArgumentException("Provide Name!") }), Charset.forName("UTF-8"))
        return sDatasetRepository.save(result)
    }
}
