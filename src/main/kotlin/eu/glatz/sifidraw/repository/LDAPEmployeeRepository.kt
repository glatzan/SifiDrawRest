package eu.glatz.sifidraw.repository

import eu.glatz.sifidraw.model.LDAPEmployee
import org.springframework.data.ldap.repository.LdapRepository

interface LDAPEmployeeRepository : LdapRepository<LDAPEmployee> {
    fun findByLastName(lastName: String): List<LDAPEmployee>
}