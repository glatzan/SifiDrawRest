package eu.glatz.sifidraw.repository

import eu.glatz.sifidraw.model.UserSettings
import org.springframework.data.mongodb.repository.MongoRepository

interface UserSettingsRepository : MongoRepository<UserSettings, String> {
}