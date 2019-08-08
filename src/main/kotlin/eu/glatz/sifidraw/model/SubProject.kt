package eu.glatz.sifidraw.model

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import org.springframework.data.annotation.Id

@JsonIgnoreProperties(ignoreUnknown = true)
class SubProject() {

    @Id
    lateinit var id : String

    var name: String? = null
}
