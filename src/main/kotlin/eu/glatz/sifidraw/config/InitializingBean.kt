package eu.glatz.sifidraw.config

import eu.glatz.sifidraw.model.User
import eu.glatz.sifidraw.repository.UserRepository
import org.springframework.beans.factory.InitializingBean
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.stereotype.Component


@Component
class InitializingBean @Autowired constructor(
        private val userRepository: UserRepository,
        private val bCryptPasswordEncoder: BCryptPasswordEncoder) : InitializingBean {

    @Throws(Exception::class)
    override fun afterPropertiesSet() {
        println("Starting..")
        val user = userRepository.findByName("admin")
        if (user == null) {
            println("creating new user")
            val newUser = User()
            newUser.name = "admin"
            newUser.password = bCryptPasswordEncoder.encode("admin")
            userRepository.save(newUser)
        }
    }

}