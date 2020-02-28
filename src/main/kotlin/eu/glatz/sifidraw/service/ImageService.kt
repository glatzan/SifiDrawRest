package eu.glatz.sifidraw.service

import eu.glatz.sifidraw.config.ProjectSettings
import eu.glatz.sifidraw.model.Image
import eu.glatz.sifidraw.repository.ImageGroupRepository
import eu.glatz.sifidraw.repository.ImageRepository
import org.apache.commons.io.FileUtils
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import java.io.File
import java.nio.charset.Charset
import java.util.*

@Service
class ImageService @Autowired constructor(
        private val projectSettings: ProjectSettings,
        private val imageGroupRepository: ImageGroupRepository,
        private val imageRepository: ImageRepository) : AbstractService() {

    fun addImageToPath(path: String, image: Image): Image {
        val imagePath = String(Base64.getDecoder().decode(image.id), Charset.forName("UTF-8"))
        val imgName = imagePath.substringAfterLast("/")

        val validPath = path + (if (!path.endsWith("/")) "/" else "")
        var newImageID = validPath + imgName

        val imageFile = File(projectSettings.dir, imagePath);
        var newImageFile = File(projectSettings.dir, newImageID);

        if (newImageFile.exists()) {
            val uuid = UUID.randomUUID()
            val randomUUIDString = uuid.toString()
            newImageID = validPath + randomUUIDString + "." + imgName.substringAfterLast(".")
            newImageFile = File(projectSettings.dir, newImageID);
        }

        FileUtils.moveFile(imageFile, newImageFile)

        imageRepository.delete(image)

        image.id = Base64.getEncoder().encodeToString(newImageID.toByteArray())
        return imageRepository.save(image)
    }
}