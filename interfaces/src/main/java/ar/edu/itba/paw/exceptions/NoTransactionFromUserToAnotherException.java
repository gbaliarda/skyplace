package ar.edu.itba.paw.exceptions;

import java.math.BigDecimal;

public class NoTransactionFromUserToAnotherException extends RuntimeException {

    private static final long serialVersionUID = 2634645684463220191L;
    private static final String MESSAGE = "NoTransactionFromUserToAnotherException.message";

    private final String from;
    private final String to;
    private final BigDecimal amount;

    public NoTransactionFromUserToAnotherException(String from, String to, BigDecimal amount) {
        super(MESSAGE);
        this.from = from;
        this.to = to;
        this.amount = amount;
    }

    public String getFrom() {
        return from;
    }

    public String getTo() {
        return to;
    }

    public BigDecimal getAmount() {
        return amount;
    }
}
