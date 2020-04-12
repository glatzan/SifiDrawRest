package eu.glatz.sifidraw.legacy

import eu.glatz.sifidraw.legacy.model.ImageGroup
import org.springframework.data.mongodb.repository.MongoRepository

interface ImageGroupRepository  : MongoRepository<ImageGroup, String> {
    fun findByName(name: String): ImageGroup?
}