package eu.glatz.sifidraw.config

import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.ldap.core.ContextSource
import org.springframework.ldap.core.LdapTemplate
import org.springframework.ldap.core.support.LdapContextSource


@Configuration
class LDAPConfig {

    @Value("\${ldap.url}")
    private lateinit var ldapURL: String

    @Bean
    fun contextSource(): ContextSource {
        val ldapContextSource = LdapContextSource()
        ldapContextSource.setUrl(ldapURL)
        return ldapContextSource
    }

    @Bean
    fun ldapTemplate(contextSource: ContextSource): LdapTemplate {
        return LdapTemplate(contextSource)
    }
}