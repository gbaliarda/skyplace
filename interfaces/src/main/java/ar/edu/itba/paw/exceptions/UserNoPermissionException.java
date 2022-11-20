package ar.edu.itba.paw.exceptions;

public class UserNoPermissionException extends RuntimeException{
    private static final long serialVersionUID = -7713296866908933956L;
    private final static String MESSAGE = "User has no permission for attempted action";

    public UserNoPermissionException() {
        super(MESSAGE);
    }
}
