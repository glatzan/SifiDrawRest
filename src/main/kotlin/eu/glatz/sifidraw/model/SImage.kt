package eu.glatz.sifidraw.model

import org.springframework.data.annotation.Transient
import org.springframework.data.mongodb.core.mapping.Document

@Document(collection = "SImage")
class SImage : SAImage() {

    var layers: MutableList<Layer> = mutableListOf()

    @Transient
    var data: String = ""

    @Transient
    var fileExtension: String = ""

    var width = 0

    var height = 0

    var hasLayerData = false

    init {
        type = "img"
    }
}