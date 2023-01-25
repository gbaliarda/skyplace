package ar.edu.itba.paw.webapp.dto;


import javax.validation.constraints.Size;

public class txHashDto {
@Size(min = 66, max = 66)
    private  String txHash;

    public String getTxHash() {
        return txHash;
    }

    public void setTxHash(String  txHash) {
        this.txHash = txHash;
    }
}
