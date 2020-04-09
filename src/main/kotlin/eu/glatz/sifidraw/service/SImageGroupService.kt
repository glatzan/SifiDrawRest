package eu.glatz.sifidraw.service

import eu.glatz.sifidraw.config.ProjectSettings
import eu.glatz.sifidraw.model.SIHasImages
import eu.glatz.sifidraw.model.SImage
import eu.glatz.sifidraw.model.SImageGroup
import eu.glatz.sifidraw.repository.SAImageRepository
import eu.glatz.sifidraw.repository.SDatasetRepository
import eu.glatz.sifidraw.repository.SProjectRepository
import org.apache.commons.io.FileUtils
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import java.io.File

@Service
class SImageGroupService @Autowired constructor(
        private val sProjectRepository: SProjectRepository,
        private val projectSettings: ProjectSettings,
        private val saImageService: SAImageService,
        private val saImageRepository: SAImageRepository,
        private val sDatasetRepository: SDatasetRepository) : AbstractFileService() {


    fun loadImageGroup(imageGroupID: String, loadImageData: Boolean, asFormat: String = "png"): SImageGroup {
        val imageGroup = saImageRepository.findById(imageGroupID).orElseThrow { throw IllegalArgumentException("ImageGroup not found!") } as SImageGroup
        return loadImageGroup(imageGroup, loadImageData, asFormat)
    }

    fun loadImageGroup(imageGroup: SImageGroup, loadImageData: Boolean, asFormat: String = "png"): SImageGroup {
        if (loadImageData) {
            for (img in imageGroup.images) {
                if (img is SImage)
                    saImageService.loadImageData(img, asFormat)
                else if (img is SImageGroup)
                    this.loadImageGroup(img, loadImageData, asFormat)
            }
        }

        return imageGroup
    }

    fun createImageGroup(name: String, parent: String): Pair<SImageGroup, SIHasImages> {
        return createImageGroup(name, saImageService.findParent(parent))
    }

    fun createImageGroup(name: String, parent: SIHasImages): Pair<SImageGroup, SIHasImages> {
        require(name.isNotEmpty()) { throw IllegalArgumentException("Provide Image Group Name!") }

        val imageGroup = SImageGroup()
        imageGroup.name = name

        return addImageGroupToParent(imageGroup, parent)
    }

    fun addImageGroupToParent(imageGroupID: String, parentID: String): Pair<SImageGroup, SIHasImages> {
        val parent = saImageService.findParent(parentID)
        val imageGroup = saImageRepository.findById(imageGroupID).orElseThrow { throw IllegalArgumentException("ImageGroup not found!") } as SImageGroup
        return addImageGroupToParent(imageGroup, parent)
    }

    fun addImageGroupToParent(imageGroup: SImageGroup, parent: SIHasImages): Pair<SImageGroup, SIHasImages> {
        require(imageGroup.name.isNotEmpty()) { IllegalArgumentException("Provide ImageGroup name!") }

        val imageGroupFile = getUniqueFile(File(projectSettings.dir, parent.path), imageGroup.name)
        imageGroupFile.mkdirs()

        imageGroup.path = "${parent.path}${imageGroupFile.name}/"
        val dbImageGroup = saImageRepository.save(imageGroup)

        parent.images.add(dbImageGroup)
        val dbParent = saImageService.saveParent(parent)

        return Pair(dbImageGroup, dbParent)
    }

    fun deleteImageGroup(imageGroupID: String) {
        val imageGroup = saImageRepository.findById(imageGroupID).orElseThrow { throw IllegalArgumentException("ImageGroup not found!") }
        if (imageGroup !is SImageGroup)
            throw IllegalArgumentException("No ImageGroup found!")

        deleteImageGroup(imageGroup)
    }

    fun deleteImageGroup(imageGroup: SImageGroup) {
        for (img in imageGroup.images) {
            if (img is SImage)
                saImageService.deleteImage(img, false)
            else if (img is SImageGroup)
                deleteImageGroup(img)
        }

        FileUtils.deleteDirectory(File(projectSettings.dir, imageGroup.path))
        saImageRepository.delete(imageGroup)
    }

    fun cloneImageGroup(imageGroupID: String, targetParentID: String): Pair<SImageGroup, SIHasImages> {
        val imageGroup = saImageRepository.findById(imageGroupID).orElseThrow { throw IllegalArgumentException("ImageGroup not found!") }
        if (imageGroup !is SImageGroup)
            throw IllegalArgumentException("No ImageGroup found!")
        val parent = if (targetParentID.isEmpty()) saImageService.findParentByImage(imageGroup) else saImageService.findParent(targetParentID)
        return cloneImageGroup(imageGroup, parent)
    }

    fun cloneImageGroup(imageGroup: SImageGroup, parent: SIHasImages): Pair<SImageGroup, SIHasImages> {
        val images = imageGroup.images
        imageGroup.id = null
        imageGroup.images = mutableListOf()

        var clonedImageGroup = addImageGroupToParent(imageGroup, parent).first

        for (image in images) {
            if (image is SImage)
                clonedImageGroup = saImageService.cloneImage(image, clonedImageGroup).second as SImageGroup
            else if (image is SImageGroup)
                clonedImageGroup = this.cloneImageGroup(image, clonedImageGroup).second as SImageGroup
        }

        return Pair(clonedImageGroup, parent)
    }

    fun updateImageGroup(imageGroup: SImageGroup) {
        val dbImageGroup = saImageRepository.findById(imageGroup.id
                ?: "").orElseThrow { throw IllegalArgumentException("ImageGroup not found!") }
        if (imageGroup.concurrencyCounter > dbImageGroup.concurrencyCounter) {
            saImageRepository.save(imageGroup)
            for (image in imageGroup.images) {
                if (image is SImage)
                    saImageService.updateImage(image)
                else if (image is SImageGroup)
                    updateImageGroup(image)
            }
        } else
            throw IllegalArgumentException("Concurrency Error: new counter = ${imageGroup.concurrencyCounter}; old image ${dbImageGroup.concurrencyCounter}")
    }

}