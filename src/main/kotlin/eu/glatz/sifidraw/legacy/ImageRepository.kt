package eu.glatz.sifidraw.legacy

import eu.glatz.sifidraw.legacy.model.Image
import org.springframework.data.mongodb.repository.MongoRepository

interface ImageRepository : MongoRepository<Image, String> {
}