package eu.glatz.sifidraw.service

import java.io.File
import java.util.*

abstract class AbstractFileService : AbstractService() {

    fun getUniqueFile(baseDirectory: File, proposedName: String): File {
        var file = File(baseDirectory, proposedName)
        while (file.exists()) {
            val uid = UUID.randomUUID().toString() + if (file.extension.isNotEmpty()) ".${file.extension}" else ""
            file = File(baseDirectory, uid)
        }
        return file
    }
}