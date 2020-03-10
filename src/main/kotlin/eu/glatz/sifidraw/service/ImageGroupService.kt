package eu.glatz.sifidraw.service

import eu.glatz.sifidraw.config.ProjectSettings
import eu.glatz.sifidraw.model.Image
import eu.glatz.sifidraw.model.ImageGroup
import eu.glatz.sifidraw.repository.ImageGroupRepository
import eu.glatz.sifidraw.repository.ImageRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import java.io.File
import java.nio.charset.Charset
import java.util.*
import java.util.UUID


@Service
class ImageGroupService @Autowired constructor(
        private val projectSettings: ProjectSettings,
        private val imageGroupRepository: ImageGroupRepository,
        private val imageRepository: ImageRepository,
        private val imageService: ImageService) : AbstractService() {

    fun createImageGroup(dataSetPath: String, groupName: String): ImageGroup? {

        val uuid = UUID.randomUUID()
        val randomUUIDString = uuid.toString()

        val imageGroupPath = File(dataSetPath, randomUUIDString);
        val absoluteImageGroupPath = File(projectSettings.dir, imageGroupPath.path)

        return if (absoluteImageGroupPath.exists())
            throw IllegalArgumentException("Folder exists")
        else {
            absoluteImageGroupPath.mkdirs();
            val fixedFolderPath = (imageGroupPath.path + if (!imageGroupPath.path.endsWith("/")) "/" else "").replace("\\", "/")
            val newImgGroup = ImageGroup(Base64.getEncoder().encodeToString(fixedFolderPath.toByteArray()), groupName);
            imageGroupRepository.save(newImgGroup)
        }
    }

    fun cloneImageGroup(imageGroupPath: String, datasetPath: String? = null): ImageGroup {
        val id = String(Base64.getEncoder().encodeToString(imageGroupPath.toByteArray()).toByteArray(), Charset.forName("UTF-8"))

        var fixedDatasetPath = datasetPath;

        if (fixedDatasetPath == null)
            fixedDatasetPath = if(imageGroupPath.endsWith("/")) imageGroupPath.substring(0,imageGroupPath.length-1).substringBeforeLast("/") else  imageGroupPath.substringBeforeLast("/")


        if (!groupExist(id))
            throw java.lang.IllegalArgumentException("Image Group not found!")

        val datasetFolder = File(projectSettings.dir, fixedDatasetPath)

        if (!datasetFolder.isDirectory)
            throw java.lang.IllegalArgumentException("Target dir not found!")

        val origGroup = imageGroupRepository.findById(id);

        if (!origGroup.isPresent)
            throw java.lang.IllegalArgumentException("Image Group not found in Database!")

        var newName = origGroup.get().name + "_Kopie"
        var i = 0
        while (imageGroupRepository.findByName(newName) != null) {
            newName = origGroup.get().name + "_Kopie_${i}"
            i++
        }

        val newGroup = createImageGroup(fixedDatasetPath, newName);
        val decodedGroupID = String(Base64.getDecoder().decode(newGroup!!.id), Charset.forName("UTF-8"))

        val images = imageService.getImagesOfFolder(imageGroupPath, false);

        images.forEach {
            val decodedID = String(Base64.getDecoder().decode(it.id), Charset.forName("UTF-8"))
            imageService.cloneImage(decodedID, decodedGroupID)
        }


        return getImageGroup(decodedGroupID, false)
    }

    fun addImageToGroup(imageGroup: ImageGroup, image: Image): Image {
        val groupDir = String(Base64.getDecoder().decode(imageGroup.id), Charset.forName("UTF-8"))
        return this.imageService.moveAndAddImageToPath(groupDir, image)
    }

    fun removeImageFromGroup(imageGroup: ImageGroup, image: Image): ImageGroup {
        val imgName = image.id.substringAfterLast("/")
        imageRepository.delete(image)
        return imageGroupRepository.save(imageGroup)
    }

    fun getImageGroup(imageGroupPath: String, loadImageData: Boolean, format : String = "png"): ImageGroup {
        val folder = File(projectSettings.dir, imageGroupPath);
        val fixedFolderPath = imageGroupPath + if (!imageGroupPath.endsWith("/")) "/" else ""
        if (folder.isDirectory) {
            val base64ID = String(Base64.getEncoder().encodeToString(fixedFolderPath.toByteArray()).toByteArray(), Charset.forName("UTF-8"))
            val imageGroup = imageGroupRepository.findById(base64ID).orElse(ImageGroup(base64ID, folder.name))
            imageGroup.images.addAll(imageService.getImagesOfFolder(fixedFolderPath, loadImageData,format))
            return imageGroup
        }
        throw java.lang.IllegalArgumentException("ImageGroup not found")
    }

    fun deleteImageGroup(imageGroupPath: String){
        val folder = File(projectSettings.dir, imageGroupPath);
        val fixedFolderPath = imageGroupPath + if (!imageGroupPath.endsWith("/")) "/" else ""

        if(folder.isDirectory) {
            val base64ID = String(Base64.getEncoder().encodeToString(fixedFolderPath.toByteArray()).toByteArray(), Charset.forName("UTF-8"))
            imageService.getDeleteImagesOfFolder(fixedFolderPath)
            folder.delete()

            val imageGroup = imageGroupRepository.findById(base64ID)
            if (!imageGroup.isPresent)
                return
            imageGroupRepository.delete(imageGroup.get())

        }
    }

    fun updateImageGroup(group: ImageGroup): ImageGroup {
        val dbGroup = imageGroupRepository.findById(group.id)

//        val images = group.images.map { imageService.updateImage(it) }

        if (dbGroup.isPresent) {
            if (dbGroup.get().concurrencyCounter + 1 == group.concurrencyCounter) {
                return imageGroupRepository.save(group);
//            savedGroup.images = images.toMutableList()
            } else
                throw IllegalArgumentException("Concurrency Error NEW group = ${group.concurrencyCounter}; old group ${dbGroup.get().concurrencyCounter}")
        } else
            return imageGroupRepository.save(group);
    }

    fun groupExist(groupPath: String): Boolean {
        val id = String(Base64.getDecoder().decode(groupPath), Charset.forName("UTF-8"))
        val imgFile = File(projectSettings.dir, id)
        return imgFile.isDirectory;
    }
}