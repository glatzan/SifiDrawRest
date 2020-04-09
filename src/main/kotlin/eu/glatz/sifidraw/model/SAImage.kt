package eu.glatz.sifidraw.model

import com.fasterxml.jackson.annotation.JsonView
import eu.glatz.sifidraw.util.JsonViews
import org.springframework.data.annotation.Transient
import org.springframework.data.mongodb.core.mapping.Document

@Document(collection = "SImage")
open class SAImage : SEntity() {

    companion object {
        @kotlin.jvm.Transient
        @JvmStatic
        val SEQUENCE_NAME: String = "image_sequence"
    }

    @JsonView(JsonViews.AllDatasetData::class, JsonViews.OnlyDatasetData::class)
    open var type: String = ""
}