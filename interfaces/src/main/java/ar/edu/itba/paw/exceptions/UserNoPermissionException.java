package ar.edu.itba.paw.exceptions;

public class UserNoPermissionException extends RuntimeException{
    private static final long serialVersionUID = -7713296866908933956L;
    private final static String MESSAGE = "UserNoPermissionException.message";

    public UserNoPermissionException() {
        super(MESSAGE);
    }
}
