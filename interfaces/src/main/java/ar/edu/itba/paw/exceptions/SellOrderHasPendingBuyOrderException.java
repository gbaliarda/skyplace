package ar.edu.itba.paw.exceptions;

public class SellOrderHasPendingBuyOrderException extends RuntimeException{
    private static final long serialVersionUID = 2222156174076927700L;
    private static final String MESSAGE = "SellOrderHasPendingBuyOrderException.message";

    public SellOrderHasPendingBuyOrderException() {
        super(MESSAGE);
    }
}
