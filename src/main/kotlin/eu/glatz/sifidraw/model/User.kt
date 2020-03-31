package eu.glatz.sifidraw.model

import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document

@Document(collection = "user")
class User {


    companion object{
        @Transient
        @JvmStatic
        val SEQUENCE_NAME: String = "users_sequence"
    }

    @Id
    var id: Long = 0
    var name: String = ""
    var password: String = ""
    var valToken: String = ""
    var localUser = true
}