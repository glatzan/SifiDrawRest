package eu.glatz.sifidraw.service

import eu.glatz.sifidraw.config.ProjectSettings
import eu.glatz.sifidraw.model.SDataset
import eu.glatz.sifidraw.model.SImage
import eu.glatz.sifidraw.model.SImageGroup
import eu.glatz.sifidraw.model.SProject
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
        private val sImageGroupService: SImageGroupService,
        private val sDatasetRepository: SDatasetRepository) : AbstractFileService() {

    fun createDataset(name: String, projectID: String): Pair<SDataset, SProject> {
        val project = sProjectRepository.findById(projectID).orElseThrow { IllegalArgumentException("Project not found!") }

        val newDataset = SDataset()
        newDataset.name = name
        val datasetFile = getUniqueFile(File(projectSettings.dir, project.path), name)
        datasetFile.mkdirs()
        newDataset.path = "${project.path}${datasetFile.name}/"

        val dbDataset = sDatasetRepository.save(newDataset)

        project.datasets.add(dbDataset)
        val dbProject = sProjectRepository.save(project)

        return Pair(dbDataset, dbProject)
    }

    fun deleteDataset(datasetID: String, removeFromParent: Boolean = true): Boolean {
        val dataset = sDatasetRepository.findById(datasetID).orElseThrow { throw IllegalArgumentException("Dataset not found!") }

        for (img in dataset.images) {
            if (img is SImage)
                saImageService.deleteImage(img, true)
            else if (img is SImageGroup)
                sImageGroupService.deleteImageGroup(img)
        }

        if (removeFromParent) {
            val project = sProjectRepository.findByDatasets(dataset.id ?: "")
            if (project.isPresent) {
                val dbDataset = project.get().datasets.findLast { it.id == dataset.id }
                project.get().datasets.remove(dbDataset)
                sProjectRepository.save(project.get())
            }
        }

        sDatasetRepository.delete(dataset)
        File(projectSettings.dir, dataset.path).delete()
        return true
    }

}