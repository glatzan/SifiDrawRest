package eu.glatz.sifidraw.service

import eu.glatz.sifidraw.config.ProjectSettings
import eu.glatz.sifidraw.model.Dataset
import eu.glatz.sifidraw.model.Image
import eu.glatz.sifidraw.model.ProjectData
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import org.springframework.web.bind.annotation.RequestParam
import java.io.File
import java.lang.IllegalArgumentException
import java.nio.charset.Charset
import java.util.*
import java.util.Collections



@Service
class DatasetService @Autowired constructor(
        private val projectSettings: ProjectSettings) : AbstractService() {

    public fun getDataset(id: String): Dataset {
        if ("" == id)
            throw IllegalArgumentException("");

        val decodedID = String(Base64.getDecoder().decode(id), Charset.forName("UTF-8"))

        val dataset = Dataset(id, decodedID.substringAfterLast("/"))
        val base = File(projectSettings.dir, decodedID)

        if (!base.isDirectory)
            throw IllegalArgumentException("");

        var imgs = base.list { current, name -> name.matches(Regex(".*((.jpg)|(.png)|(.tif))")) }

        var list = imgs.toList();
        Collections.sort(list, object : Comparator<String> {
             override fun compare(o1: String, o2: String): Int {
                return extractInt(o1) - extractInt(o2)
            }

            internal fun extractInt(s: String): Int {
                val num = s.replace("\\D".toRegex(), "")
                // return 0 if no digits found
                return if (num.isEmpty()) 0 else Integer.parseInt(num)
            }
        })

        for (img in list) {
            val imgWithoudFileEnd = img.substringBeforeLast(".")
            //         val imgFile = File(dataset, img)
            //        val thumb = ImageIO.read(imgFile).getScaledInstance(100, 100, BufferedImage.SCALE_SMOOTH)
            //        val imageBuff = BufferedImage(100, 100, BufferedImage.TYPE_INT_RGB)
            //        imageBuff.graphics.drawImage(thumb, 0, 0, Color(0, 0, 0), null)
            //        val os = ByteArrayOutputStream()
            //        ImageIO.write(imageBuff, img.substringAfterLast("."), os)
            //        val encodeImage = Base64.getEncoder().withoutPadding().encodeToString(os.toByteArray())
            val res = Image(Base64.getEncoder().encodeToString("${decodedID}/$imgWithoudFileEnd".toByteArray()), imgWithoudFileEnd);
            dataset.images.add(res)
        }
        return dataset
    }
}

