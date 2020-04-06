package eu.glatz.sifidraw.config

import com.google.gson.Gson
import eu.glatz.sifidraw.model.SDataset
import eu.glatz.sifidraw.model.SImage
import eu.glatz.sifidraw.model.SProject
import eu.glatz.sifidraw.model.User
import eu.glatz.sifidraw.repository.SAImageRepository
import eu.glatz.sifidraw.repository.SDatasetRepository
import eu.glatz.sifidraw.repository.SProjectRepository
import eu.glatz.sifidraw.repository.UserRepository
import eu.glatz.sifidraw.service.*
import org.springframework.beans.factory.InitializingBean
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.stereotype.Component


@Component
class InitializingBean @Autowired constructor(
        private val userRepository: UserRepository,
        private val userService: UserService,
        private val bCryptPasswordEncoder: BCryptPasswordEncoder,
        private val sProjectService: SProjectService,
        private val sDatasetService: SDatasetService,
        private val sProjectRepository: SProjectRepository,
        private val saImageService: SAImageService,
        private val sDatasetRepository: SDatasetRepository,
        private val dbConverterService: DBConverterService,
        private val saImageRepository: SAImageRepository) : InitializingBean {

    @Throws(Exception::class)
    override fun afterPropertiesSet() {
        println("Starting..")
        val user = userRepository.findByName("admin")
        if (user == null) {
            println("creating new user")
            val newUser = User()
            newUser.name = "admin"
            newUser.password = bCryptPasswordEncoder.encode("admin")
            newUser.localUser = true
            userService.createNewUser(newUser)
        }

        dbConverterService.sync()

    }

}