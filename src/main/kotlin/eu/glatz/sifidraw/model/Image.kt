package eu.glatz.sifidraw.model

import org.springframework.data.annotation.Transient

class Image(id: String, name: String) : IImage(id, name) {
    var layers: MutableList<Layer> = mutableListOf()
    @Transient
    override var type: String = "img"
    @Transient
    var data: String = ""
}
