package ar.edu.itba.paw.webapp.exceptions;

public class FileNotAnAcceptableImageException extends RuntimeException {
    private static final long serialVersionUID = -3687585050170946133L;
    private static final String MESSAGE = "FileNotAnAcceptableImageException.message";

    private final int limitMB;

    public FileNotAnAcceptableImageException(int limitMB) {
        super(MESSAGE);
        this.limitMB = limitMB;
    }

    public int getLimitMB() {
        return limitMB;
    }
}
