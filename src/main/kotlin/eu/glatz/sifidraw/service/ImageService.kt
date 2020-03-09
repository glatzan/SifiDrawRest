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

    fun moveAndAddImageToPath(path: String, image: Image): Image {
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

    fun addNewImageToPath(image: Image, type: String): Image {
        var imagePath = String(Base64.getDecoder().decode(image.id), Charset.forName("UTF-8"))
        imagePath += if (imagePath.endsWith(type)) "" else type

        val basePath = imagePath.substringBeforeLast("/") + "/"
        val basePathFile = File(projectSettings.dir, basePath)
        if (!basePathFile.exists())
            basePathFile.mkdirs()

        var targetPathFile = File(projectSettings.dir, imagePath);
        if (targetPathFile.exists()) {
            val uuid = UUID.randomUUID()
            val randomUUIDString = uuid.toString()
            val newPath = basePath + randomUUIDString + "." + imagePath.substringAfterLast(".")
            image.id = Base64.getEncoder().encodeToString(newPath.toByteArray())
            targetPathFile = File(projectSettings.dir, newPath);
        }

        ImageUtil.writeBase64Img(image.data, targetPathFile)
        return imageRepository.save(image)
    }

    fun getImage(imagePath: String, loadImageData: Boolean, format: String = "png"): Image {
        val id = Base64.getEncoder().encodeToString(imagePath.toByteArray())
        val img = imageRepository.findById(id).orElse(Image(id, imagePath.substringAfterLast("/").substringBeforeLast(".")))

        if (loadImageData) {
            val readImg = ImageUtil.readImageAsBufferedImage(File(projectSettings.dir, imagePath))
            img.width = readImg.width
            img.height = readImg.height
            img.data = ImageUtil.imageToBase64(readImg, format)
            img.fileExtension = format
        }

        return img
    }

    fun imageExist(imagePath: String): Boolean {
        val id = String(Base64.getDecoder().decode(imagePath), Charset.forName("UTF-8"))
        val imgFile = File(projectSettings.dir, id)
        return imgFile.isFile;
    }

    fun deleteImage(imagePath: String) {
        val id = Base64.getEncoder().encodeToString(imagePath.toByteArray())
        val imgFile = File(projectSettings.dir, imagePath)
        if (imgFile.isFile) {
            imgFile.delete();
            val obj = imageRepository.findById(id);
            if (!obj.isPresent)
                return
            imageRepository.delete(obj.get())
        }
    }

    fun getDeleteImagesOfFolder(folderPath: String) {
        val folder = File(projectSettings.dir, folderPath);
        val fixedFolderPath = folderPath + if (!folderPath.endsWith("/")) "/" else ""
        val resultList = mutableListOf<Image>()
        val images = folder.list { _, name -> name.matches(Regex(".*((.jpg)|(.png)|(.tif))")) } ?: return
        for (img in images) {
            deleteImage("${folderPath}$img")
        }
    }

    fun getImagesOfFolder(folderPath: String, loadImageData: Boolean, format: String = "png"): List<Image> {
        val folder = File(projectSettings.dir, folderPath);
        val fixedFolderPath = folderPath + if (!folderPath.endsWith("/")) "/" else ""
        val resultList = mutableListOf<Image>()
        val images = folder.list { _, name -> name.matches(Regex(".*((.jpg)|(.png)|(.tif))")) } ?: return resultList
        for (img in images) {
            resultList.add(getImage("${folderPath}$img", loadImageData, format))
        }

        resultList.sortBy { it.name }
        return resultList
    }

    fun updateImage(image: Image) : Image{
        val dbImage = imageRepository.findById(image.id).orElseThrow { IllegalAccessException("Image not found!") };

        if (dbImage.concurrencyCounter + 1 == image.concurrencyCounter)
            return imageRepository.save(image);
        else
            throw IllegalArgumentException("Concurrency Error NEW Image = ${image.concurrencyCounter}; old Image ${dbImage.concurrencyCounter}")
    }
}