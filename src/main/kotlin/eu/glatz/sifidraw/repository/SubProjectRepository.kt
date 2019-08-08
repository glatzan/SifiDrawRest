package eu.glatz.sifidraw.repository

import eu.glatz.sifidraw.model.SubProject
import org.springframework.data.mongodb.repository.MongoRepository

interface SubProjectRepository : MongoRepository<SubProject, String> {
    fun findByName(name: String): List<SubProject>
}
