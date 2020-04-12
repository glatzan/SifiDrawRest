package eu.glatz.sifidraw.repository

import eu.glatz.sifidraw.model.SDataset
import eu.glatz.sifidraw.model.SProject
import org.bson.types.ObjectId
import org.springframework.data.mongodb.repository.MongoRepository
import org.springframework.data.mongodb.repository.Query
import java.util.*

interface SProjectRepository : MongoRepository<SProject, String> {

    fun findByPath(path: String) : Optional<SProject>

    fun findByName(name: String) : Optional<SProject>

    fun findByDatasets(datasetID: String): Optional<SProject>

}