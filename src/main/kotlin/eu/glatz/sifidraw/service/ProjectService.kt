package eu.glatz.sifidraw.service

import eu.glatz.sifidraw.model.Dataset
import eu.glatz.sifidraw.model.ProjectData
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.stereotype.Service
import java.io.File


@Service
@ConfigurationProperties(prefix = "settings")
class ProjectService : AbstractService() {

    lateinit var dir: String

    public fun getProjectData(): List<ProjectData> {
        val base = File(dir)

        val directories = base.list { current, name -> File(current, name).isDirectory }
        val projects = directories.map { dir -> ProjectData(dir) }

        projects.forEach {
            val f = File(base, it.id)
            print(f.absolutePath)
            val datasets = f.list { current, name -> File(current, name).isDirectory }.map { dir -> Dataset(dir) }
            it.datasets = datasets;

            datasets.forEach {
                val dataset =  File(f, it.id)
            }
        }

        return projects
    }
}
