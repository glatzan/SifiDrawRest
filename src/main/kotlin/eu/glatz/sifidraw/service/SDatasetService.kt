package eu.glatz.sifidraw.service

import eu.glatz.sifidraw.config.ProjectSettings
import eu.glatz.sifidraw.model.SAImage
import eu.glatz.sifidraw.model.SDataset
import eu.glatz.sifidraw.repository.SAImageRepository
import eu.glatz.sifidraw.repository.SDatasetRepository
import eu.glatz.sifidraw.repository.SProjectRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import java.io.File

@Service
class SDatasetService @Autowired constructor(
        private val sProjectRepository: SProjectRepository,
        private val projectSettings: ProjectSettings,
        private val saImageService: SAImageService,
        private val saImageRepository: SAImageRepository,
        private val sDatasetRepository: SDatasetRepository) : AbstractFileService() {

    fun createDataset(name: String, projectID: String): SDataset {
        val project = sProjectRepository.findById(projectID).orElseThrow { IllegalArgumentException("Project not found!") }

        val newDataset = SDataset()
        newDataset.name = name
        val datasetFile = getUniqueFile(File(projectSettings.dir, project.path), name)
        newDataset.path = "${project.path}${datasetFile.name}/"

        return sDatasetRepository.save(newDataset)
    }

}