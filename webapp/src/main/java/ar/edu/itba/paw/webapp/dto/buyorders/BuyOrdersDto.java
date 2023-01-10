package ar.edu.itba.paw.webapp.dto.buyorders;

import java.util.List;

public class BuyOrdersDto {

    private long totalPages;
    private List<BuyOrderDto> buyorders;

    public static BuyOrdersDto fromBuyOrdersList(List<BuyOrderDto> buyorders, long totalPages) {
        final BuyOrdersDto buyOrdersDto = new BuyOrdersDto();

        buyOrdersDto.buyorders = buyorders;
        buyOrdersDto.totalPages = totalPages;

        return buyOrdersDto;
    }

    public long getTotalPages() { return totalPages; }

    public void setTotalPages(long totalPages) { this.totalPages = totalPages; }

    public List<BuyOrderDto> getBuyorders() { return buyorders; }

    public void setBuyorders(List<BuyOrderDto> buyorders) { this.buyorders = buyorders; }
}
