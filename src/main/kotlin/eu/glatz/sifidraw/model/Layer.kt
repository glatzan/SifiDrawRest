package eu.glatz.sifidraw.model

class Layer {

    var id: String = "1"
    var name: String = "1"
    var lines: Array<Array<Point>> = arrayOf(arrayOf<Point>())

    var size: Int = 1
    var color: String = "#ffffff"

    var type: Int = 1

    var interpolationPointDistance: Int = if(type == 0) 0 else 10
}
