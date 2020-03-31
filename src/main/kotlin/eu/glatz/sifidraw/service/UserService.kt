package eu.glatz.sifidraw.service

import eu.glatz.sifidraw.model.User
import eu.glatz.sifidraw.repository.UserRepository
import org.springframework.stereotype.Service

@Service
class UserService constructor(
        private val sequenceGeneratorService: SequenceGeneratorService,
        private val userRepository: UserRepository) {

    fun createNewUser(user: User): User {
        user.id = sequenceGeneratorService.generateSequence(User.SEQUENCE_NAME)
        return userRepository.save(user)
    }
}
