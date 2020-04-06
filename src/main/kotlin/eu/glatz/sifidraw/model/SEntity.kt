package eu.glatz.sifidraw.model

import org.bson.types.ObjectId
import org.springframework.data.annotation.Id

open class SEntity {
    @Id
    var id: String? = null

    var name: String = ""

    var path : String = ""

    var concurrencyCounter = 0;
}