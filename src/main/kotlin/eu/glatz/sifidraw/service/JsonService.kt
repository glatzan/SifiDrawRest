package eu.glatz.sifidraw.service

import com.fasterxml.jackson.core.JsonFactory
import java.io.File
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.core.util.DefaultPrettyPrinter
import org.simpleflatmapper.csv.CsvParser
import org.springframework.stereotype.Service
import java.io.StringWriter


@Service
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

    fun readCsvToJSON(csvFile: File) : String {
        val reader = CsvParser.reader(csvFile)

        val jsonFactory = JsonFactory()

        val iterator = reader.iterator()
        val headers = iterator.next()

        val result = StringWriter();

        jsonFactory.createGenerator(result).use { jsonGenerator ->

            jsonGenerator.writeStartArray()

            while (iterator.hasNext()) {
                jsonGenerator.writeStartObject()
                val values = iterator.next()
                val nbCells = Math.min(values.size, headers.size)
                for (i in 0 until nbCells) {
                    jsonGenerator.writeFieldName(headers[i])
                    jsonGenerator.writeString(values[i])
                }
                jsonGenerator.writeEndObject()
            }
            jsonGenerator.writeEndArray()
        }

        reader.close();

        return result.toString();
    }
}
