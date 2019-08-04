package eu.glatz.sifidraw.service

import java.io.File
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.core.util.DefaultPrettyPrinter


class JsonService : AbstractService() {

    fun <T : Any> readJsonData(file: File, oftClass: Class<T>): T {
        val objectMapper = ObjectMapper()
        val emp = objectMapper.readValue(file, oftClass::class.java)
        return emp as T
    }

    fun <T> writeValue(file: File, obj: T) {
        val mapper = ObjectMapper()
        val writer = mapper.writer(DefaultPrettyPrinter())
        writer.writeValue(file, obj)
    }

    fun jsonFileExist(file: File): Boolean {
        return file.isFile
    }
}
