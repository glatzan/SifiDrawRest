package eu.glatz.sifidraw

import eu.glatz.sifidraw.model.ProjectData
import eu.glatz.sifidraw.service.ProjectService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.CrossOrigin
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController


@CrossOrigin
@RestController
class ProjectController @Autowired constructor(private val projectService: ProjectService) {

    @CrossOrigin
    @RequestMapping("/projects")
    fun getProjectData(): List<ProjectData> {
        return projectService.getProjectData();
    }
}
