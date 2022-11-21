package ar.edu.itba.paw.webapp.dto;

import ar.edu.itba.paw.model.BuyOrder;

import javax.ws.rs.core.UriInfo;
import java.math.BigDecimal;
import java.net.URI;

public class BuyOrderDto {

    private URI sellorder;
    private UserDto offeredBy;
    private BigDecimal amount;
    private URI self;

    public static BuyOrderDto fromBuyOrder(BuyOrder buyOrder, UriInfo uriInfo) {
        final BuyOrderDto buyOrderDto = new BuyOrderDto();


        buyOrderDto.offeredBy = UserDto.fromUser(uriInfo, buyOrder.getOfferedBy());
        buyOrderDto.amount = buyOrder.getAmount();
        buyOrderDto.self = uriInfo.getAbsolutePathBuilder().replacePath("sellorders").path(String.valueOf(buyOrder.getBuyOrderId().getSellOrderId())).path("buyorders").build();
        buyOrderDto.sellorder = uriInfo.getAbsolutePathBuilder().replacePath("sellorders").path(String.valueOf(buyOrder.getBuyOrderId().getSellOrderId())).build();

        return buyOrderDto;
    }

    public URI getSellorder() {
        return sellorder;
    }

    public void setSellorder(URI sellorder) {
        this.sellorder = sellorder;
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
