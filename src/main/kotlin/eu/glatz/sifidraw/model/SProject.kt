package eu.glatz.sifidraw.model

import org.springframework.data.mongodb.core.mapping.DBRef
import org.springframework.data.mongodb.core.mapping.Document

@Document
class SProject : SEntity() {

    companion object {
        @Transient
        @JvmStatic
        val SEQUENCE_NAME: String = "project_sequence"
    }

    @DBRef
    var datasets = mutableListOf<SDataset>()
}