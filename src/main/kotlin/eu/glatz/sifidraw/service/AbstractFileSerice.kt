package eu.glatz.sifidraw.service

import java.io.File
import java.util.*

abstract class AbstractFileService : AbstractService() {

    fun getUniqueFile(baseDirectory: File, proposedName: String): File {
        var file = File(baseDirectory, proposedName)
        while (file.isDirectory) {
            file = File(baseDirectory, UUID.randomUUID().toString())
        }
        return file
    }
}