package eu.glatz.sifidraw.config

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.stereotype.Component

@ConfigurationProperties(prefix = "settings")
@Component()
class ProjectSettings {
    lateinit var dir: String
}
