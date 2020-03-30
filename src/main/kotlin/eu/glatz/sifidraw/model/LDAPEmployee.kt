package eu.glatz.sifidraw.model

import com.fasterxml.jackson.annotation.JsonIgnore
import org.springframework.ldap.odm.annotations.Attribute
import org.springframework.ldap.odm.annotations.Entry
import org.springframework.ldap.odm.annotations.Id
import javax.naming.Name

@Entry(objectClasses = ["person", "fw1Person", "top", "uklfrPerson"], base = "ou=people,dc=ukl,dc=uni-freiburg,dc=de")
open class LDAPEmployee {

    @Id
    @JsonIgnore
    var dn: Name? = null

    /**
     * Full name
     */
    @Attribute(name = "cn")
    var fullName: String? = null

    /**
     * Title e.g. Dr.
     */
    @Attribute(name = "personalTitle")
    var title: String? = null

    /**
     * Login name
     */
    @Attribute(name = "uid")
    var uid: String? = null

    /**
     * last name
     */
    @Attribute(name = "sn")
    var lastName: String? = null

    /**
     * Employee Number
     */
    @Attribute(name = "employeeNumber")
    var employeeNumber: String? = null

    /**
     * First name
     */
    @Attribute(name = "givenName")
    var firstName: String? = null

    /**
     * Email
     */
    @Attribute(name = "mail")
    var mail: String? = null

    /**
     * phone
     */
    @Attribute(name = "telephonenumber")
    var phone: String? = null

    /**
     * PAger
     */
    @Attribute(name = "pager")
    var pager: String? = null

    /**
     * Clinical Role. e.g. consultant
     */
    @Attribute(name = "title")
    var clinicRole: String? = null

    /**
     * Name of Organization
     */
    @Attribute(name = "ou")
    var organization: String? = null

    /**
     * SEX, 1 = male, > 1 female
     */
    @Attribute(name = "uklfrPersonType")
    var sex: String? = null
}