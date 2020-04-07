package eu.glatz.sifidraw.service

import eu.glatz.sifidraw.config.ProjectSettings
import eu.glatz.sifidraw.model.SAImage
import eu.glatz.sifidraw.model.SImage
import eu.glatz.sifidraw.model.SImageGroup
import eu.glatz.sifidraw.repository.SAImageRepository
import eu.glatz.sifidraw.repository.SDatasetRepository
import eu.glatz.sifidraw.repository.SProjectRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import java.io.File

@Service
class SImageGroupService @Autowired constructor(
        private val sProjectRepository: SProjectRepository,
        private val projectSettings: ProjectSettings,
        private val saImageService: SAImageService,
        private val saImageRepository: SAImageRepository,
        private val sDatasetRepository: SDatasetRepository) : AbstractFileService() {

    fun addImageGroupToDataset(imageGroup: SImageGroup, datasetID: String): SImageGroup {
        val dataset = sDatasetRepository.findById(datasetID).orElseThrow { IllegalArgumentException("Dataset not found!") }

        require(imageGroup.name.isEmpty()) { IllegalArgumentException("Provide ImageGroup name!") }

        val imageGroupFile = getUniqueFile(File(projectSettings.dir, dataset.path), imageGroup.name)
        imageGroupFile.mkdirs()

        imageGroup.path = "${dataset.path}${imageGroupFile.name}/"

        return saImageRepository.save(imageGroup)
    }

    fun cloneImageGroup(imageGroupID: String, targetDatasetID: String): SImageGroup {
        val imageGroup = saImageRepository.findById(imageGroupID).orElseThrow { IllegalArgumentException("ImageGroup not found!") }
        if (imageGroup !is SImageGroup)
            throw IllegalArgumentException("No ImageGroup found!")

        val images = imageGroup.images
        imageGroup.id = null
        imageGroup.images = mutableListOf()

        var clonedImageGroup = addImageGroupToDataset(imageGroup, targetDatasetID)

        for (image in images) {
            if (image !is SImage)
                continue
            clonedImageGroup = saImageService.cloneImage(image, clonedImageGroup).second as SImageGroup
        }

        return clonedImageGroup
    }

    fun addImageToDataset(datasetID: String, imageID: String): SAImage {
        val dataset = sDatasetRepository.findById(datasetID).orElseThrow { IllegalArgumentException("Dataset not found") }
        val image = saImageRepository.findById(imageID).orElseThrow { IllegalArgumentException("Image not found") }
        return saImageService.moveImageToDataset(image, dataset)
    }

}