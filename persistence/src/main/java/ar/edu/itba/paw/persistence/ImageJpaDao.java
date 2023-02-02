package ar.edu.itba.paw.persistence;

import ar.edu.itba.paw.model.Image;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;
import org.springframework.web.multipart.MultipartFile;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.io.IOException;
import java.util.Optional;

@Repository
public class  ImageJpaDao implements ImageDao {

    private static final Logger LOGGER = LoggerFactory.getLogger(ImageJpaDao.class);

    @PersistenceContext
    private EntityManager em;

    /**
     * @param image File that contains the image
     * @return The new image entity created.
     */
    @Override
    public Image createImage(byte[] image) {
        Image newImage = new Image(image);
        em.persist(newImage);
        return newImage; // has ID field populated by JPA
    }

    /**
     * @param image File that contains the image
     * @return The new image entity created.
     */
    @Deprecated
    public Image createImage(MultipartFile image) {
        Image newImage = null;
        try {
            newImage = new Image(image.getBytes());
            em.persist(newImage);
        } catch (IOException e) {
            LOGGER.error("Error creating Image: {}", e.getMessage());
        }
        return newImage; // has ID field populated by JPA
    }

    /**
     * @param id id of the image to retrieve
     * @return Optional of the image with an id that may or may not exist
     */
    @Override
    public Optional<Image> getImage(int id) {
        return Optional.ofNullable(em.find(Image.class, id));
    }

}
