package eu.glatz.sifidraw.service

import eu.glatz.sifidraw.model.User
import eu.glatz.sifidraw.repository.UserRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.stereotype.Service

@Service
class UserDetailsServiceImpl @Autowired constructor(
        private val userRepository: UserRepository) : UserDetailsService {

    @Throws(UsernameNotFoundException::class)
    override fun loadUserByUsername(username: String?): UserDetails? {
        username ?: throw UsernameNotFoundException("")
        val applicationUser: User? = userRepository.findByName(username)
                ?: throw UsernameNotFoundException(username)
        return org.springframework.security.core.userdetails.User(applicationUser?.name, applicationUser?.password, emptyList())
    }
}