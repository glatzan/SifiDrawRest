package eu.glatz.sifidraw.service

import eu.glatz.sifidraw.model.Image
import eu.glatz.sifidraw.util.ImageUtil
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.stereotype.Service
import java.io.File

@ConfigurationProperties(prefix = "imagemagic")
@Service
class ImageMagicService {

    var dir: String = ""
    var executable: String = ""
    var file: String = ""
    var command: String = ""

    fun prepareImage(image: Image): File {
        val file = File(dir, file.replace("{}", System.currentTimeMillis().toString() + "" + Math.random()))
        println(file.absolutePath)
        ImageUtil.writeBase64Img(image.data, file);
        return file;
    }

    fun runImageMagic(imageFile: File, userCommand: String): File {
        var command = command.replace("{dir}", dir)
        command = command.replace("{executable}", executable)
        command = command.replace("{command}", userCommand)
        command = command.replace("{inputfile}", imageFile.absolutePath)

        val exFile = File(imageFile.absolutePath.replace(imageFile.name, imageFile.name.toString().replace(".", "_.")))
        command = command.replace("{outputfile}", exFile.absolutePath)

        println("Running post process command: $command")

        val process2 = Runtime.getRuntime().exec(command)
        println("Waiting for batch file ...");
        process2.waitFor();
        println("Batch file done.");

        return exFile
    }

    fun getConvertedImage(file: File): String {
        return ImageUtil.readImgAsBase64(file)
    }
}
