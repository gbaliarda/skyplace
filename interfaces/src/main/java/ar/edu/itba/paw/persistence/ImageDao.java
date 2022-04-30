package ar.edu.itba.paw.persistence;

import ar.edu.itba.paw.model.Image;
import org.springframework.web.multipart.MultipartFile;

import java.util.Optional;

public interface ImageDao {

    Optional<Long> createImage(MultipartFile image);

    Image getImage(long id);
}