package eu.glatz.sifidraw.util

import java.io.ByteArrayOutputStream
import java.io.File
import java.util.*
import javax.imageio.ImageIO

class ImageReader {

    companion object {

        @JvmStatic
        fun readImgAsBase64(file: File): String {
            println(file.absolutePath + " " + file.isFile)
            if (file.isFile) {
                val img = ImageIO.read(file)
                val os = ByteArrayOutputStream()
                ImageIO.write(img, "png", os)
                return Base64.getEncoder().withoutPadding().encodeToString(os.toByteArray())
            }
            return ""
        }
    }
}
