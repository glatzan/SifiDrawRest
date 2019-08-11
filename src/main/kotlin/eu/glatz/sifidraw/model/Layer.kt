package eu.glatz.sifidraw.model

class Layer {

    var id: Int = 0
    var lines: Array<Array<Point>> = arrayOf(arrayOf<Point>())

    var size : Int = 1
    var color : String = "#ffffff"
}
