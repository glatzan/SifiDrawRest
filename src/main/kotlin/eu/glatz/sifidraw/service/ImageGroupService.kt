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

    fun addImageToGroup(imageGroup: ImageGroup, image: Image): Image {
        val groupDir = String(Base64.getDecoder().decode(imageGroup.id), Charset.forName("UTF-8"))
        return this.imageService.moveAndAddImageToPath(groupDir, image)
    }

    fun removeImageFromGroup(imageGroup: ImageGroup, image: Image): ImageGroup {
        val imgName = image.id.substringAfterLast("/")
        imageRepository.delete(image)
        return imageGroupRepository.save(imageGroup)
    }

    fun getImageGroup(imageGroupPath: String, loadImageData: Boolean): ImageGroup {
        val folder = File(projectSettings.dir, imageGroupPath);
        val fixedFolderPath = imageGroupPath + if (!imageGroupPath.endsWith("/")) "/" else ""
        if (folder.isDirectory) {
            val base64ID = String(Base64.getEncoder().encodeToString(fixedFolderPath.toByteArray()).toByteArray(), Charset.forName("UTF-8"))
            val imageGroup = imageGroupRepository.findById(base64ID).orElse(ImageGroup(base64ID, folder.name))
            imageGroup.images.addAll(imageService.getImagesOfFolder(fixedFolderPath, loadImageData))
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
            val imageGroup = imageGroupRepository.findById(base64ID)
            if (!imageGroup.isPresent)
                return
            imageGroupRepository.delete(imageGroup.get())
        }
    }
}