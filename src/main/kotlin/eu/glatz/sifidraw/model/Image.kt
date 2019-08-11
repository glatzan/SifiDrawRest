package eu.glatz.sifidraw.model

class Image {

    var id: String
    var name: String
    var data: String
    var layers: MutableList<Layer>

    constructor(id: String, name: String) {
        this.id = id
        this.name = name
        this.data = ""
        layers = mutableListOf()
    }
}
