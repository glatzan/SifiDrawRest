package eu.glatz.sifidraw.repository

import eu.glatz.sifidraw.model.SDataset
import org.bson.types.ObjectId
import org.springframework.data.mongodb.repository.MongoRepository
import org.springframework.data.mongodb.repository.Query
import java.util.*

interface SDatasetRepository : MongoRepository<SDataset, String> {
    @Query(value = "{ 'images.\$id' : ?0 }")
    fun findByImageID(imageID: ObjectId): Optional<SDataset>
}