package eu.glatz.sifidraw.service

import org.springframework.beans.factory.annotation.Value
import org.springframework.ldap.core.LdapTemplate
import org.springframework.ldap.query.LdapQueryBuilder
import org.springframework.stereotype.Service

@Service
class LDAPService constructor(
        private val ldapTemplate: LdapTemplate) {

    @Value("\${ldap.peopleDn}")
    private lateinit var peopleBaseDN : String

    @Value("\${ldap.personDn}")
    private lateinit var personDn : String

    fun authenticate(uuid: String, password: String): Boolean {
        try {
            ldapTemplate.authenticate(LdapQueryBuilder.query().base("ou=people,dc=ukl,dc=uni-freiburg,dc=de").where(personDn).`is`(uuid), password)
        } catch (e: Exception) {
            return false
        }
        return true
    }

}