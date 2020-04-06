package eu.glatz.sifidraw.controller

import com.fasterxml.jackson.annotation.JsonView
import eu.glatz.sifidraw.model.SAImage
import eu.glatz.sifidraw.model.SDataset
import eu.glatz.sifidraw.repository.SDatasetRepository
import eu.glatz.sifidraw.service.SDatasetService
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
    @GetMapping("/dataset/minimize/{id}")
    fun getMinimizedDataset(@PathVariable id: String): SDataset {
        return sDatasetRepository.findById(id).orElseThrow()
    }

    @JsonView(JsonViews.AllDatasetData::class)
    @GetMapping("/dataset/{id}")
    fun getDataset(@PathVariable id: String): SDataset {
        return sDatasetRepository.findById(id).orElseThrow()
    }

    @GetMapping("/dataset/new")
    fun createDataset(@RequestParam("name") base64Name: Optional<String>, @RequestParam("projectID") projectID: Optional<String>): SDataset {
        val datasetName = String(Base64.getDecoder().decode(base64Name.orElseThrow { IllegalArgumentException("Name must be provided") }.toByteArray()), Charset.forName("UTF-8"))
        return sDatasetService.createDataset(datasetName, projectID.orElse(""))
    }

    /**
     * addImageToDataset
     */
    @GetMapping("/dataset/addImage")
    fun moveImageToDataset(@RequestParam("datasetID") datasetID: Optional<String>, @RequestParam("imageID") imageID: Optional<String>): SAImage {
        return sDatasetService.addImageToDataset(datasetID.orElse(""), imageID.orElse(""))
    }
}
