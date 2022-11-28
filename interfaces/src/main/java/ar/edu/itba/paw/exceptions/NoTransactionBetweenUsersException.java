package ar.edu.itba.paw.exceptions;

public class NoTransactionBetweenUsersException extends RuntimeException{

    private static final long serialVersionUID = -3859461206734273713L;
    private static final String MESSAGE = "No transaction between users sent";

    public NoTransactionBetweenUsersException() {
        super(MESSAGE);
    }
}
