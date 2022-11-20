package ar.edu.itba.paw.exceptions;

public class NftAlreadyHasSellOrderException extends RuntimeException{
    private static final long serialVersionUID = -4759678786764247845L;
    private static final String MESSAGE = "Nft already has an active sell order";

    public NftAlreadyHasSellOrderException() {
        super(MESSAGE);
    }
}
