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
        private val imageGroupService: ImageGroupService,
        private val imageRepository: ImageRepository) : AbstractService() {

    public fun getDataset(datasetPath: String, minimize: Boolean): Dataset {
        if ("" == datasetPath)
            throw IllegalArgumentException("");

        val fixedDatasetPath = datasetPath + if (!datasetPath.endsWith("/")) "/" else ""
        val id = Base64.getEncoder().encodeToString(fixedDatasetPath.toByteArray())
        val dataset = Dataset(id, fixedDatasetPath.substringBeforeLast("/").substringAfterLast("/"))
        val absoluteDatasetPath = File(projectSettings.dir, fixedDatasetPath)


        if (!absoluteDatasetPath.isDirectory)
            throw IllegalArgumentException("Dataset not found");

        val files = absoluteDatasetPath.listFiles()
        if (files != null && files.isNotEmpty()) {
            dataset.images.addAll(this.imageService.getImagesOfFolder(fixedDatasetPath, false))

            var tmpList = mutableListOf<ImageGroup>()
            for (folder in files) {
                if (folder.isDirectory) {
                    tmpList.add(imageGroupService.getImageGroup("${fixedDatasetPath}${folder.name}", false, minimize = minimize))
                }
            }

            tmpList.sortBy { it.name }

            dataset.images.addAll(tmpList)
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

    fun addImageToDataset(dataset: Dataset, image: Image): Image {
        val groupDir = String(Base64.getDecoder().decode(dataset.id), Charset.forName("UTF-8"))
        return this.imageService.moveAndAddImageToPath(groupDir, image)
    }
}

