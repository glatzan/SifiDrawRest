package eu.glatz.sifidraw.model

import org.springframework.data.annotation.Transient

class ImageGroup(id: String, name: String) : IImage(id, name)  {
    @Transient
    public var images = mutableListOf<Image>();

    @Transient
    override var type: String = "group"
}