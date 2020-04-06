package eu.glatz.sifidraw.model

import org.springframework.data.mongodb.core.mapping.DBRef
import org.springframework.data.mongodb.core.mapping.Document

@Document
class SDataset : SEntity() {

    companion object {
        @Transient
        @JvmStatic
        val SEQUENCE_NAME: String = "dataset_sequence"
    }

    @DBRef(lazy = true)
    var images = mutableListOf<SAImage>()
}