package eu.glatz.sifidraw.model

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import org.springframework.data.annotation.Id

class Dataset {

    var id: String
    var images = mutableListOf<Image>()

    constructor(id: String) {
        this.id = id
    }
}
