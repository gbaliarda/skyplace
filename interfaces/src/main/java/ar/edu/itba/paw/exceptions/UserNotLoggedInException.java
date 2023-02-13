package ar.edu.itba.paw.exceptions;

public class UserNotLoggedInException extends RuntimeException{
    private static final long serialVersionUID = 5937742006603835603L;
    private final static String MESSAGE = "UserNotLoggedInException.message";

    public UserNotLoggedInException() {
        super(MESSAGE);
    }
}
