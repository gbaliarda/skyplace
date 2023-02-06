package ar.edu.itba.paw.persistence;

import ar.edu.itba.paw.model.Image;

import java.util.Optional;

public interface ImageDao {

    Image createImage(byte[] image);

    Optional<Image> getImage(int id);

}
