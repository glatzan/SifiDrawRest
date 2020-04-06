package eu.glatz.sifidraw.service

import eu.glatz.sifidraw.config.ProjectSettings
import eu.glatz.sifidraw.model.SAImage
import eu.glatz.sifidraw.model.SDataset
import eu.glatz.sifidraw.model.SImage
import eu.glatz.sifidraw.repository.SAImageRepository
import eu.glatz.sifidraw.util.ImageUtil
import org.apache.commons.io.FileUtils
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import java.io.File

@Service
class SAImageService @Autowired constructor(
        private val sequenceGeneratorService: SequenceGeneratorService,
        private val projectSettings: ProjectSettings,
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

    fun saveImage(image: SAImage): SAImage {
        val dbImage: SAImage? = saImageRepository.findById(image.id ?: "").orElse(null)

        return if (dbImage != null) {
            if (image.concurrencyCounter < dbImage.concurrencyCounter)
                saImageRepository.save(image);
            else
                throw IllegalArgumentException("Concurrency Error: new image = ${image.concurrencyCounter}; old image ${dbImage.concurrencyCounter}")
        } else {
            imageRepository.save(image)
        }
    }

    fun moveImageToDataset(image: SAImage, dataset: SDataset): SAImage {
        //todo remove from old dataset
        val newImagePath = getUniqueFile(File(projectSettings.dir, dataset.path), File(image.path).name)
        FileUtils.moveFile(File(projectSettings.dir, image.path), newImagePath)
        image.path = "${dataset.path}${newImagePath.name}/"
        return saImageRepository.save(image)
    }


    fun createImage(name: String, path: String): SImage {
        val baseDir = File(projectSettings.dir)

        val image = SImage()
        image.name = name
        image.path = path

        return imageRepository.save(image)
    }


}