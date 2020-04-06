package eu.glatz.sifidraw.service

import eu.glatz.sifidraw.config.ProjectSettings
import eu.glatz.sifidraw.repository.ImageGroupRepository
import eu.glatz.sifidraw.repository.ImageRepository
import eu.glatz.sifidraw.repository.SProjectRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import java.io.File

@Service
class SyncFilesystemService @Autowired constructor(
        private val projectSettings: ProjectSettings,
        private val imageGroupRepository: ImageGroupRepository,
        private val projectRepository: SProjectRepository,
        private val imageRepository: ImageRepository) : AbstractService() {

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