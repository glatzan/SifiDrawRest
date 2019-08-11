package eu.glatz.sifidraw.service

import eu.glatz.sifidraw.config.ProjectSettings
import eu.glatz.sifidraw.model.Dataset
import eu.glatz.sifidraw.model.Image
import eu.glatz.sifidraw.model.ProjectData
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.stereotype.Service
import java.io.File
import org.springframework.core.io.ClassPathResource
import java.nio.file.Files
import java.util.*
import java.awt.image.BufferedImage
import java.io.ByteArrayOutputStream
import javax.imageio.ImageIO
import java.awt.Color
import javax.swing.Spring.height


@Service
class ProjectService @Autowired constructor(
        private val projectSettings: ProjectSettings) : AbstractService() {

    public fun getProjectData(): List<ProjectData> {
        val base = File(projectSettings.dir)

        val directories = base.list { current, name -> File(current, name).isDirectory }
        val projects = directories.map { dir -> ProjectData(dir) }

        projects.forEach {
            val project = File(base, it.id)
            it.datasets = project.list { current, name -> File(current, name).isDirectory && !name.startsWith(".")}.map { dir -> Dataset("${it.id}_|_$dir", dir) }
        }

        return projects
    }
}
