package ar.edu.itba.paw.webapp.dto.nfts;

import java.util.List;

public class NftsDto {

    private int total;
    private List<NftDto> nfts;

    public static NftsDto fromNftList(final List<NftDto> nfts, final int total){
        final NftsDto dto = new NftsDto();

        dto.nfts = nfts;
        dto.total = total;

        return dto;
    }

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }

    public List<NftDto> getNfts() {
        return nfts;
    }

    public void setNfts(List<NftDto> nfts) {
        this.nfts = nfts;
    }
}