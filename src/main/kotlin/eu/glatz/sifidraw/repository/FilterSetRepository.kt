package eu.glatz.sifidraw.repository

import eu.glatz.sifidraw.model.FilterSet
import eu.glatz.sifidraw.model.User
import org.springframework.data.mongodb.repository.MongoRepository

interface FilterSetRepository : MongoRepository<FilterSet, Long> {
}