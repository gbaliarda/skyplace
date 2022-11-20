package ar.edu.itba.paw.webapp.dto;

import ar.edu.itba.paw.model.BuyOrder;

import javax.ws.rs.core.UriInfo;
import java.math.BigDecimal;
import java.net.URI;

public class BuyOrderDto {

    private int sellOrderId;
    private UserDto offeredBy;
    private BigDecimal amount;
    private URI self;

    public static BuyOrderDto fromBuyOrder(BuyOrder buyOrder, UriInfo uriInfo) {
        final BuyOrderDto buyOrderDto = new BuyOrderDto();

        buyOrderDto.sellOrderId = buyOrder.getBuyOrderId().getSellOrderId();
        buyOrderDto.offeredBy = UserDto.fromUser(uriInfo, buyOrder.getOfferedBy());
        buyOrderDto.amount = buyOrder.getAmount();
        buyOrderDto.self = uriInfo.getAbsolutePathBuilder().replacePath("sellorders").path(String.valueOf(buyOrder.getBuyOrderId().getSellOrderId())).path("buyorders").build();

        return buyOrderDto;
    }

    public int getSellOrderId() {
        return sellOrderId;
    }

    public void setSellOrderId(int sellOrderId) {
        this.sellOrderId = sellOrderId;
    }


    public UserDto getOfferedBy() {
        return offeredBy;
    }

    public void setOfferedBy(UserDto offeredBy) {
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
