package eu.glatz.sifidraw.repository

import eu.glatz.sifidraw.model.User
import org.springframework.data.mongodb.repository.MongoRepository

interface UserRepository : MongoRepository<User, Int> {
    fun findByName(name: String): User?
}