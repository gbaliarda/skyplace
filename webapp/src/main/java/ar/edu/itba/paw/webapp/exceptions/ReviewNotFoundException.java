package ar.edu.itba.paw.webapp.exceptions;

public class ReviewNotFoundException extends RuntimeException{

    private static final long serialVersionUID = 4190830333654498216L;
    private static final String MESSAGE = "ReviewNotFoundException.message";

    public ReviewNotFoundException() {
        super(MESSAGE);
    }

}
