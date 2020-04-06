package eu.glatz.sifidraw.model

import org.springframework.data.annotation.Transient
import org.springframework.data.mongodb.core.mapping.Document

@Document(collection = "SImage")
open class SAImage : SEntity() {

    companion object {
        @kotlin.jvm.Transient
        @JvmStatic
        val SEQUENCE_NAME: String = "image_sequence"
    }

    @Transient
    open var type: String = ""
}