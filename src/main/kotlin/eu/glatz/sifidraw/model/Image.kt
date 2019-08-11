package eu.glatz.sifidraw.model

class Image {

    var id: String
    var data : String
    var layers : MutableList<Layer>

    constructor(id: String) {
        this.id = id
        this.data = ""
        layers = mutableListOf()
    }
}
