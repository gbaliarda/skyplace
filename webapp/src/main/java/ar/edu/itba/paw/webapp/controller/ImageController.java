package ar.edu.itba.paw.webapp.controller;

import ar.edu.itba.paw.model.Image;
import ar.edu.itba.paw.service.ImageService;
import ar.edu.itba.paw.webapp.dto.ImageDto;
import org.apache.commons.io.FileUtils;
import org.apache.tika.Tika;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
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

        return Response.ok(maybeImage.get().getImage())
                .type(mimeType) // Set the type of the response to image/jpeg
                .build();
    }

}
