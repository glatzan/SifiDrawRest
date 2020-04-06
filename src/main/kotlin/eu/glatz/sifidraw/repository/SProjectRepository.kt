package eu.glatz.sifidraw.repository

import eu.glatz.sifidraw.model.SProject
import org.bson.types.ObjectId
import org.springframework.data.mongodb.repository.MongoRepository

interface SProjectRepository : MongoRepository<SProject, String> {
    fun findByPath(path: String)
}