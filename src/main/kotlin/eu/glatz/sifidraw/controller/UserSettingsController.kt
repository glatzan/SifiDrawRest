package eu.glatz.sifidraw.controller

import eu.glatz.sifidraw.model.UserSettings
import eu.glatz.sifidraw.repository.UserSettingsRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.core.Authentication
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.web.bind.annotation.*

@CrossOrigin
@RequestMapping("SifiDrawRest")
class UserSettingsController @Autowired constructor(
        private val userSettingsRepository: UserSettingsRepository) {

    @PostMapping("/user/settings/update")
    fun updateUserSettings(@RequestBody userSettings: UserSettings) {
        val auth: Authentication = SecurityContextHolder.getContext().authentication
        if (auth.principal == userSettings.id) {
            println("same user")
            userSettingsRepository.save(userSettings)
        } else {
            throw IllegalArgumentException("Is not the current user!")
        }
    }

    @GetMapping("/user/settings/{id}")
    fun getUserSettings(@PathVariable id: String): UserSettings {
        return userSettingsRepository.findById(id).orElse(UserSettings(id))
    }
}