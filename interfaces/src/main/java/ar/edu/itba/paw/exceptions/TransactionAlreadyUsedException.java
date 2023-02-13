package ar.edu.itba.paw.exceptions;

public class TransactionAlreadyUsedException extends RuntimeException {

    private static final long serialVersionUID = 2692845794161736374L;
    private static final String MESSAGE = "TransactionAlreadyUsedException.message";

    public TransactionAlreadyUsedException() {
        super(MESSAGE);
    }

}
