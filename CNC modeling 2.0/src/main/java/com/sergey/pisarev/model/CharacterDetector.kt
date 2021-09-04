package com.sergey.pisarev.model

import org.mozilla.universalchardet.UniversalDetector
import kotlin.Throws
import java.io.IOException
import com.sergey.pisarev.model.CharacterDetector
import java.io.InputStream

/**
 * Character detector implementation based on
 * Google's [juniversalchardet](http://juniversalchardet.googlecode.com/files/juniversalchardet-1.0.jar) library.
 */
class CharacterDetector  // Private constructor
private constructor() {
    private val detector = UniversalDetector(null)

    /**
     * Detects character encoding for the data from the supplied <tt>stream</tt>.
     * Returns <tt>null</tt> if encoding cannot be determined.
     *
     * @param stream
     * @return name of the detected character encoding, or <tt>null</tt> if encoding cannot be determined
     */
    @Throws(IOException::class)
    fun detect(stream: InputStream): String {
        // Reset detector before using
        detector.reset()
        // Buffer
        val buf = ByteArray(1024)
        return try {
            var nread: Int
            while (stream.read(buf).also { nread = it } > 0 && !detector.isDone) {
                detector.handleData(buf, 0, nread)
            }
            detector.dataEnd()
            detector.detectedCharset
        } finally {
            detector.reset()
        }
    }

    companion object {
        @JvmStatic
        var instance: CharacterDetector? = null
            get() {
                if (field == null) field = CharacterDetector()
                return field
            }
            private set
    }
}