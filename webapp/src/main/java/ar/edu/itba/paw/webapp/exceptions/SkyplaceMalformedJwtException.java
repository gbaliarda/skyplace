package ar.edu.itba.paw.webapp.exceptions;

public class SkyplaceMalformedJwtException extends RuntimeException {

    private static final long serialVersionUID = -8046232031284687636L;
    private static final String MESSAGE = "SkyplaceMalformedJwtException.message";

    public SkyplaceMalformedJwtException() {
        super(MESSAGE);
    }

}
