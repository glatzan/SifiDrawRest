package eu.glatz.sifidraw.repository

import eu.glatz.sifidraw.model.SDataset
import eu.glatz.sifidraw.model.SProject
import org.bson.types.ObjectId
import org.springframework.data.mongodb.repository.MongoRepository
import org.springframework.data.mongodb.repository.Query
import java.util.*

interface SDatasetRepository : MongoRepository<SDataset, String> {
    @Query(value = "{ 'images.\$id' : ?0 }")
    fun findByImageID(imageID: ObjectId): Optional<SDataset>

    fun findByPath(path: String): Optional<SDataset>

    fun findByName(path: String): Optional<SDataset>

    fun findByNameAndPathRegex(name: String, path: String): Optional<SDataset>
}