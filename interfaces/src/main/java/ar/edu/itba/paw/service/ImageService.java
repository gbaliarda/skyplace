package ar.edu.itba.paw.service;

import ar.edu.itba.paw.model.Image;

import java.util.Optional;

public interface ImageService {

    Image createImage(byte[] image);

    Optional<Image> getImage(int id);
}
