package eu.glatz.sifidraw.model

import com.fasterxml.jackson.annotation.JsonView
import eu.glatz.sifidraw.util.JsonViews
import org.springframework.data.annotation.Transient
import org.springframework.data.mongodb.core.mapping.Document

@Document(collection = "SImage")
class SImage : SAImage() {

    @JsonView(JsonViews.AllDatasetData::class)
    var layers: MutableList<Layer> = mutableListOf()

    @Transient
    @JsonView(JsonViews.AllDatasetData::class, JsonViews.OnlyDatasetData::class)
    var data: String = ""

    @Transient
    @JsonView(JsonViews.AllDatasetData::class, JsonViews.OnlyDatasetData::class)
    var fileExtension: String = ""

    @JsonView(JsonViews.AllDatasetData::class, JsonViews.OnlyDatasetData::class)
    var width = 0

    @JsonView(JsonViews.AllDatasetData::class, JsonViews.OnlyDatasetData::class)
    var height = 0

    @JsonView(JsonViews.AllDatasetData::class, JsonViews.OnlyDatasetData::class)
    var hasLayerData = false

    init {
        type = "img"
    }
}