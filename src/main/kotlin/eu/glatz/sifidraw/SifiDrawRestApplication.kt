package eu.glatz.sifidraw

import eu.glatz.sifidraw.config.LDAPConfig
import eu.glatz.sifidraw.config.SecurityConfig
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.autoconfigure.mongo.embedded.EmbeddedMongoAutoConfiguration
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.boot.runApplication
import org.springframework.context.annotation.Import
import org.springframework.data.ldap.repository.config.EnableLdapRepositories


@SpringBootApplication(exclude = [EmbeddedMongoAutoConfiguration::class, SecurityAutoConfiguration::class])
@EnableConfigurationProperties
@EnableLdapRepositories
@Import(SecurityConfig::class, LDAPConfig::class)
class SifiDrawRestApplication

fun main(args: Array<String>) {
    runApplication<SifiDrawRestApplication>(*args)
}
