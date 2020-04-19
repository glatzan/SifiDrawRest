package eu.glatz.sifidraw.service

import com.google.gson.Gson
import eu.glatz.sifidraw.config.ProjectSettings
import eu.glatz.sifidraw.model.SProject
import eu.glatz.sifidraw.repository.SProjectRepository
import org.apache.commons.io.FileUtils
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import java.io.*
import java.util.zip.ZipEntry
import java.util.zip.ZipOutputStream


@Service
class SProjectService @Autowired constructor(
        private val sequenceGeneratorService: SequenceGeneratorService,
        private val projectSettings: ProjectSettings,
        private val sDatasetService: SDatasetService,
        private val sProjectRepository: SProjectRepository) : AbstractFileService() {

    fun createProject(name: String): SProject {
        val newProject = SProject()
        newProject.name = name

        if (!name.matches(Regex("\\w*")))
            throw IllegalArgumentException("Illegal name $name")

        val projectFile = getUniqueFile(File(projectSettings.dir), name)
        projectFile.mkdirs()
        newProject.path = "${projectFile.name}/"

        return sProjectRepository.save(newProject)
    }

    fun deleteProject(projectID :String) : Boolean{
        val project = sProjectRepository.findById(projectID).orElseThrow { throw IllegalArgumentException("Project not found!") }

        for(dataset in project.datasets){
            sDatasetService.deleteDataset(dataset.id ?: "",true)
        }

        sProjectRepository.delete(project)
        File(projectSettings.dir, project.path).delete()

        return true
    }

    fun exportProject(projectID: String){
        val project = sProjectRepository.findById(projectID).orElseThrow { throw IllegalArgumentException("Project not found!") }

        val byteOutputStream = ByteArrayOutputStream()
        val out = ZipOutputStream(byteOutputStream)

        val e = ZipEntry("db.json")
        out.putNextEntry(e)

        val dbData = Gson().toJson(project).toByteArray()
        out.write(dbData, 0, dbData.size)
        out.closeEntry()
        out.close()

        FileUtils.writeByteArrayToFile(File("D:/test.zip"),byteOutputStream.toByteArray())

        VerzeichnisZippen(File(projectSettings.dir,project.path))
    }

    fun VerzeichnisZippen(folder: File) {
        val zipName = "D:/test"
        var zos: ZipOutputStream? = null
        try {
            val f = File("$zipName.zip")
            println("Erzeuge Archiv " + f.canonicalPath)
            zos = ZipOutputStream(FileOutputStream(
                    f.canonicalPath))
            zipDir(zipName, folder.path, folder, zos)
        } catch (e: IOException) {
            e.printStackTrace()
        } finally {
            try {
                zos?.close()
            } catch (ioe: IOException) {
            }
        }
    }

    private fun zipDir(zipName: String?, dirToZip: String?, dirToZipFile: File?,
                       zos: ZipOutputStream?) {
        if (zipName == null || dirToZip == null || dirToZipFile == null || zos == null || !dirToZipFile.isDirectory) return
        var fis: FileInputStream? = null
        try {
            val fileArr = dirToZipFile.listFiles()
            var path: String
            for (f in fileArr) {
                if (f.isDirectory) {
                    zipDir(zipName, dirToZip, f, zos)
                    continue
                }
                fis = FileInputStream(f)
                path = f.canonicalPath
                val name = path.substring(dirToZip.length, path.length)
                println("Packe $name")
                zos.putNextEntry(ZipEntry(name))
                var len: Int
                val buffer = ByteArray(2048)
                while (fis.read(buffer, 0, buffer.size).also { len = it } > 0) {
                    zos.write(buffer, 0, len)
                }
            }
        } catch (e: FileNotFoundException) {
            e.printStackTrace()
        } catch (e: IOException) {
            e.printStackTrace()
        } finally {
            try {
                fis?.close()
            } catch (ioe: IOException) {
            }
        }
    }
}