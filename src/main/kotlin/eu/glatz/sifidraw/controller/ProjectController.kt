package eu.glatz.sifidraw.controller

import eu.glatz.sifidraw.model.ProjectData
import eu.glatz.sifidraw.service.ProjectService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.CrossOrigin
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.nio.charset.Charset
import java.util.*


@CrossOrigin
@RequestMapping("SifiDrawRest")
class ProjectController @Autowired constructor(private val projectService: ProjectService) {

    @RequestMapping("/projects")
    fun getProjectData(): List<ProjectData> {
        return projectService.getProjectData();
    }

    @RequestMapping("/projects/create/{dir}")
    fun createProject(@PathVariable dir: String) {
        val dir = String(Base64.getDecoder().decode(dir), Charset.forName("UTF-8"))
        projectService.createProject(dir)
    }
}
