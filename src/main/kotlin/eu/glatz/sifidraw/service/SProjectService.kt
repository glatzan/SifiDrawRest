package eu.glatz.sifidraw.service

import eu.glatz.sifidraw.config.ProjectSettings
import eu.glatz.sifidraw.model.SProject
import eu.glatz.sifidraw.repository.SProjectRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import java.io.File

@Service
class SProjectService @Autowired constructor(
        private val sequenceGeneratorService: SequenceGeneratorService,
        private val projectSettings: ProjectSettings,
        private val projectRepository: SProjectRepository) : AbstractFileService() {

    fun createProject(name: String): SProject {
        val newProject = SProject()
        newProject.name = name

        if (!name.matches(Regex("\\w*")))
            throw IllegalArgumentException("Illegal name $name")

        val projectFile = getUniqueFile(File(projectSettings.dir), name)
        projectFile.mkdirs()
        newProject.path = "${projectFile.name}/"

        return projectRepository.save(newProject)
    }

}