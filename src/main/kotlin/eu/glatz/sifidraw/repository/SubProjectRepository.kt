package eu.glatz.sifidraw.repository

import eu.glatz.sifidraw.model.Dataset
import org.springframework.data.mongodb.repository.MongoRepository

interface DatasetRepository : MongoRepository<Dataset, String> {
}
