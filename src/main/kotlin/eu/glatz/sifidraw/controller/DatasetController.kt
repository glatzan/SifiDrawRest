package eu.glatz.sifidraw.controller

import eu.glatz.sifidraw.model.Dataset
import eu.glatz.sifidraw.model.ProjectData
import eu.glatz.sifidraw.service.DatasetService
import eu.glatz.sifidraw.service.ProjectService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*

@CrossOrigin
@RestController
class DatasetController @Autowired constructor(private val datasetService: DatasetService) {

    @GetMapping("/dataset/{id}")
    fun getDataset(@PathVariable id: String): Dataset {
        println("hallo $id"        )
        return datasetService.getDataset(id);
    }
}
