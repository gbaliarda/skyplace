package ar.edu.itba.paw.exceptions;

public class BuyOrderNotFoundException extends RuntimeException {

    private static final long serialVersionUID = 1705136425025419031L;
    private static final String MESSAGE = "BuyOrderNotFoundException.message";

    public BuyOrderNotFoundException() {
        super(MESSAGE);
    }


}
