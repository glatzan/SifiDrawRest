package eu.glatz.sifidraw.service

import eu.glatz.sifidraw.config.ProjectSettings
import eu.glatz.sifidraw.model.Layer
import eu.glatz.sifidraw.model.SAImage
import eu.glatz.sifidraw.model.SImage
import eu.glatz.sifidraw.repository.SAImageRepository
import eu.glatz.sifidraw.repository.SDatasetRepository
import eu.glatz.sifidraw.repository.SProjectRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import java.io.File

@Service
class SyncFilesystemService @Autowired constructor(
        private val projectSettings: ProjectSettings,
        private val sProjectService: SProjectService,
        private val sDatasetService: SDatasetService,
        private val saImageService: SAImageService,
        private val saImageRepository: SAImageRepository,
        private val sDatasetRepository: SDatasetRepository,
        private val sImageGroupService: SImageGroupService,
        private val sProjectRepository: SProjectRepository) : AbstractService() {

    fun sync() {
        val baseDir = File(projectSettings.dir)
        val files = baseDir.listFiles() ?: return

        for (projectFile in files) {
            if (projectFile.isDirectory) {
                val projectName = projectFile.name
                val projectPath = "${projectFile.name}/"

                val dbProject = sProjectRepository.findByPath(projectPath).orElse(sProjectService.createProject(projectName))

                // datasets
                val datasetFiles = projectFile.listFiles()
                if (datasetFiles != null) {
                    for (datasetFile in datasetFiles) {
                        val datasetName = datasetFile.name
                        val datasetPath = "${projectPath}${datasetFile.name}/"

                        val dataset = sDatasetRepository.findByPath(datasetPath).orElse(sDatasetService.createDataset(datasetName, dbProject.id
                                ?: "").first)

                        // images
                        val imageFiles = datasetFile.listFiles()
                        if (imageFiles != null) {
                            for (imgFile in imageFiles) {
                                if (imgFile.isDirectory) {
                                    val imageGroupPath = "$datasetPath${imgFile.name}/"
                                    val imageGroupName = imgFile.name

                                    val imageGroup = saImageRepository.findByPath(imageGroupPath).orElse(sImageGroupService.createImageGroup(imageGroupName, dataset.id
                                            ?: "").first)

                                    val subImgs = imgFile.listFiles()

                                    if (subImgs != null) {
                                        for (subImg in subImgs) {
                                            if (subImg.name.endsWith(".png")) {
                                                checkImage(subImg, imageGroupPath, imageGroup.id ?: "")
                                            }
                                        }
                                    }
                                } else if (imgFile.name.endsWith(".png")) {
                                    val imagePath = "$datasetPath${imgFile.name}"
                                    checkImage(imgFile, imagePath, dataset.id ?: "")
                                }
                            }
                        }
                    }
                }

            }

        }

    }

    private fun checkImage(imageFile: File, datasetPath: String, parentID: String): SAImage {
        val imageName = imageFile.name
        val imagePath = "$datasetPath${imageName}"

        val dbImage: SAImage? = saImageRepository.findByPath(imagePath).orElse(null)

        if (dbImage == null) {
            val image = SImage()
            image.name = imageName
            image.path = imagePath
            image.fileExtension = ".png"
            image.width = 0
            image.height = 0
            image.layers = mutableListOf<Layer>()
            image.hasLayerData = image.layers.isNotEmpty()
            image.concurrencyCounter = 0
            return saImageService.createImage(image, parentID).first
        }

        return dbImage
    }

}