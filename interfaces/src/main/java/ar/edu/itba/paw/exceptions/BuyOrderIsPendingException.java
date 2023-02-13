package ar.edu.itba.paw.exceptions;

public class BuyOrderIsPendingException extends RuntimeException{

    private static final long serialVersionUID = -6581385921969230232L;
    public static final String MESSAGE = "BuyOrderIsPendingException.message";

    public BuyOrderIsPendingException() {
        super(MESSAGE);
    }
}
