package eu.glatz.sifidraw.service

import eu.glatz.sifidraw.config.ProjectSettings
import eu.glatz.sifidraw.model.SProject
import eu.glatz.sifidraw.repository.SProjectRepository
import org.bson.types.ObjectId
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import java.io.File

@Service
class SProjectService @Autowired constructor(
        private val sequenceGeneratorService: SequenceGeneratorService,
        private val projectSettings: ProjectSettings,
        private val projectRepository: SProjectRepository) : AbstractService() {

    fun createProject(name: String, path: String): SProject {
        val baseDir = File(projectSettings.dir)

        val newProject = SProject()
        newProject.name = name
        newProject.path = path

        return projectRepository.save(newProject)
    }

}