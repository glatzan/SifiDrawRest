package eu.glatz.sifidraw.util

import java.io.ByteArrayOutputStream
import java.io.File
import java.util.*
import javax.imageio.ImageIO
import java.io.ByteArrayInputStream
import java.awt.image.BufferedImage
import java.io.FileNotFoundException


class ImageUtil {

    companion object {

        @JvmStatic
        fun readImgAsBase64(file: File): String {
            println(file.absolutePath + " " + file.isFile)
            if (file.isFile) {
                val img = ImageIO.read(file)
                val os = ByteArrayOutputStream()
                ImageIO.write(img, "png", os)
                return Base64.getEncoder().encodeToString(os.toByteArray())
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
                ImageIO.write(image, "TIFF", file)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        @JvmStatic
        fun findImage(path: String, imagePrefix : String) : File{

            val base = File(path, imagePrefix.substringBeforeLast("/"))
            val img = base.listFiles{ current, name -> name.matches(Regex(imagePrefix.substringAfterLast("/")+".*")) }

            if(img.size >= 0)
                return img[0];

            throw FileNotFoundException();
        }
    }
}
