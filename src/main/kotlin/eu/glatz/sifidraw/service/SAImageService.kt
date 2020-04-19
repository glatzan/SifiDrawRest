package eu.glatz.sifidraw.service

import eu.glatz.sifidraw.config.ProjectSettings
import eu.glatz.sifidraw.model.*
import eu.glatz.sifidraw.repository.SAImageRepository
import eu.glatz.sifidraw.repository.SDatasetRepository
import eu.glatz.sifidraw.repository.SProjectRepository
import eu.glatz.sifidraw.util.ImageUtil
import org.bson.types.ObjectId
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Lazy
import org.springframework.stereotype.Service
import org.springframework.web.multipart.MultipartFile
import java.io.File
import java.util.*

@Service
class SAImageService @Autowired constructor(
        private val projectSettings: ProjectSettings,
        private val sDatasetRepository: SDatasetRepository,
        @Lazy private val sDatasetService: SDatasetService,
        private val saImageRepository: SAImageRepository,
        private val sProjectRepository: SProjectRepository) : AbstractFileService() {

    fun loadImage(imageID: String, loadImageData: Boolean = true, asFormat: String = "png"): SImage {
        val image = saImageRepository.findById(imageID).orElseThrow { throw IllegalArgumentException("Image not found!") }

        if (image !is SImage)
            throw IllegalStateException("Not an image")

        if (loadImageData) {
            loadImageData(image, asFormat)

        }
        return image
    }

    fun loadImageData(image: SImage, asFormat: String = "png") {
        var data = ImageUtil.readImageAsBufferedImage(File(projectSettings.dir, image.path))
        image.width = data.width
        image.height = data.height

        // workaround for png to jpg conversion
        if (image.path.substringAfterLast(".") != asFormat)
            data = ImageUtil.prepareConvertImage(data)

        image.data = ImageUtil.imageToBase64(data, asFormat)
        image.fileExtension = asFormat
    }

    fun addImageToParent(imageID: String, parentID: String, format: String = "png"): Pair<SImage, SIHasImages> {
        return addImageToParentLoaded(loadImage(imageID, true) as SImage, findParent(parentID), format)
    }

    fun addImageToParentLoaded(image: SImage, parentID: String, format: String = "png"): Pair<SImage, SIHasImages> {
        return addImageToParentLoaded(image, findParent(parentID), format)
    }

    fun addImageToParentLoaded(image: SImage, parent: SIHasImages, format: String = "png"): Pair<SImage, SIHasImages> {
        if(image.path.isEmpty())
            image.path = image.name

        if(!image.path.endsWith(format)){
            if(image.path.matches(Regex(".*\\.[A-Za-z]{2,4}$"))){
                image.path = image.path.substringBeforeLast(".") + format
            }else
                image.path += ".$format"
        }

        val imageNameWithExtension = if (image.path.contains("/")) image.path.substringAfterLast("/") else image.path
        val imagePath = getUniqueFile(File(projectSettings.dir, parent.path), imageNameWithExtension)
        ImageUtil.writeBase64Img(image.data, imagePath, format)
        image.path = "${parent.path}${imagePath.name}"
        val imageResult = saImageRepository.save(image)

        parent.images.add(image)
        val parentResult = saveParent(parent)

        return Pair(imageResult, parentResult)
    }

    fun deleteImage(imageID: String, removeFromParent: Boolean = true) {
        deleteImage(saImageRepository.findById(imageID).orElseThrow { throw IllegalArgumentException("Image not found!") }, removeFromParent)
    }

    fun deleteImage(image: SAImage, removeFromParent: Boolean = true) {
        if (image !is SImage)
            throw IllegalArgumentException("Not an image!")

        if (removeFromParent)
            removeImageFromParent(image)

        saImageRepository.delete(image)
        File(projectSettings.dir, image.path).delete()
    }

    fun moveImageToParent(imageID: String, parentID: String): Pair<SImage, SIHasImages> {
        return moveImageToParent(imageID, findParent(parentID))
    }

    fun moveImageToParent(imageID: String, parent: SIHasImages): Pair<SImage, SIHasImages> {
        return moveImageToParent(loadImage(imageID, true) as SImage, parent)
    }

    fun moveImageToParent(image: SImage, parent: SIHasImages): Pair<SImage, SIHasImages> {
        deleteImage(image)
        return addImageToParentLoaded(image, parent)
    }

    fun updateImage(image: SImage): SAImage {
        val dbImage: SAImage? = saImageRepository.findById(image.id
                ?: "").orElseThrow { throw IllegalArgumentException("Image not found in db") }

        if (image.concurrencyCounter > dbImage!!.concurrencyCounter) {
            if (image.layers.isNotEmpty())
                image.hasLayerData = true

            return saImageRepository.save(image);
        } else
            throw IllegalArgumentException("Concurrency Error: new image = ${image.concurrencyCounter}; old image ${dbImage.concurrencyCounter}")
    }

    fun cloneImage(imageID: String, parentID: String): Pair<SImage, SIHasImages> {
        val image = loadImage(imageID, true)
        val parent = if (parentID.isEmpty()) findParentByImage(image) else findParent(parentID)
        return cloneImage(image, parent)
    }

    fun cloneImage(image: SImage, newParent: SIHasImages): Pair<SImage, SIHasImages> {
        image.id = null
        return addImageToParentLoaded(image, newParent)
    }

    fun addMultiPartImageToDataset(multipartFile: MultipartFile, datasetID: String): Pair<SImage, SIHasImages> {
        val dataset = sDatasetRepository.findById(datasetID).orElseThrow { throw IllegalArgumentException("Dataset not found!") }

        val imageName = multipartFile.originalFilename ?: "noName.png"

        val image = SImage()
        image.name = imageName.substringBeforeLast(".")
        image.data = String(Base64.getEncoder().encodeToString(multipartFile.bytes).toByteArray())
        image.fileExtension = "png"

        return addImageToParentLoaded(image, dataset)
    }

    fun createImageByPath(image: SImage, format: String = "png"): SImage {
        val path = image.path.replace("\\", "/")
        val pathArray = path.split("/")

        if (pathArray.size != 3) {
            throw IllegalArgumentException("Image Path not well formatted!")
        }

        val project = sProjectRepository.findByName(pathArray[0]).orElseThrow { throw IllegalArgumentException("Project not found ny path: ${pathArray[0]}") }
        println(pathArray[1])
        println("/^${project.path.replace("/", "\\/")}.*/")
        val dataset = sDatasetRepository.findByNameAndPathRegex(pathArray[1], "${project.path.replace("/", "")}.*").orElse(sDatasetService.createDataset(pathArray[1], project.id
                ?: "").first)

        return addImageToParentLoaded(image, dataset, format).first
    }

    fun createImage(image: SImage, parentID: String): Pair<SImage, SIHasImages> {
        val parent = findParent(parentID)

        val imageResult = saImageRepository.save(image)
        parent.images.add(image)
        val parentResult = saveParent(parent)

        return Pair(imageResult, parentResult)
    }

    /**
     * Removes Image or ImageGroup form Parent (Dataset or Imagegroup)
     */
    fun removeImageFromParent(image: SAImage): SIHasImages? {
        // removing from old dataset
        val parent = findParentByImage(image)
        val dBImage = parent.images.findLast { it.id == image.id }
        parent.images.remove(dBImage)
        return saveParent(parent)
    }

    /**
     * Returns a dataset or imagegroup
     */
    fun findParent(parentID: String): SIHasImages {
        val dataset = sDatasetRepository.findById(parentID)

        if (dataset.isPresent)
            return dataset.get()

        val imageGroup = saImageRepository.findById(parentID)

        if (imageGroup.isPresent && imageGroup.get() is SImageGroup)
            return imageGroup.get() as SImageGroup

        throw IllegalArgumentException("Parent with ID: $parentID not found")
    }

    /**
     * Returns a dataset or imagegroup
     */
    fun findParentByImage(image: SAImage): SIHasImages {
        val dataset = sDatasetRepository.findByImageID(ObjectId(image.id))

        if (dataset.isPresent)
            return dataset.get()

        val imageGroup = saImageRepository.findImageGroupByImageID(ObjectId(image.id))

        if (imageGroup.isPresent)
            return imageGroup.get() as SImageGroup

        throw IllegalArgumentException("Parent with containing image with id: ${image.id} not found")
    }

    /**
     * Saves a dataset or image group
     */
    fun saveParent(parent: SIHasImages): SIHasImages {
        return when (parent) {
            is SDataset -> sDatasetRepository.save(parent)
            is SImageGroup -> saImageRepository.save(parent)
            else -> throw IllegalArgumentException("Parent not recognized")
        }
    }
}