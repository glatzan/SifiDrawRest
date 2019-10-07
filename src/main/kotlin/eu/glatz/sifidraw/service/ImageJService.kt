package eu.glatz.sifidraw.service

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.stereotype.Service
import java.io.File

@ConfigurationProperties(prefix = "imagej")
@Service
class ImageJService @Autowired constructor(
        private val jsonService: JsonService) {
    var dir: String = ""
    var executable: String = ""
    var file: String = ""
    var command: String = ""


    fun runImageJReturnJson(imageFile: File, makroFile: File): String {
        val outputFile = runImageJ(imageFile, makroFile)
        return jsonService.readCsvToJSON(outputFile);
    }

    fun runImageJ(imageFile: File, makroFile: File): File {
        var command = command.replace("{dir}", dir)
        command = command.replace("{executable}", executable)
        command = command.replace("{macro}", makroFile.absolutePath)
        command = command.replace("{inputfile}", imageFile.absolutePath)

        val outputFile = File(imageFile.absolutePath.replace(imageFile.name, imageFile.name.toString().replaceAfterLast(".", ".csv")))
        command = command.replace("{outputfile}", outputFile.absolutePath)

        println("Running post process command: $command")

        val process2 = Runtime.getRuntime().exec(command)
        println("Waiting for batch file ...");
        process2.waitFor();
        println("Batch file done.");

        return outputFile
    }
}
