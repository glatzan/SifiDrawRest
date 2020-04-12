package eu.glatz.sifidraw.legacy.model

import eu.glatz.sifidraw.legacy.model.IImage
import eu.glatz.sifidraw.legacy.model.Image
import org.springframework.data.annotation.Transient

class ImageGroup(id: String, name: String) : IImage(id, name)  {
    @Transient
    public var images = mutableListOf<Image>();

    @Transient
    override var type: String = "group"
}