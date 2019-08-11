package eu.glatz.sifidraw.repository

import eu.glatz.sifidraw.model.Dataset
import eu.glatz.sifidraw.model.Image
import org.springframework.data.mongodb.repository.MongoRepository

interface ImageRepository : MongoRepository<Image, String> {
}
