package eu.glatz.sifidraw.service

import eu.glatz.sifidraw.config.ProjectSettings
import eu.glatz.sifidraw.model.Dataset
import eu.glatz.sifidraw.model.ProjectData
import eu.glatz.sifidraw.model.SProject
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import java.io.File
import java.util.*


@Service
class ProjectService @Autowired constructor(
        private val sequenceGeneratorService: SequenceGeneratorService,
        private val projectSettings: ProjectSettings) : AbstractService() {

    public fun getProjectData(): List<ProjectData> {
        val base = File(projectSettings.dir)

        val directories = base.list { current, name -> File(current, name).isDirectory }
        val projects = directories.map { dir -> ProjectData(dir) }

        projects.forEach {
            val project = File(base, it.id)
            it.datasets = project.list { current, name -> File(current, name).isDirectory && !name.startsWith(".") }.map { dir -> Dataset(Base64.getEncoder().encodeToString("${it.id}/$dir".toByteArray()), dir) }
        }

        return projects
    }

    public fun createProject(dir: String) {
        val base = File(projectSettings.dir)
        val newProject = File(base, dir);
        newProject.mkdirs();
    }


}
