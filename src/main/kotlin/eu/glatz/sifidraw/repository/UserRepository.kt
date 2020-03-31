package eu.glatz.sifidraw.repository

import eu.glatz.sifidraw.model.User
import org.springframework.data.mongodb.repository.MongoRepository

interface UserRepository : MongoRepository<User, Long> {
    fun findByName(name: String): User?
}