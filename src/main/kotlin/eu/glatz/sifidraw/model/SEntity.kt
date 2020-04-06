package eu.glatz.sifidraw.model

import com.fasterxml.jackson.annotation.JsonView
import eu.glatz.sifidraw.util.JsonViews
import org.bson.types.ObjectId
import org.springframework.data.annotation.Id

open class SEntity {
    @Id
    @JsonView(JsonViews.ProjectsAndDatasets::class, JsonViews.AllDatasetData::class)
    var id: String? = null

    @JsonView(JsonViews.ProjectsAndDatasets::class, JsonViews.AllDatasetData::class)
    var name: String = ""

    @JsonView(JsonViews.ProjectsAndDatasets::class, JsonViews.AllDatasetData::class)
    var path : String = ""

    @JsonView(JsonViews.ProjectsAndDatasets::class, JsonViews.AllDatasetData::class)
    var concurrencyCounter = 0;
}