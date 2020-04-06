package eu.glatz.sifidraw.service

import eu.glatz.sifidraw.config.ProjectSettings
import eu.glatz.sifidraw.model.*
import eu.glatz.sifidraw.repository.*
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import java.io.File
import java.nio.charset.Charset
import java.util.Base64.getEncoder

@Service
class DBConverterService @Autowired constructor(
        private val projectSettings: ProjectSettings,
        private val datasetRepository: SProjectRepository,
        private val imageGroupRepository: ImageGroupRepository,
        private val saImageRepository: SAImageRepository,
        private val sDatasetRepository: SDatasetRepository,
        private val sProjectRepository: SProjectRepository,
        private val projectRepository: SProjectRepository,
        private val imageRepository: ImageRepository) : AbstractService() {

    fun sync() {
        val baseDir = File(projectSettings.dir)
        val files = baseDir.listFiles()

        for (projectFile in files) {
            if (projectFile.isDirectory) {
                println("------")
                println("Project: ${projectFile.name}")
                val project = SProject()
                project.name = projectFile.name
                project.path = "${projectFile.name}/"

                // datasets
                val datasetFiles = projectFile.listFiles()

                for (datasetFile in datasetFiles) {
                    if (datasetFile.isDirectory) {
                        val datasetPath = "${project.name}/${datasetFile.name}/"

                        println("   Dataset: ${datasetPath}")

                        var dataset = SDataset()
                        dataset.name = datasetFile.name
                        dataset.path = datasetPath

                        // images
                        val imageFiles = datasetFile.listFiles()

                        for (imgFile in imageFiles) {
                            if (imgFile.isDirectory) {
                                val imageGroupPath = "$datasetPath${imgFile.name}/"
                                val dbImGroup: ImageGroup? = imageGroupRepository.findById(generateBase64ID(imageGroupPath)).orElse(null)

                                println("       ImageGroup: ${imageGroupPath} - db ${dbImGroup != null}")

                                var imageGroup = SImageGroup()
                                imageGroup.name = dbImGroup?.name ?: imgFile.name
                                imageGroup.path = imageGroupPath

                                val subImgs = imgFile.listFiles()
                                for (subImg in subImgs) {
                                    if (subImg.name.endsWith(".png")) {
                                        val sImage = getImage(subImg, imageGroupPath, true)
                                        imageGroup.images.add(sImage)
                                    }
                                }

                                imageGroup = saImageRepository.save(imageGroup)
                                dataset.images.add(imageGroup)

                            } else if (imgFile.name.endsWith(".png")) {
                                val image = getImage(imgFile, datasetPath)
                                dataset.images.add(image)
                            }

                        }

                        dataset = sDatasetRepository.save(dataset)
                        project.datasets.add(dataset)
                    }
                }

                sProjectRepository.save(project)
            }

        }
    }

    private fun getImage(imageFile: File, datasetPath: String, imgGroup: Boolean = false): SAImage {
        val imagePath = "$datasetPath${imageFile.name}"
        val dbImage: Image? = imageRepository.findById(generateBase64ID(imagePath)).orElse(null)

        if (imgGroup)
            println("           Image: ${imagePath} - db ${dbImage != null}")
        else
            println("       Image: ${imagePath} - db ${dbImage != null}")

        var image = SImage()
        image.name = dbImage?.name ?: imageFile.name
        image.path = imagePath
        image.fileExtension = dbImage?.fileExtension ?: ".png"
        image.width = dbImage?.width ?: 0
        image.height = dbImage?.height ?: 0
        image.layers = dbImage?.layers ?: mutableListOf<Layer>()
        image.hasLayerData = image.layers.isNotEmpty()
        image.concurrencyCounter = dbImage?.concurrencyCounter ?: 0

        return saImageRepository.save(image)
    }

    private fun generateBase64ID(str: String): String {
        return String(getEncoder().encodeToString(str.toByteArray()).toByteArray(), Charset.forName("UTF-8"))
    }
}