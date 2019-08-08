package eu.glatz.sifidraw.config

import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import java.io.File

@Configuration
@ConfigurationProperties(prefix = "database")
class DatabaseConfig {

    lateinit var dir: String
    lateinit var host: String
    var port: Int = 5001


    @Bean
    public fun mongoInMemory(): MongoInMemory {
        return MongoInMemory(port, host, File(dir))
    }
}
