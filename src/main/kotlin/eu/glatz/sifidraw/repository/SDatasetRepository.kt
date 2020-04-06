package eu.glatz.sifidraw.repository

import eu.glatz.sifidraw.model.SDataset
import eu.glatz.sifidraw.model.SProject
import org.bson.types.ObjectId
import org.springframework.data.mongodb.repository.MongoRepository

interface SDatasetRepository : MongoRepository<SDataset, String> {
}