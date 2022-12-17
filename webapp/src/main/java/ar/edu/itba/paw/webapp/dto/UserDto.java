package ar.edu.itba.paw.webapp.dto;

import ar.edu.itba.paw.model.User;

import javax.ws.rs.core.UriBuilder;
import javax.ws.rs.core.UriInfo;
import java.net.URI;

public class UserDto {

    private int id;
    private String username;
    private String wallet;
    private String email;

    private URI self;


    public static UserDto fromUser(final UriInfo uriInfo, final User user) {
        final UserDto userDto = new UserDto();

        final UriBuilder userUriBuilder = uriInfo.getAbsolutePathBuilder().replacePath("users")
                .path(String.valueOf(user.getId()));

        userDto.id = user.getId();
        userDto.username = user.getUsername();
        userDto.email = user.getEmail();
        userDto.wallet = user.getWallet();
        userDto.self = userUriBuilder.build();

        return userDto;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getWallet() {
        return wallet;
    }

    public void setWallet(String wallet) {
        this.wallet = wallet;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public URI getSelf() {
        return self;
    }

    public void setSelf(URI self) {
        this.self = self;
    }
}
