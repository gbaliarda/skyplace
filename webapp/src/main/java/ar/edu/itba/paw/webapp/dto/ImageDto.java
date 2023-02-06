package ar.edu.itba.paw.webapp.dto;

import ar.edu.itba.paw.model.Image;

import javax.ws.rs.core.UriBuilder;
import javax.ws.rs.core.UriInfo;
import java.net.URI;

public class ImageDto {

    private int id;
    private byte[] image;

    private URI self;

    public static ImageDto fromImage(final UriInfo uriInfo, final Image image) {
        final ImageDto dto = new ImageDto();

        final UriBuilder imageUriBuilder = uriInfo.getBaseUriBuilder().path("images")
                .path(String.valueOf(image.getIdImage()));

        dto.self = imageUriBuilder.build();
        dto.id = image.getIdImage();
        dto.image = image.getImage();

        return dto;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public byte[] getImage() {
        return image;
    }

    public void setImage(byte[] image) {
        this.image = image;
    }

    public URI getSelf() {
        return self;
    }

    public void setSelf(URI self) {
        this.self = self;
    }
}
