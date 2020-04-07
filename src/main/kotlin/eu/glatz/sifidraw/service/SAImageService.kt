package eu.glatz.sifidraw.service

import eu.glatz.sifidraw.config.ProjectSettings
import eu.glatz.sifidraw.model.*
import eu.glatz.sifidraw.repository.SAImageRepository
import eu.glatz.sifidraw.repository.SDatasetRepository
import eu.glatz.sifidraw.util.ImageUtil
import org.apache.commons.io.FileUtils
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import org.springframework.web.multipart.MultipartFile
import java.io.File
import java.util.*

@Service
class SAImageService @Autowired constructor(
        private val sequenceGeneratorService: SequenceGeneratorService,
        private val projectSettings: ProjectSettings,
        private val sDatasetRepository: SDatasetRepository,
        private val saImageRepository: SAImageRepository,
        private val imageRepository: SAImageRepository) : AbstractFileService() {

    fun loadImage(imageID: String, loadImageData: Boolean = true, asFormat: String = "png"): SAImage {
        val image = saImageRepository.findById(imageID).orElseThrow { IllegalArgumentException("Image not found!") }

        if (image !is SImage)
            throw IllegalStateException("Not an image")

        if (loadImageData) {
            var data = ImageUtil.readImageAsBufferedImage(File(projectSettings.dir, image.path))
            image.width = data.width
            image.height = data.height

            // workaround for png to jpg conversion
            if (image.path.substringAfterLast(".") != asFormat)
                data = ImageUtil.prepareConvertImage(data)

            image.data = ImageUtil.imageToBase64(data, asFormat)
            image.fileExtension = asFormat

        }
        return image
    }

    fun updateImage(image: SAImage): SAImage {
        val dbImage: SAImage? = saImageRepository.findById(image.id ?: "").orElse(null)

        require(dbImage != null) { IllegalArgumentException("Image not found in db") }

        if (image.concurrencyCounter < dbImage.concurrencyCounter)
            return saImageRepository.save(image);
        else
            throw IllegalArgumentException("Concurrency Error: new image = ${image.concurrencyCounter}; old image ${dbImage.concurrencyCounter}")
    }

    fun moveImageToDataset(image: SAImage, dataset: SDataset): SAImage {
        //todo remove from old dataset
        val newImagePath = getUniqueFile(File(projectSettings.dir, dataset.path), File(image.path).name)
        FileUtils.moveFile(File(projectSettings.dir, image.path), newImagePath)
        image.path = "${dataset.path}${newImagePath.name}/"
        return saImageRepository.save(image)
    }

    fun addImageToDataset(image: SImage, datasetID: String, format: String): Pair<SImage, SDataset> {
        val dataset = sDatasetRepository.findById(datasetID).orElseThrow { IllegalArgumentException("Dataset not found!") }
        val result = addImageToParent(image, dataset, format)
        return Pair(result.first, result.second as SDataset)
    }

    fun addImageToImageGroup(image: SImage, imageGroupID: String, format: String): Pair<SImage, SImageGroup> {
        val imageGroup = saImageRepository.findById(imageGroupID).orElseThrow { IllegalArgumentException("ImageGroup not found!") }
        if (imageGroup !is SImageGroup)
            throw IllegalArgumentException("Parent is no ImageGroup")
        val result = addImageToParent(image, imageGroup, format)
        return Pair(result.first, result.second as SImageGroup)
    }

    fun addImageToParent(image: SImage, parent: SIHasImages, format: String = "png"): Pair<SImage, SIHasImages> {
        val imageNameWithExtension = if (image.path.contains("/")) image.path.substringAfterLast("/") else image.path
        val imagePath = getUniqueFile(File(projectSettings.dir, parent.path), imageNameWithExtension)
        ImageUtil.writeBase64Img(image.data, imagePath, format)
        image.path = "${parent.path}$imageNameWithExtension"
        val imageResult = saImageRepository.save(image)

        parent.images.add(image)
        val parentResult = saveParent(parent)

        return Pair(imageResult, parentResult)
    }


    fun deleteImage(imageID: String) {
        val image = saImageRepository.findById(imageID).orElseThrow { IllegalArgumentException("Image not found!") }

        if (image !is SImage)
            throw IllegalArgumentException("Entity is not an image!")

        saImageRepository.delete(image)
        FileUtils.forceDelete(File(projectSettings.dir, image.path))

        removeImageFromParent(imageID)
    }

    fun removeImageFromParent(imageID: String){

    }

    fun cloneImageToDataset(image: String, parentID: String): Pair<SImage, SDataset> {
        val dataset = sDatasetRepository.findById(parentID).orElseThrow { IllegalArgumentException("Dataset not found!") }
        val result = cloneImage(image, dataset)
        return Pair(result.first, result.second as SDataset)
    }

    fun cloneImageToImageGroup(image: String, parentID: String): Pair<SImage, SImageGroup> {
        val imageGroup = saImageRepository.findById(parentID).orElseThrow { IllegalArgumentException("ImageGroup not found!") }
        if (imageGroup !is SImageGroup)
            throw IllegalArgumentException("Parent is no ImageGroup")
        val result = cloneImage(image, imageGroup)
        return Pair(result.first, result.second as SImageGroup)
    }

    fun cloneImage(imageID: String, newParent: SIHasImages): Pair<SImage, SIHasImages> {
        val image = loadImage(imageID, true)

        if (image !is SImage)
            throw IllegalArgumentException("Entity is not an image!")

        return cloneImage(image as SImage, newParent)
    }

    fun cloneImage(image: SImage, newParent: SIHasImages): Pair<SImage, SIHasImages> {
        image.id = null
        return addImageToParent(image, newParent)
    }

    fun addMultiPartImageToDataset(multipartFile: MultipartFile, datasetID: String): Pair<SImage, SIHasImages> {
        val dataset = sDatasetRepository.findById(datasetID).orElseThrow { IllegalArgumentException("Dataset not found!") }

        val imageName = multipartFile.originalFilename ?: "noName.png"

        val image = SImage()
        image.name = imageName.substringBeforeLast(".")
        image.data = String(Base64.getEncoder().encodeToString(multipartFile.bytes).toByteArray())
        image.path
        image.fileExtension = "png"

        return addImageToParent(image, dataset)
    }

    private fun saveParent(parent: SIHasImages): SIHasImages {
        when (parent) {
            is SDataset -> return sDatasetRepository.save(parent)
            is SImageGroup -> return saImageRepository.save(parent)
            else -> throw IllegalArgumentException("Parent not recognized")
        }
    }


    fun createImage(name: String, path: String): SImage {
        val baseDir = File(projectSettings.dir)

        val image = SImage()
        image.name = name
        image.path = path

        return imageRepository.save(image)
    }

}