package eu.glatz.sifidraw.service

import eu.glatz.sifidraw.config.ProjectSettings
import eu.glatz.sifidraw.model.Image
import eu.glatz.sifidraw.repository.ImageGroupRepository
import eu.glatz.sifidraw.repository.ImageRepository
import eu.glatz.sifidraw.util.ImageUtil
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
        val imageDir = String(Base64.getDecoder().decode(image.id), Charset.forName("UTF-8"))
        val imgName = imageDir.substringAfterLast("/")
        val newImageID = path + (if (!path.endsWith("/")) "/" else "") + imgName

        val imgFile = ImageUtil.findImage(projectSettings.dir, imageDir)

        FileUtils.moveFileToDirectory(imgFile, File(projectSettings.dir, path), true)

        imageRepository.delete(image)

        image.id = Base64.getEncoder().encodeToString(newImageID.toByteArray())
        return imageRepository.save(image)
    }
}