package eu.glatz.sifidraw.model

class ProjectData{
    var id: Long
    var name: String
    var subProjects : List<SubProject>

    constructor(id : Long, name : String){
        this.id = id
        this.name = name
        this.subProjects = listOf()
    }
}
