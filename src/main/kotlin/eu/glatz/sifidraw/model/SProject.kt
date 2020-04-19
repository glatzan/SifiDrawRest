package eu.glatz.sifidraw.model

import com.fasterxml.jackson.annotation.JsonView
import eu.glatz.sifidraw.util.JsonViews
import org.springframework.data.mongodb.core.mapping.DBRef
import org.springframework.data.mongodb.core.mapping.Document

@Document
class SProject : SEntity() {

    companion object {
        @Transient
        @JvmStatic
        val SEQUENCE_NAME: String = "project_sequence"
    }

    @JsonView(JsonViews.ProjectsAndDatasets::class, JsonViews.AllDatasetData::class)
    @DBRef
    var datasets = mutableListOf<SDataset>()

    init {
        type = "project"
    }
}