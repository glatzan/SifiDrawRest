package eu.glatz.sifidraw.service

import eu.glatz.sifidraw.config.ProjectSettings
import eu.glatz.sifidraw.model.Dataset
import eu.glatz.sifidraw.model.Image
import eu.glatz.sifidraw.model.ImageGroup
import eu.glatz.sifidraw.repository.ImageGroupRepository
import eu.glatz.sifidraw.repository.ImageRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import java.io.File
import java.nio.charset.Charset
import java.util.*


@Service
class DatasetService @Autowired constructor(
        private val projectSettings: ProjectSettings,
        private val imageGroupRepository: ImageGroupRepository,
        private val imageService: ImageService,
        private val imageRepository: ImageRepository) : AbstractService() {

    public fun getDataset(id: String): Dataset {
        if ("" == id)
            throw IllegalArgumentException("");

        val decodedID = String(Base64.getDecoder().decode(id), Charset.forName("UTF-8"))

        val dataset = Dataset(id, decodedID.substringAfterLast("/"))
        val base = File(projectSettings.dir, decodedID)

        if (!base.isDirectory)
            throw IllegalArgumentException("");

        val files = base.listFiles()
        if (files != null && files.isNotEmpty()) {
            dataset.images.addAll(getImagesOfFolder(decodedID, base))

            for (folder in files) {
                if (folder.isDirectory) {
                    val imageGroupID = (decodedID + "/" + folder.name)
                    val base64ID = String(Base64.getEncoder().encodeToString(imageGroupID.toByteArray()).toByteArray(), Charset.forName("UTF-8"))
                    val imageGroup = imageGroupRepository.findById(base64ID).orElse(ImageGroup(base64ID, folder.name))
                    imageGroup.images.addAll(getImagesOfFolder(imageGroupID, folder))
                    dataset.images.add(imageGroup)
                }
            }
        }
//        Collections.sort(list, object : Comparator<String> {
//            override fun compare(o1: String, o2: String): Int {
//                return extractInt(o1) - extractInt(o2)
//            }
//
//            internal fun extractInt(s: String): Int {
//                val num = s.replace("\\D".toRegex(), "")
//                // return 0 if no digits found
//                return if (num.isEmpty()) 0 else Integer.parseInt(num)
//            }
//        })


        return dataset
    }

    private fun getImagesOfFolder(decodedID: String, folder: File): MutableList<Image> {
        val resultList = mutableListOf<Image>()
        val images = folder.list { _, name -> name.matches(Regex(".*((.jpg)|(.png)|(.tif))")) } ?: return resultList
        for (img in images) {
            val imageWithoutFileEnd = img.substringBeforeLast(".")
            val imageID = Base64.getEncoder().encodeToString("${decodedID}/$img".toByteArray())
            resultList.add(imageRepository.findById(imageID).orElse(Image(imageID, imageWithoutFileEnd)))
        }
        return resultList
    }

    fun addImageToDataset(dataset: Dataset, image: Image): Image {
        val groupDir = String(Base64.getDecoder().decode(dataset.id), Charset.forName("UTF-8"))
        return this.imageService.addImageToPath(groupDir, image)
    }
}

