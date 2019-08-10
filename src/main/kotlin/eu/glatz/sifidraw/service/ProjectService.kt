package eu.glatz.sifidraw.service

import eu.glatz.sifidraw.model.Dataset
import eu.glatz.sifidraw.model.Image
import eu.glatz.sifidraw.model.ProjectData
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
@ConfigurationProperties(prefix = "settings")
class ProjectService : AbstractService() {

    lateinit var dir: String

    public fun getProjectData(): List<ProjectData> {
        val base = File(dir)

        val directories = base.list { current, name -> File(current, name).isDirectory }
        val projects = directories.map { dir -> ProjectData(dir) }

        projects.forEach {
            val project = File(base, it.id)
            it.datasets = project.list { current, name -> File(current, name).isDirectory }.map { dir -> Dataset(dir) }

            it.datasets.forEach {
                println("111")
                val dataset = File(project, it.id)
                var imgs = dataset.list { current, name -> name.matches(Regex(".*((.jpg)|(.png)|(.tif))")) }

                println(imgs.size)

                for (img in imgs) {
                    //         val imgFile = File(dataset, img)
                    //        val thumb = ImageIO.read(imgFile).getScaledInstance(100, 100, BufferedImage.SCALE_SMOOTH)
                    //        val imageBuff = BufferedImage(100, 100, BufferedImage.TYPE_INT_RGB)
                    //        imageBuff.graphics.drawImage(thumb, 0, 0, Color(0, 0, 0), null)
                    //        val os = ByteArrayOutputStream()
                    //        ImageIO.write(imageBuff, img.substringAfterLast("."), os)
                    //        val encodeImage = Base64.getEncoder().withoutPadding().encodeToString(os.toByteArray())
                    var img = Image(img, "");
                    it.images.add(img)
                }
            }
        }

        return projects
    }
}
