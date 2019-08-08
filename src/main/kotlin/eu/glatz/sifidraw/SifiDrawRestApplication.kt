package eu.glatz.sifidraw

import eu.glatz.sifidraw.config.DatabaseConfig
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.autoconfigure.mongo.embedded.EmbeddedMongoAutoConfiguration
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.boot.runApplication
import org.springframework.context.annotation.Import

@SpringBootApplication(exclude = [EmbeddedMongoAutoConfiguration::class])
@EnableConfigurationProperties
@Import(DatabaseConfig::class)
class SifiDrawRestApplication

fun main(args: Array<String>) {
    runApplication<SifiDrawRestApplication>(*args)
}
