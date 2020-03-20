package eu.glatz.sifidraw

import ij.IJ
import ij.ImagePlus
import ij.plugin.filter.PlugInFilter
import ij.plugin.filter.PlugInFilter.DOES_ALL
import ij.process.ImageProcessor
import java.lang.IllegalArgumentException

class Filter_Plugin : PlugInFilter {
    var imp: ImagePlus? = null
    override fun setup(arg: String, imp: ImagePlus): Int {
        this.imp = imp
        return DOES_ALL
    }

    override fun run(ip: ImageProcessor) {
        ip.invert()
        IJ.log("icon double-clicked");
        throw IllegalArgumentException("hallo")
    }
}