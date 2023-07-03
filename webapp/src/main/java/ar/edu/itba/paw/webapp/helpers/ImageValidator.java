package ar.edu.itba.paw.webapp.helpers;

import ar.edu.itba.paw.webapp.exceptions.FileNotAnAcceptableImageException;
import org.apache.tika.Tika;

import java.io.IOException;

public class ImageValidator {

    private static final int FILE_SIZE_MB = 5;
    private static final int FILE_SIZE_LIMIT = FILE_SIZE_MB * 1048576;       // 5MB

    public static void ValidateImage(byte[] maybeImage) throws IOException, FileNotAnAcceptableImageException {
        Tika tika = new Tika();
        if(maybeImage.length > FILE_SIZE_LIMIT) {
            throw new FileNotAnAcceptableImageException(FILE_SIZE_MB);
        }
        String mimeType = tika.detect(maybeImage);
        if (!mimeType.startsWith("image/"))
            throw new FileNotAnAcceptableImageException(FILE_SIZE_MB);            // File sent is not an image exception
    }

}
