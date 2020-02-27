package eu.glatz.sifidraw.model

class Image(id: String, name: String) : IImage(id, name) {
    var data: String = ""
    var layers: MutableList<Layer> = mutableListOf()
}
