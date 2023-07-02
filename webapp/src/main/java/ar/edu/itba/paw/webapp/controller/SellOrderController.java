package ar.edu.itba.paw.webapp.controller;

import ar.edu.itba.paw.exceptions.BuyOrderNotFoundException;
import ar.edu.itba.paw.exceptions.SellOrderNotFoundException;
import ar.edu.itba.paw.exceptions.UserNoPermissionException;
import ar.edu.itba.paw.exceptions.UserNotFoundException;
import ar.edu.itba.paw.model.*;
import ar.edu.itba.paw.service.*;
import ar.edu.itba.paw.webapp.dto.BuyOrderDto;
import ar.edu.itba.paw.webapp.dto.SellOrderDto;
import ar.edu.itba.paw.webapp.dto.txHashDto;
import ar.edu.itba.paw.webapp.exceptions.NoBodyException;
import ar.edu.itba.paw.webapp.form.CreateSellOrderForm;
import ar.edu.itba.paw.webapp.form.PriceForm;
import ar.edu.itba.paw.webapp.form.SellNftForm;
import ar.edu.itba.paw.webapp.helpers.ResponseHelpers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.validation.Valid;
import javax.ws.rs.*;
import javax.ws.rs.core.*;
import java.net.URI;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Path("/api/sellorders")
@Component
public class SellOrderController {

    private final SellOrderService sellOrderService;
    private final BuyOrderService buyOrderService;
    private final UserService userService;

    @Context
    private UriInfo uriInfo;

    @Autowired
    public SellOrderController(SellOrderService sellOrderService, BuyOrderService buyOrderService, UserService userService) {
        this.sellOrderService = sellOrderService;
        this.buyOrderService = buyOrderService;
        this.userService = userService;
    }

    @POST
    @Consumes({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_FORM_URLENCODED, })
    public Response createSellOrder(@Valid CreateSellOrderForm form) {
        if(form == null)
            throw new NoBodyException();
        final SellOrder newSellOrder = sellOrderService.create(form.getPrice(), form.getNftId(), form.getCategory());
        final URI location = uriInfo.getAbsolutePathBuilder()
                .path(String.valueOf(newSellOrder.getId())).build();
        return Response.created(location).build();
    }

    @GET
    @Path("/{id}")
    @Produces({ MediaType.APPLICATION_JSON, })
    public Response getSellOrder(@PathParam("id") int id) {
        Optional<SellOrder> maybeSellOrder = sellOrderService.getOrderById(id);
        if (!maybeSellOrder.isPresent())
            throw new SellOrderNotFoundException();

        SellOrderDto dto = SellOrderDto.fromSellOrder(uriInfo, maybeSellOrder.get());
        return Response.ok(dto).build();
    }

    @PUT
    @Path("/{id}")
    @Consumes({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_FORM_URLENCODED, })
    public Response updateSellOrder(@PathParam("id") int id, @Valid SellNftForm form){
        if(form == null)
            throw new NoBodyException();
        boolean result = sellOrderService.update(id, form.getCategory(), form.getPrice());
        if(result)
            return Response.noContent().build();
        throw new SellOrderNotFoundException();
    }

    @DELETE
    @Path("/{id}")
    public Response deleteSellOrder(@PathParam("id") int id) {
        sellOrderService.delete(id);
        return Response.noContent().build();
    }

    @Consumes({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_FORM_URLENCODED })
    @POST
    @Path("/{id}/buyorders")
    public Response createBuyOrder(@PathParam("id") int id, @Valid final PriceForm priceForm) {
        if(priceForm == null)
            throw new NoBodyException();
        Optional<User> maybeUser = userService.getCurrentUser();
        if(!maybeUser.isPresent())
            throw new UserNotFoundException();
        int currentUserId = maybeUser.get().getId();
        Optional<SellOrder> maybeSellOrder = this.sellOrderService.getOrderById(id);
        if (!maybeSellOrder.isPresent())
            throw new SellOrderNotFoundException();

        buyOrderService.create(id, priceForm.getPrice(), currentUserId);
        final URI location = uriInfo.getAbsolutePathBuilder()
                .path(String.valueOf(currentUserId)).build();
        return Response.created(location).build();
    }

    @GET
    @Path("/{id}/buyorders")
    @Produces({ MediaType.APPLICATION_JSON})
    public Response getBuyOrdersBySellOrder(
            @PathParam("id") int id,
            @QueryParam("page") @DefaultValue("1") int offerPage,
            @QueryParam("status") @DefaultValue("ALL") String status
    ){
        Optional<SellOrder> maybeSellOrder = this.sellOrderService.getOrderById(id);
        if (!maybeSellOrder.isPresent())
            throw new SellOrderNotFoundException();

        int amountOfferPages = buyOrderService.getAmountPagesBySellOrderId(maybeSellOrder.get(), status);

        List<BuyOrderDto> buyOrdersList = buyOrderService.getOrdersBySellOrderId(offerPage, maybeSellOrder.get().getId(), status).stream().map(n -> BuyOrderDto.fromBuyOrder(n, uriInfo)).collect(Collectors.toList());

        Map<String, Object[]> queryParams = new HashMap<>();
        queryParams.put("status", new Object[]{status});
        Response.ResponseBuilder responseBuilder = Response.ok(new GenericEntity<List<BuyOrderDto>>(buyOrdersList) {});
        ResponseHelpers.addTotalPagesHeader(responseBuilder, amountOfferPages);
        return ResponseHelpers.addLinkAttributes(responseBuilder, uriInfo, offerPage, amountOfferPages, queryParams).build();
    }

    @DELETE
    @Path("/{id}/buyorders/{userId}")
    public Response deleteBuyOrderFromUserId(@PathParam("id") int id, @PathParam("userId") int userId) {
        buyOrderService.deleteBuyOrder(id, userId);
        return Response.status(Response.Status.NO_CONTENT).build();
    }

    @PUT
    @Path("/{id}/buyorders/{userId}/")
    public Response acceptBuyOrderFromOwnerId(@PathParam("id") int id, @PathParam("userId") int OwnerId) {
        buyOrderService.acceptBuyOrder(id, OwnerId);
        return Response.ok().build();
    }

    @GET
    @Path("/{id}/buyorders/{userId}/")
    @Produces({ MediaType.APPLICATION_JSON, })
    public Response getBuyOrderFromUserId(@PathParam("id") int id, @PathParam("userId") int userId) {
        Optional<BuyOrder> buyorder = buyOrderService.getBuyOrder(id, userId);
        if(buyorder.isPresent()) {
            BuyOrderDto dto = BuyOrderDto.fromBuyOrder(buyorder.get(), uriInfo);
            return Response.ok(dto).build();
        }
        throw new BuyOrderNotFoundException();
    }

    @POST
    @Consumes({ MediaType.APPLICATION_JSON })
    @Path("/{id}/buyorders/{userId}")
    public Response confirmBuyOrderFromBuyerId(@PathParam("id") int sellOrderId, @PathParam("userId") int buyerId, final @Valid txHashDto txHashDto) {
        Optional<Integer> response = buyOrderService.validateTransaction(txHashDto.getTxHash(), sellOrderId, buyerId);
        if (!response.isPresent())
            throw new UserNoPermissionException();

        final URI purchaseUri = uriInfo.getAbsolutePathBuilder()
                .replacePath("/purchases/").path(response.get().toString()).build();
        return Response.created(purchaseUri).build();

    }

}
