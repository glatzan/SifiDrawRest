package eu.glatz.sifidraw.util

import eu.glatz.sifidraw.model.Image
import java.awt.image.BufferedImage
import java.io.*
import java.util.*
import javax.imageio.ImageIO


class ImageUtil {

    companion object {

        @JvmStatic
        fun readImageAsBufferedImage(file: File): BufferedImage {
            println(file.absolutePath + " " + file.isFile)
            if (file.isFile) {
                return ImageIO.read(file)
            }
            throw IOException("Not Image File")
        }

        @JvmStatic
        fun imageToBase64(img: BufferedImage): String {
            val os = ByteArrayOutputStream()
            ImageIO.write(img, "png", os)
            return Base64.getEncoder().encodeToString(os.toByteArray())
        }

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
            writeImg(Base64.getDecoder().decode(imageString), file)
        }

        @JvmStatic
        fun writeImg(bytes: ByteArray, file: File) {
            try {
                val bis = ByteArrayInputStream(bytes)
                val image = ImageIO.read(bis)
                bis.close()
                ImageIO.write(image, "png", file)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        @JvmStatic
        fun findImage(path: String, imagePrefix: String): File {

            val base = File(path, imagePrefix.substringBeforeLast("/"))
            val img = base.listFiles { current, name -> name.matches(Regex(imagePrefix.substringAfterLast("/") + ".*")) }

            if (img.size >= 0)
                return img[0];

            throw FileNotFoundException();
        }

        @JvmStatic
        fun writeUniqueBase64Img(dir: String, file: String, image: Image): File {
            val file = File(dir, file.replace("{}", System.currentTimeMillis().toString() + "" + Math.random()))
            println(file.absolutePath)
            ImageUtil.writeBase64Img(image.data, file);
            return file;
        }
    }
}
