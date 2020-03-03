package eu.glatz.sifidraw.model

class UserSettings constructor(var id: String) {
    var defaultLayerSettings: MutableList<Layer> = mutableListOf(Layer())
}