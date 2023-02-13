package ar.edu.itba.paw.exceptions;

public class UserNotFoundException extends RuntimeException{
    private static final long serialVersionUID = 6547590290096537427L;
    private final static String MESSAGE = "UserNotFoundException.message";

    public UserNotFoundException() {
        super(MESSAGE);
    }
}
