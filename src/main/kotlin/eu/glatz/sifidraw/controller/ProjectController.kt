package eu.glatz.sifidraw.controller

import eu.glatz.sifidraw.model.ProjectData
import eu.glatz.sifidraw.service.ProjectService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.*
import java.nio.charset.Charset
import java.util.*


@CrossOrigin
@RestController
@RequestMapping("SifiDrawRest")
class ProjectController @Autowired constructor(private val projectService: ProjectService) {

    @GetMapping("/projects")
    fun getProjectData(): List<ProjectData> {
        return projectService.getProjectData();
    }

    @RequestMapping("/projects/create/{dir}")
    fun createProject(@PathVariable dir: String) {
        val dir = String(Base64.getDecoder().decode(dir), Charset.forName("UTF-8"))
        projectService.createProject(dir)
    }
}
