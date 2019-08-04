package eu.glatz.sifidraw.service

import eu.glatz.sifidraw.model.ProjectData
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.stereotype.Service
import java.io.File
import java.nio.file.Files
import java.io.FilenameFilter


@ConfigurationProperties(prefix = "settings")
@Service
class ProjectService : AbstractService() {

    lateinit var baseDir: String

    public fun getProjectData(): List<ProjectData> {
        val file = File(baseDir)
        val directories = file.list { current, name -> File(current, name).isDirectory }
        return directories.mapIndexedNotNull { index, dir -> ProjectData(index.toLong(), dir) }
    }
}
