package eu.glatz.sifidraw.service

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.stereotype.Service
import java.io.File
import ij.IJ
import ij.ImageJ
import ij.io.FileSaver
import ij.ImagePlus


@ConfigurationProperties(prefix = "imagej")
@Service
class ImageJService @Autowired constructor(
        private val jsonService: JsonService) {

    var dir: String = ""
    var executable: String = ""
    var file: String = ""
    var command: String = ""
    var pluginDir = ""

    fun runImageJReturnJson(imageFile: File, makroFile: File): String {
        val outputFile = runImageJ(imageFile, makroFile)
        return jsonService.readCsvToJSON(outputFile);
    }

    fun runImageJ(imageFile: File, makroFile: File): File {
        var command = command.replace("{dir}", dir)
        command = command.replace("{executable}", executable)
        command = command.replace("{macro}", makroFile.absolutePath)
        command = command.replace("{inputfile}", imageFile.absolutePath)

        val outputFile = File(imageFile.absolutePath.replace(imageFile.name, imageFile.name.toString().replaceAfterLast(".", "csv")))
        command = command.replace("{outputfile}", outputFile.absolutePath)

        println("Running post process command: $command")

        val process2 = Runtime.getRuntime().exec(command)
        println("Waiting for batch file ...");
        process2.waitFor();
        println("Batch file done.");

        return outputFile
    }

    fun run(imageFile: File) {
        val directory = "C:\\Users\\Speedy Octopus\\Desktop\\10Cover Shots\\10.JPG"

        val imp = IJ.openImage(directory)
        val fileSaver = FileSaver(imp)

        System.setProperty("plugins.dir", pluginDir)
        IJ.run(imp, "8-bit", "")
        IJ.run(imp, "Ridge Detection", "line_width=3.5 high_contrast=230 low_contrast=87 correct_position extend_line show_ids displayresults add_to_manager method_for_overlap_resolution=NONE sigma=1.51 lower_threshold=3.06 upper_threshold=7.99 minimum_line_length=5 maximum=0")

        //fileSaver.save("D:test.csv")
        //fileSaver.saveAsJpeg("C:\\Users\\Speedy Octopus\\Desktop\\10Cover Shots\\10edited.JPG")
    }
}
