package eu.glatz.sifidraw.repository

import eu.glatz.sifidraw.model.SAImage
import eu.glatz.sifidraw.model.SImageGroup
import eu.glatz.sifidraw.model.SProject
import org.bson.types.ObjectId
import org.springframework.data.mongodb.repository.MongoRepository
import org.springframework.data.mongodb.repository.Query
import java.util.*

interface SAImageRepository : MongoRepository<SAImage, String> {
    @Query(value = "{ 'images.\$id' : ?0 }")
    fun findImageGroupByImageID(imageID: ObjectId): Optional<SImageGroup>
    fun findByPath(path: String) : Optional<SAImage>
}