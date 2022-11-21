package ar.edu.itba.paw.webapp.exceptions;

public class NoBodyException extends RuntimeException {

    private static final long serialVersionUID = 7527021052997094720L;
    private static final String MESSAGE = "Request must contain a body";

    public NoBodyException() {
        super(MESSAGE);
    }
}
