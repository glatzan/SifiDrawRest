package eu.glatz.sifidraw.service

import eu.glatz.sifidraw.config.ProjectSettings
import eu.glatz.sifidraw.model.SAImage
import eu.glatz.sifidraw.model.SImage
import eu.glatz.sifidraw.repository.SAImageRepository
import org.bson.types.ObjectId
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import java.io.File

@Service
class SAImageService @Autowired constructor(
        private val sequenceGeneratorService: SequenceGeneratorService,
        private val projectSettings: ProjectSettings,
        private val imageRepository: SAImageRepository) : AbstractService() {

    fun createImage(name: String, path: String): SImage {
        val baseDir = File(projectSettings.dir)

        val image = SImage()
        image.name = name
        image.path = path

        return imageRepository.save(image)
    }

}