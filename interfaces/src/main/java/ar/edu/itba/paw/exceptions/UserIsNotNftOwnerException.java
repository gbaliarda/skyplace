package ar.edu.itba.paw.exceptions;

public class UserIsNotNftOwnerException extends RuntimeException{
    private static final long serialVersionUID = 83166227073626301L;
    private final static String MESSAGE = "UserIsNotNftOwnerException.message";

    public UserIsNotNftOwnerException() {
        super(MESSAGE);
    }
}
