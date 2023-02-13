package ar.edu.itba.paw.webapp.exceptions;

public class SkyplaceExpiredJwtException extends RuntimeException {

    private static final long serialVersionUID = 4004170147512490855L;
    private static final String MESSAGE = "SkyplaceExpiredJwtException.message";

    public SkyplaceExpiredJwtException(){
        super(MESSAGE);
    }

}
