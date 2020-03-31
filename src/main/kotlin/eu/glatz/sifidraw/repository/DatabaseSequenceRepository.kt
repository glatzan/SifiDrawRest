package eu.glatz.sifidraw.repository

import eu.glatz.sifidraw.model.DatabaseSequence
import org.springframework.data.mongodb.repository.MongoRepository

interface DatabaseSequenceRepository : MongoRepository<DatabaseSequence, String> {
}