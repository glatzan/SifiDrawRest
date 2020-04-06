package eu.glatz.sifidraw.service

import eu.glatz.sifidraw.config.ProjectSettings
import eu.glatz.sifidraw.model.SDataset
import eu.glatz.sifidraw.repository.SDatasetRepository
import org.bson.types.ObjectId
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import java.io.File

@Service
class SDatasetService @Autowired constructor(
        private val sequenceGeneratorService: SequenceGeneratorService,
        private val projectSettings: ProjectSettings,
        private val datasetRepository: SDatasetRepository) : AbstractService() {

    fun createDataset(name: String, path: String): SDataset {
        val baseDir = File(projectSettings.dir)

        val newDataset = SDataset()
        newDataset.name = name
        newDataset.path = path

        return datasetRepository.save(newDataset)
    }

}