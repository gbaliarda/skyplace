package ar.edu.itba.paw.webapp.controller;

import ar.edu.itba.paw.model.Image;
import ar.edu.itba.paw.service.ImageService;
import ar.edu.itba.paw.webapp.dto.ImageDto;
import org.apache.commons.io.FileUtils;
import org.apache.tika.Tika;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.ws.rs.*;
import javax.ws.rs.core.*;
import java.io.File;
import java.util.Optional;

@Path("/api/images")
@Component
public class ImageController {

    private final ImageService imageService;

    @Context
    private UriInfo uriInfo;

    @Autowired
    public ImageController(ImageService imageService) {
        this.imageService = imageService;
    }

    @GET
    @Path("/{id}")
    public Response getImage(@PathParam("id") int id) {
        Optional<Image> maybeImage = imageService.getImage(id);
        if(!maybeImage.isPresent())
            throw new NotFoundException("Image not found");

        Tika tika = new Tika();
        String mimeType = tika.detect(maybeImage.get().getImage());

        CacheControl cacheControl = new CacheControl();
        cacheControl.setPrivate(false);
        cacheControl.setMaxAge(60 * 60 * 24);

        return Response.ok(maybeImage.get().getImage())
                .cacheControl(cacheControl)
                .type(mimeType)
                .build();
    }

}
