package ar.edu.itba.paw.exceptions;

public class UserAlreadyExistsException extends RuntimeException {
    private final static long serialVersionUID = 7500557750110066474L;
    private final static String MESSAGE = "User already exists";

    public UserAlreadyExistsException() {
        super(MESSAGE);
    }
}
