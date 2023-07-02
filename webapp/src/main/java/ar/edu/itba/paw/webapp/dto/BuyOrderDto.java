package ar.edu.itba.paw.webapp.dto;

import ar.edu.itba.paw.model.BuyOrder;
import ar.edu.itba.paw.model.StatusBuyOrder;
import ar.edu.itba.paw.webapp.helpers.UriPrefix;

import javax.ws.rs.core.UriInfo;
import java.math.BigDecimal;
import java.net.URI;

public class BuyOrderDto {

    private BigDecimal amount;

    private URI sellorder;
    private URI offeredBy;
    private URI self;
    private StatusBuyOrder status;

    public static BuyOrderDto fromBuyOrder(BuyOrder buyOrder, UriInfo uriInfo) {
        final BuyOrderDto buyOrderDto = new BuyOrderDto();

        buyOrderDto.self = uriInfo.getBaseUriBuilder().path(UriPrefix.SELLORDERS_PREFIX.getUrl())
                .path(String.valueOf(buyOrder.getBuyOrderId().getSellOrderId()))
                .path("buyorders").path(String.valueOf(buyOrder.getOfferedBy().getId())).build();

        buyOrderDto.sellorder = uriInfo.getBaseUriBuilder().path(UriPrefix.SELLORDERS_PREFIX.getUrl())
                .path(String.valueOf(buyOrder.getBuyOrderId().getSellOrderId())).build();

        buyOrderDto.offeredBy = uriInfo.getBaseUriBuilder().path(UriPrefix.USERS_PREFIX.getUrl())
                .path(String.valueOf(buyOrder.getOfferedBy().getId())).build();

        buyOrderDto.amount = buyOrder.getAmount();

        buyOrderDto.status = buyOrder.getStatus();

        return buyOrderDto;
    }

    public URI getSellorder() {
        return sellorder;
    }

    public void setSellorder(URI sellorder) {
        this.sellorder = sellorder;
    }

    public URI getOfferedBy() {
        return offeredBy;
    }

    public void setOfferedBy(URI offeredBy) {
        this.offeredBy = offeredBy;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public URI getSelf() {
        return self;
    }

    public void setSelf(URI self) {
        this.self = self;
    }

    public StatusBuyOrder getStatus() {
        return status;
    }

    public void setStatus(StatusBuyOrder status) {
        this.status = status;
    }
}
