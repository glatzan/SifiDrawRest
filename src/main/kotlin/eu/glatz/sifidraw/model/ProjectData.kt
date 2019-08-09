package eu.glatz.sifidraw.model

class ProjectData{
    var id: String
    var datasets : List<Dataset>

    constructor(id : String){
        this.id = id
        this.datasets = listOf()
    }
}
