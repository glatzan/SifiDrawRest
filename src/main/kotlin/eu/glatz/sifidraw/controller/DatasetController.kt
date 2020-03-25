package eu.glatz.sifidraw.controller

import eu.glatz.sifidraw.config.ProjectSettings
import eu.glatz.sifidraw.model.Dataset
import eu.glatz.sifidraw.model.Image
import eu.glatz.sifidraw.service.DatasetService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.*
import java.io.File
import java.nio.charset.Charset
import java.util.*
import kotlin.collections.ArrayList

@CrossOrigin
@RestController
@RequestMapping("SifiDrawRest")
class DatasetController @Autowired constructor(
        private val datasetService: DatasetService,
        private val projectSettings: ProjectSettings) {

    @GetMapping("/dataset/{id}")
    fun getDataset(@PathVariable id: String, @RequestParam("minimize") minimize: Optional<Boolean>): Dataset {
        val decodedID = String(Base64.getDecoder().decode(id), Charset.forName("UTF-8"))
        return datasetService.getDataset(decodedID, minimize.orElse(false));
    }

    @GetMapping("/datasets/{datasets}")
    fun getDatasets(@PathVariable datasets: String): Array<Dataset> {
        val decodedDatasets = String(Base64.getDecoder().decode(datasets), Charset.forName("UTF-8"))
        val result = ArrayList<Dataset>();
        val arr = decodedDatasets.split("-");
        for (a in arr) {
            result.add(getDataset(a, Optional.of(true)))
        }

        return result.toTypedArray();
    }

    @PostMapping("/dataset/new/{id}")
    fun createDataset(@PathVariable id: String): Boolean {
        val f = File(projectSettings.dir, String(Base64.getDecoder().decode(id), Charset.forName("UTF-8")))
        println("creating dir ${f.absolutePath}")
        return f.mkdir();
    }

    @PostMapping("/dataset/addImage")
    fun addImageToDataset(@RequestBody request: ImageAddRequest?) {
        if (request == null)
            throw IllegalArgumentException("Arguments not valid")

        datasetService.addImageToDataset(request.dataset, request.image)
    }

    class ImageAddRequest {
        lateinit var image: Image
        lateinit var dataset: Dataset
    }
}
