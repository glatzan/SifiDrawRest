package eu.glatz.sifidraw

import org.springframework.beans.factory.annotation.Autowired
import eu.glatz.sifidraw.model.Dataset
import eu.glatz.sifidraw.repository.DatasetRepository
import org.springframework.web.bind.annotation.CrossOrigin
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.util.*


@CrossOrigin
@RestController
class SubProjectController @Autowired constructor(
        private val datasetRepository: DatasetRepository) {

//    @RequestMapping("/sub/{id}")
//    fun getRecognition(@PathVariable("id") id: String): Optional<Dataset> {
//        return subProjectRepository.findById(id)
//    }


//    @RequestMapping("/colleagues")
//    fun getColleagues(): List<Dataset> {
////        var subProject = Dataset()
////        subProject.id = "fdasdsad"
////        subProjectRepository.save(subProject)
//
//        return subProjectRepository.findAll()
//    }
}
