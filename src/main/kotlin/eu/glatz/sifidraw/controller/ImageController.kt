package eu.glatz.sifidraw.controller

import eu.glatz.sifidraw.model.SAImage
import eu.glatz.sifidraw.model.SImage
import eu.glatz.sifidraw.repository.SAImageRepository
import eu.glatz.sifidraw.service.SAImageService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.*
import org.springframework.web.multipart.MultipartFile
import java.nio.charset.Charset
import java.util.*


@CrossOrigin
@RestController
@RequestMapping("SifiDrawRest")
class ImageController @Autowired constructor(
        private val saImageRepository: SAImageRepository,
        private val saImageService: SAImageService) {

    @GetMapping("/image/{imageID}")
    fun getImage(@PathVariable imageID: String, @RequestParam("format") format: Optional<String>): SAImage {
        return saImageService.loadImage(imageID, true, format.orElse("png"))
    }

    @PutMapping("/image/update")
    fun updateImage(@RequestBody image: SAImage): SAImage {
        return saImageService.updateImage(image)
    }

    //     @PostMapping("/image/{type}")
    @PostMapping("/image/addToDataset/{datasetID}")
    fun createImageInDataset(@RequestBody image: SImage, @PathVariable datasetID: String, @RequestParam("format") format: Optional<String>) {
        if (format.isPresent && !format.get().matches(Regex("jpg|png|tif|bmp")))
            throw IllegalArgumentException("Please provide a valid type")
        saImageService.addImageToDataset(image, datasetID, format.orElse("png"))
    }

    //     @PostMapping("/image/{type}")
    @PostMapping("/image/addToImageGroup/{imageGroupID}")
    fun createImageInImageGroup(@RequestBody image: SImage, @PathVariable imageGroupID: String, @RequestParam("format") format: Optional<String>) {
        if (format.isPresent && !format.get().matches(Regex("jpg|png|tif|bmp")))
            throw IllegalArgumentException("Please provide a valid type")
        saImageService.addImageToImageGroup(image, imageGroupID, format.orElse("png"))
    }

    @DeleteMapping("/image/delete/{id}")
    fun deleteImage(@PathVariable id: String) {
        saImageService.deleteImage(id)
    }

    @GetMapping("/image/cloneToDataset/{id}")
    fun cloneImageToDataset(@PathVariable id: String, @RequestParam("parentID") parentID: Optional<String>): SImage {
        return saImageService.cloneImageToDataset(id, parentID.orElse(""))
    }

    @GetMapping("/image/rename/{id}")
    fun renameImage(@PathVariable id: String, @RequestParam("newName") newName: Optional<String>): SAImage {
        val result = saImageRepository.findById(id).orElseThrow { IllegalArgumentException("Image not found!") }
        result.name = String(Base64.getDecoder().decode(newName.orElseThrow { IllegalArgumentException("Entity not found!") }), Charset.forName("UTF-8"))
        result.concurrencyCounter++;
        return saImageRepository.save(result)
    }

    @PostMapping("/image/upload/{parentID}")
    fun uploadPicture(@RequestParam("file") multipartFile: MultipartFile?, @PathVariable parentID: String, @RequestParam("format") format: Optional<String>): SImage {
        if (format.isPresent && !format.get().matches(Regex("jpg|png|tif|bmp")))
            throw IllegalArgumentException("Please provide a valid type")

        if (multipartFile == null || multipartFile.size == 0L)
            throw IllegalArgumentException("File is empty")

        return saImageService.addMultiPartImageToDataset(multipartFile, parentID)
    }


//    @PutMapping("/image/update/checked")
//    fun updateAndCheckImageData(@RequestBody image: Image): Image {
//        return if (imageService.imageExist(image.id)) {
//            println("image ok update" + image.name)
//            return imageService.updateImage(image)
//        } else {
//            val img = Gson().toJson(image)
//            println("Image not found!")
//            println(img)
//            Image("-", "-")
//        }
//    }
}
