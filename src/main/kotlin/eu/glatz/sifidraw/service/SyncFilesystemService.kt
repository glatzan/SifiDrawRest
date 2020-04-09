package eu.glatz.sifidraw.service

import eu.glatz.sifidraw.config.ProjectSettings
import eu.glatz.sifidraw.repository.SProjectRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import java.io.File

@Service
class SyncFilesystemService @Autowired constructor(
        private val projectSettings: ProjectSettings,
        private val projectRepository: SProjectRepository) : AbstractService() {

    fun sync(){
        val baseDir = File(projectSettings.dir)
        val files = baseDir.listFiles()

        for(file in files){
            if(file.isDirectory){
                val dbProject = projectRepository.findByPath(file.path)
            }

        }
    }
}