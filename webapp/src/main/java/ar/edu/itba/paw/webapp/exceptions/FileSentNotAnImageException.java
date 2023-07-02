package ar.edu.itba.paw.webapp.exceptions;

public class FileSentNotAnImageException extends RuntimeException {
    private static final long serialVersionUID = -3687585050170946133L;
    private static final String MESSAGE = "FileSentNotAnImageException.message";

    public FileSentNotAnImageException() {
        super(MESSAGE);
    }

}
