package eu.glatz.sifidraw.controller

import com.fasterxml.jackson.annotation.JsonView
import eu.glatz.sifidraw.legacy.DBConverterService
import eu.glatz.sifidraw.model.SProject
import eu.glatz.sifidraw.repository.SProjectRepository
import eu.glatz.sifidraw.service.SProjectService
import eu.glatz.sifidraw.util.JsonViews
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.*
import java.nio.charset.Charset
import java.util.*


@CrossOrigin
@RestController
@RequestMapping("SifiDrawRest")
class ProjectController @Autowired constructor(
        private val sProjectRepository: SProjectRepository,
        private val dbConverterService: DBConverterService,
        private val sProjectService: SProjectService) {

    @JsonView(JsonViews.ProjectsAndDatasets::class)
    @GetMapping("/projects")
    fun getProjectData(): List<SProject> {
        return sProjectRepository.findAll()
    }

    @GetMapping("/projects/create/{name}")
    fun createProject(@PathVariable name: String): SProject {
        return sProjectService.createProject(String(Base64.getDecoder().decode(name), Charset.forName("UTF-8")))
    }

    @DeleteMapping("/projects/delete/{id}")
    fun deleteProject(@PathVariable id: String): Boolean {
        return sProjectService.deleteProject(id)
    }

    @GetMapping("/convertDB/version/{version}")
    fun convertOLDDatabase(@PathVariable version: String) {
        dbConverterService.sync()
    }
}
