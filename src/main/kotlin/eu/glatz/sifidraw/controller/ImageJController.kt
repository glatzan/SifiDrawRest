package eu.glatz.sifidraw.controller

import eu.glatz.sifidraw.model.SImage
import eu.glatz.sifidraw.service.ImageJService
import eu.glatz.sifidraw.service.ImageMagicService
import eu.glatz.sifidraw.util.ImageUtil
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.*
import java.io.File
import java.nio.charset.Charset
import java.util.*

@CrossOrigin
@RestController
@RequestMapping("SifiDrawRest")
class ImageJController @Autowired constructor(
        private val imageJService: ImageJService) {

    @PostMapping("/imagej/lines")
    fun processImageReturnJSON(@RequestBody image: SImage): String {

        val file = ImageUtil.writeUniqueBase64Img(imageJService.dir, imageJService.file, image)

        val result = imageJService.runImageJReturnJson(file, File("D:/Projekte/sifidraw/tmp/lines.ijm"));

        return result
    }
}
