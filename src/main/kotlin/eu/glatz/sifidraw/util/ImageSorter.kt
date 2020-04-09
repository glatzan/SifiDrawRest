package eu.glatz.sifidraw.util

import eu.glatz.sifidraw.model.SAImage
import eu.glatz.sifidraw.model.SIHasImages
import java.math.BigInteger
import java.util.regex.Pattern
import kotlin.math.min


object ImageSorter : Comparator<SAImage> {

    private val NUMBERS: Pattern = Pattern.compile("(?<=\\D)(?=\\d)|(?<=\\d)(?=\\D)")

    override fun compare(p0: SAImage?, p1: SAImage?): Int {
        if (p0 == null || p1 == null) return if (p1 == null) if (p0 == null) 0 else -1 else 1

        // Splitting both input strings by the above patterns
        // Splitting both input strings by the above patterns
        val split1: Array<String> = NUMBERS.split(p0.name)
        val split2: Array<String> = NUMBERS.split(p1.name)
        for (i in 0 until min(split1.size, split2.size)) {
            val c1 = split1[i][0]
            val c2 = split2[i][0]
            var cmp = 0
            // If both segments start with a digit, sort them numerically using
            // BigInteger to stay safe
            if (c1 in '0'..'9' && c2.toInt() >= 0 && c2 <= '9') cmp = BigInteger(split1[i]).compareTo(BigInteger(split2[i]))
            // If we haven't sorted numerically before, or if numeric sorting yielded
            // equality (e.g 007 and 7) then sort lexicographically
            if (cmp == 0) cmp = split1[i].compareTo(split2[i])
            // Abort once some prefix has unequal ordering
            if (cmp != 0) return cmp
        }

        // If we reach this, then both strings have equally ordered prefixes, but
        // maybe one string is longer than the other (i.e. has more segments)
        return split1.size - split2.size;
    }


    fun sort(parent: SIHasImages) : SIHasImages {
        parent.images.sortWith(ImageSorter)
        for (img in parent.images) {
            if (img is SIHasImages)
                sort(img)
        }
        return parent
    }
}