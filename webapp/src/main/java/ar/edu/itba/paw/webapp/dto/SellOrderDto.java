package ar.edu.itba.paw.webapp.dto;

import ar.edu.itba.paw.model.Category;
import ar.edu.itba.paw.model.SellOrder;
import ar.edu.itba.paw.webapp.helpers.UriPrefix;

import javax.ws.rs.core.UriBuilder;
import javax.ws.rs.core.UriInfo;
import java.math.BigDecimal;
import java.net.URI;

public class SellOrderDto {

    private int id;
    private BigDecimal price;
    private Category category;

    private URI self;
    private URI nft;
    private URI buyorders;

    public static SellOrderDto fromSellOrder(final UriInfo uriInfo, final SellOrder sellOrder){
        final SellOrderDto dto = new SellOrderDto();

        final UriBuilder sellOrderUriBuilder = uriInfo.getBaseUriBuilder().path(UriPrefix.SELLORDERS_PREFIX.getUrl())
                .path(String.valueOf(sellOrder.getId()));

        final UriBuilder nftUriBuilder = uriInfo.getBaseUriBuilder().path(UriPrefix.NFTS_PREFIX.getUrl())
                .path(String.valueOf(sellOrder.getNft().getId()));

        dto.id = sellOrder.getId();
        dto.price = sellOrder.getPrice();
        dto.category = sellOrder.getCategory();
        dto.nft = nftUriBuilder.build();
        dto.self = sellOrderUriBuilder.build();
        dto.buyorders = sellOrderUriBuilder.path("buyorders").build();

        return dto;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }

    public URI getNft() {
        return nft;
    }

    public void setNft(URI nft) {
        this.nft = nft;
    }

    public URI getSelf() {
        return self;
    }

    public void setSelf(URI self) {
        this.self = self;
    }

    public URI getBuyorders() {
        return buyorders;
    }

    public void setBuyorders(URI offers) {
        this.buyorders = offers;
    }
}
