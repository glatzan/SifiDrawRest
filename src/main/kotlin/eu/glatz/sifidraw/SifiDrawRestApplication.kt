package eu.glatz.sifidraw

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.boot.runApplication
import org.springframework.context.annotation.Import

@SpringBootApplication
@EnableConfigurationProperties
class SifiDrawRestApplication

fun main(args: Array<String>) {
    runApplication<SifiDrawRestApplication>(*args)
}
