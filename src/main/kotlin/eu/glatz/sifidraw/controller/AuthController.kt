package eu.glatz.sifidraw.controller

import eu.glatz.sifidraw.model.User
import eu.glatz.sifidraw.repository.UserRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.core.Authentication
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController


@RestController
@RequestMapping()
class AuthController @Autowired constructor(
        private val userRepository: UserRepository,
        private val bCryptPasswordEncoder: BCryptPasswordEncoder) {


    @PostMapping("/user/sign-up")
    fun signUp(@RequestBody user: User) {
        user.password = bCryptPasswordEncoder.encode(user.password)
        userRepository.save(user)
    }

}