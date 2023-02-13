package ar.edu.itba.paw.exceptions;

public class BuyOrderIsNotPendingException extends RuntimeException {

    private static final long serialVersionUID = -6046472176118971378L;
    private static final String MESSAGE = "BuyOrderIsNotPendingException.message";

    public BuyOrderIsNotPendingException() {
        super(MESSAGE);
    }

}
