package eu.glatz.sifidraw.config

import eu.glatz.sifidraw.model.User
import eu.glatz.sifidraw.repository.UserRepository
import eu.glatz.sifidraw.service.LDAPService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Lazy
import org.springframework.security.authentication.AuthenticationProvider
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.Authentication
import org.springframework.security.core.AuthenticationException
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.stereotype.Component
import java.util.*


@Component
class LDAPDatabaseAuthenticationProvider @Autowired constructor(
        private val userRepository: UserRepository,
        private val ldapService: LDAPService,
        @Lazy private val bCryptPasswordEncoder: BCryptPasswordEncoder) : AuthenticationProvider {

    @Throws(AuthenticationException::class)
    override fun authenticate(authentication: Authentication): Authentication? {
        val name: String = authentication.name
        val password: String = authentication.credentials.toString()


        val applicationUser: User? = userRepository.findByName(name)


        if (applicationUser != null && applicationUser.localUser) {
            return if (bCryptPasswordEncoder.matches(password,applicationUser.password))
                UsernamePasswordAuthenticationToken(
                        name, bCryptPasswordEncoder.encode(password), ArrayList())
            else
                null
        } else if (applicationUser != null) {
            return if (ldapService.authenticate(name, password)) {
                UsernamePasswordAuthenticationToken(
                        name, bCryptPasswordEncoder.encode(password), ArrayList())
            } else {
                null
            }
        } else {
            if (ldapService.authenticate(name, password)) {
                val user = User();
                user.name = name
                user.localUser = false
                userRepository.save(user)

                return UsernamePasswordAuthenticationToken(
                        name, bCryptPasswordEncoder.encode(password), ArrayList())
            }
        }

        return null
    }

    override fun supports(authentication: Class<*>): Boolean {
        return authentication == UsernamePasswordAuthenticationToken::class.java
    }
}