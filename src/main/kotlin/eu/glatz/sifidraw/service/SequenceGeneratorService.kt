package eu.glatz.sifidraw.service

import eu.glatz.sifidraw.model.DatabaseSequence
import eu.glatz.sifidraw.repository.DatabaseSequenceRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service


@Service
class SequenceGeneratorService @Autowired constructor(
        private val databaseSequenceRepository: DatabaseSequenceRepository) {

    fun generateSequence(seqName: String): Long {
        val seq = databaseSequenceRepository.findById(seqName)

        return if (seq.isPresent) {
            val result = seq.get().seq
            seq.get().seq += 1
            databaseSequenceRepository.save(seq.get())
            result
        } else {
            val nseq = DatabaseSequence()
            nseq.id = seqName
            nseq.seq = 1
            databaseSequenceRepository.save(nseq)
            0
        }
    }
}