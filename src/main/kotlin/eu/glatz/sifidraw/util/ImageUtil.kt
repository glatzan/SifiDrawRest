package eu.glatz.sifidraw.util

import java.io.ByteArrayOutputStream
import java.io.File
import java.util.*
import javax.imageio.ImageIO
import java.io.ByteArrayInputStream
import java.awt.image.BufferedImage


class ImageUtil {

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

        @JvmStatic
        fun writeBase64Img(imageString: String, file: File) {
            try {
                val decoder = Base64.getDecoder().decode(imageString)
                val bis = ByteArrayInputStream(decoder)
                val image = ImageIO.read(bis)
                bis.close()
                ImageIO.write(image, "png", file)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}
