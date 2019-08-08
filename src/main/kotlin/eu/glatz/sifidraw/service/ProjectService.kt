package eu.glatz.sifidraw.service

import eu.glatz.sifidraw.model.ProjectData
import eu.glatz.sifidraw.model.SubProject
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.stereotype.Service
import java.io.File
import java.nio.file.Files
import java.io.FilenameFilter


@ConfigurationProperties(prefix = "settings")
@Service
class ProjectService : AbstractService() {

    lateinit var baseDir: String

//    public fun getProjectData(): List<ProjectData> {
//        val file = F ile(baseDir)
//        val directories = file.list { current, name -> File(current, name).isDirectory }
//        val projects = directories.mapIndexedNotNull { index, dir -> ProjectData(index.toLong(), dir) }
//
//        projects.forEach {
//            val f = File(baseDir, it.name)
//            print(f.absolutePath )
//            val subProjects = f.list { current, name -> File(current, name).isDirectory }.mapIndexedNotNull { index, dir -> SubProject(index.toLong(), dir) }
//            it.subProjects = subProjects;
//        }
//
//        return projects
//    }
}
