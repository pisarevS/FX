package com.sergey.pisarev.model;

import java.io.IOException;
import java.io.InputStream;

import org.mozilla.universalchardet.UniversalDetector;

/**
 * Character detector implementation based on
 * Google's <a href="http://juniversalchardet.googlecode.com/files/juniversalchardet-1.0.jar">juniversalchardet</a> library.
 */
public class CharacterDetector {

    private static CharacterDetector instance;

    // Private constructor
    private CharacterDetector() {}

    private UniversalDetector detector = new UniversalDetector(null);

    public static final CharacterDetector getInstance(){
        if ( instance == null ) instance = new CharacterDetector();
        return instance;
    }

    /**
     * Detects character encoding for the data from the supplied <tt>stream</tt>.
     * Returns <tt>null</tt> if encoding cannot be determined.
     *
     * @param stream
     * @return name of the detected character encoding, or <tt>null</tt> if encoding cannot be determined
     */
    public String detect(InputStream stream) throws IOException {
        // Reset detector before using
        detector.reset();
        // Buffer
        byte[] buf = new byte[1024];
        try {
            int nread;
            while ((nread = stream.read(buf)) > 0 && !detector.isDone()) {
                detector.handleData(buf, 0, nread);
            }
            detector.dataEnd();
            return detector.getDetectedCharset();
        } finally {
            detector.reset();
        }
    }
}
