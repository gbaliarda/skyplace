package ar.edu.itba.paw.exceptions;

public class InvalidCategoryException extends RuntimeException{
    private static final long serialVersionUID = 8328260306277404303L;
    private final static String MESSAGE = "InvalidCategoryException.message";

    public InvalidCategoryException() {
        super(MESSAGE);
    }
}
