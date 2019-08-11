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

@Service
class DatasetService @Autowired constructor(
        private val projectSettings: ProjectSettings) : AbstractService() {

    public fun getDataset(id: String): Dataset {
        if ("" == id)
            throw IllegalArgumentException("");

        val dataset = Dataset(id)
        val base = File(projectSettings.dir, id.replace("_","/"))

        if(!base.isDirectory)
            throw IllegalArgumentException("");

        var imgs = base.list { current, name -> name.matches(Regex(".*((.jpg)|(.png)|(.tif))")) }

        for (img in imgs) {
            //         val imgFile = File(dataset, img)
            //        val thumb = ImageIO.read(imgFile).getScaledInstance(100, 100, BufferedImage.SCALE_SMOOTH)
            //        val imageBuff = BufferedImage(100, 100, BufferedImage.TYPE_INT_RGB)
            //        imageBuff.graphics.drawImage(thumb, 0, 0, Color(0, 0, 0), null)
            //        val os = ByteArrayOutputStream()
            //        ImageIO.write(imageBuff, img.substringAfterLast("."), os)
            //        val encodeImage = Base64.getEncoder().withoutPadding().encodeToString(os.toByteArray())
            var img = Image(img);
            dataset.images.add(img)
        }
        return dataset
    }
}

