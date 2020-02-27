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

        val datasetDir = File(dataSetPath, randomUUIDString);
        val absoluteDatasetDir = File(projectSettings.dir, datasetDir.path)

        return if (absoluteDatasetDir.exists())
            throw IllegalArgumentException("Folder exists")
        else {
            absoluteDatasetDir.mkdirs();
            val newImgGroup = ImageGroup(Base64.getEncoder().encodeToString(datasetDir.path.replace("\\", "/").toByteArray()), groupName);
            imageGroupRepository.save(newImgGroup)
        }
    }

    fun addImageToGroup(imageGroup: ImageGroup, image: Image): Image {
        val groupDir = String(Base64.getDecoder().decode(imageGroup.id), Charset.forName("UTF-8"))
        return this.imageService.addImageToPath(groupDir, image)
    }

    fun removeImageFromGroup(imageGroup: ImageGroup, image: Image): ImageGroup {
        val imgName = image.id.substringAfterLast("/")
        imageRepository.delete(image)
        return imageGroupRepository.save(imageGroup)
    }
}