package eu.glatz.sifidraw.model

import org.springframework.data.mongodb.core.mapping.DBRef
import org.springframework.data.mongodb.core.mapping.Document

@Document(collection = "SImages")
class SImageGroup : SAImage() {

    @DBRef
    public var images = mutableListOf<SAImage>();

    init {
        type = "group"
    }
}