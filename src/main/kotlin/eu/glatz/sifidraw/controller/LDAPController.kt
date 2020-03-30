package eu.glatz.sifidraw.controller

import eu.glatz.sifidraw.model.LDAPEmployee
import eu.glatz.sifidraw.repository.LDAPEmployeeRepository
import eu.glatz.sifidraw.service.LDAPService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.*
import java.util.*


@CrossOrigin
@RestController
@RequestMapping("SifiDrawRest")
class LDAPController @Autowired constructor(
        private val ldapEmployeeRepository: LDAPEmployeeRepository,
        private val ldapService: LDAPService) {

    @GetMapping("/ldap/find/lastname/{lastName}")
    fun getImageGroupData(@PathVariable lastName: String): List<LDAPEmployee> {
        return ldapEmployeeRepository.findByLastName(lastName)
    }


    @GetMapping("/ldap/auth")
    fun auth(@RequestParam("uid") uid: Optional<String>, @RequestParam("pw") pw: Optional<String>): Boolean {

        if (uid.isPresent && pw.isPresent)
            return ldapService.authenticate(uid.get(), pw.get())

        return false
    }
}