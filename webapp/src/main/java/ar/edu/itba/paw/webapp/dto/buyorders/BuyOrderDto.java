package ar.edu.itba.paw.webapp.dto.buyorders;

import ar.edu.itba.paw.model.BuyOrder;

import javax.ws.rs.core.UriInfo;
import java.math.BigDecimal;
import java.net.URI;

public class BuyOrderDto {

    private BigDecimal amount;

    private URI sellorder;
    private URI offeredBy;
    private URI self;

    private final static String USERS_URI_PREFIX = "users";
    private final static String SELLORDERS_URI_PREFIX = "sellorders";
    private final static String BUYORDERS_URI_PREFIX = "buyorders";

    public static BuyOrderDto fromBuyOrder(BuyOrder buyOrder, UriInfo uriInfo) {
        final BuyOrderDto buyOrderDto = new BuyOrderDto();

        buyOrderDto.self = uriInfo.getBaseUriBuilder().path(SELLORDERS_URI_PREFIX)
                .path(String.valueOf(buyOrder.getBuyOrderId().getSellOrderId()))
                .path(BUYORDERS_URI_PREFIX).path(String.valueOf(buyOrder.getOfferedBy().getId())).build();

        buyOrderDto.sellorder = uriInfo.getBaseUriBuilder().path(SELLORDERS_URI_PREFIX)
                .path(String.valueOf(buyOrder.getBuyOrderId().getSellOrderId())).build();

        buyOrderDto.offeredBy = uriInfo.getBaseUriBuilder().path(USERS_URI_PREFIX)
                .path(String.valueOf(buyOrder.getOfferedBy().getId())).build();

        buyOrderDto.amount = buyOrder.getAmount();

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
}
