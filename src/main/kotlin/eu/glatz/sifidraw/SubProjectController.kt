package eu.glatz.sifidraw

import eu.glatz.sifidraw.repository.SubProjectRepository
import org.springframework.beans.factory.annotation.Autowired
import ch.qos.logback.core.joran.spi.ConsoleTarget.findByName
import eu.glatz.sifidraw.model.SubProject
import org.springframework.web.bind.annotation.CrossOrigin
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.util.*


@CrossOrigin
@RestController
class SubProjectController @Autowired constructor(
        private val subProjectRepository: SubProjectRepository) {

    @RequestMapping("/sub/{id}")
    fun getRecognition(@PathVariable("id") id: String): Optional<SubProject> {
        return subProjectRepository.findById(id)
    }


    @RequestMapping("/colleagues")
    fun getColleagues(): List<SubProject> {
        var subProject = SubProject()
        subProject.id = "fdasdsad"
        subProjectRepository.save(subProject)

        return subProjectRepository.findAll()
    }
}
