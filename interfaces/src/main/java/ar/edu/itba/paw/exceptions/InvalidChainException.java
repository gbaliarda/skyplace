package ar.edu.itba.paw.exceptions;

public class InvalidChainException extends RuntimeException{
    private final static long serialVersionUID = -2486326048918528005L;
    private final static String MESSAGE = "InvalidChainException.message";

    public InvalidChainException() {
        super(MESSAGE);
    }
}
