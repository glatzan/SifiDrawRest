package eu.glatz.sifidraw.model

import org.springframework.data.annotation.Transient

open class IImage(open var id: String, open var name: String) {
    @Transient
    open var type: String = ""

    open var concurrencyCounter = 0;
}