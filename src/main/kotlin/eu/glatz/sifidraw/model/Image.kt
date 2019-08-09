package eu.glatz.sifidraw.model

class Image {

    var name: String
    var data = byteArrayOf()

    constructor(name: String) {
        this.name = name
    }
}
