package ar.edu.itba.paw.webapp.controller;

import ar.edu.itba.paw.model.*;
import ar.edu.itba.paw.service.BuyOrderService;
import ar.edu.itba.paw.service.NftService;
import ar.edu.itba.paw.service.SellOrderService;
import ar.edu.itba.paw.service.UserService;
import ar.edu.itba.paw.webapp.dto.BuyOrderDto;
import ar.edu.itba.paw.webapp.dto.SellOrderDto;
import ar.edu.itba.paw.webapp.form.CreateSellOrderForm;
import ar.edu.itba.paw.webapp.form.PriceForm;
import ar.edu.itba.paw.webapp.form.SellNftForm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.validation.Valid;
import javax.ws.rs.*;
import javax.ws.rs.core.*;
import java.math.BigDecimal;
import java.net.URI;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Path("sellorders")
@Component
public class SellOrderController {

    private final SellOrderService sellOrderService;
    private final NftService nftService;
    private final UserService userService;

    private final BuyOrderService buyOrderService;

    @Context
    private UriInfo uriInfo;

    @Autowired
    public SellOrderController(SellOrderService sellOrderService, UserService userService, NftService nftService, BuyOrderService buyOrderService) {
        this.sellOrderService = sellOrderService;
        this.userService = userService;
        this.nftService = nftService;
        this.buyOrderService = buyOrderService;
    }

    @GET
    @Produces({ MediaType.APPLICATION_JSON, })
    public Response listSellOrders(
            @QueryParam("page") @DefaultValue("1") final int page,
            @QueryParam("minPrice") @DefaultValue("0") final BigDecimal minPrice,
            @QueryParam("maxPrice") @DefaultValue("-1") final BigDecimal maxPrice,
            @QueryParam("category") final String category,
            @QueryParam("chain") final String chain,
            @QueryParam("sort") final String sort,
            @QueryParam("search") final String search,
            @QueryParam("searchFor") final String searchFor,
            @QueryParam("seller") final Integer sellerId
    ) {
        Stream<Nft> stream = nftService.getAll(page, "onSale", category, chain, minPrice, maxPrice, sort, search, searchFor)
                .stream();

        // TODO: Get only user nfts instead of all and then filter
        // Current problem: getAllPublicationsByUser used does not support all filtering query params
        if(sellerId != null) {
            stream = stream.filter(n -> n.getOwner().getId() == sellerId);
        }
        List<SellOrderDto> sellOrderList = stream.map(Nft::getSellOrder).map(s -> SellOrderDto.fromSellOrder(uriInfo, s)).collect(Collectors.toList());

        if(sellOrderList.isEmpty())
            return Response.noContent().build();

        Response.ResponseBuilder responseBuilder = Response.ok(new GenericEntity<List<SellOrderDto>>(sellOrderList) {});
        if (page > 1)
            responseBuilder.link(uriInfo.getAbsolutePathBuilder().queryParam("page", page - 1).build(), "prev");
        int lastPage = (int) Math.ceil(nftService.getAmountPublications("onSale", category, chain, minPrice, maxPrice, sort, search, "") / (double) nftService.getPageSize());
        if (page < lastPage)
            responseBuilder.link(uriInfo.getAbsolutePathBuilder().queryParam("page", page + 1).build(), "next");

        return responseBuilder
                .link(uriInfo.getAbsolutePathBuilder().queryParam("page", 1).build(), "first")
                .link(uriInfo.getAbsolutePathBuilder().queryParam("page", lastPage).build(), "last")
                .build();
    }

    @POST
    @Consumes({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_FORM_URLENCODED, })
    public Response createSellOrder(@Valid CreateSellOrderForm form) {
        // TODO: Return other response in case of exception thrown
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
        if(maybeSellOrder.isPresent()){
            SellOrderDto dto = SellOrderDto.fromSellOrder(uriInfo, maybeSellOrder.get());
            return Response.ok(dto).build();
        }
        return Response.status(Response.Status.NOT_FOUND).build();
    }

    @PUT
    @Path("/{id}")
    @Consumes({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_FORM_URLENCODED, })
    public Response updateSellOrder(@PathParam("id") int id, @Valid SellNftForm form){
        boolean result = sellOrderService.update(id, form.getCategory(), form.getPrice());
        if(result)
            return Response.noContent().build();
        return Response.status(Response.Status.NOT_FOUND).build();
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
        //TODO GET CURRENT USER ID TO REGISTER THIS BUYORDER
        //TODO FORM VALIDATION ERROR MESSAGES

        Optional<SellOrder> maybeSellOrder = this.sellOrderService.getOrderById(id);
        if (!maybeSellOrder.isPresent()) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }

        int userId = 69; //NEED TO CORRECT THIS

        buyOrderService.create(id, priceForm.getPrice(), userId);
        // WHAT URI TO RETURN ??
        final URI location = uriInfo.getAbsolutePathBuilder()
                .path(id + "buyorders").build();
        return Response.created(location).build();
    }

    @GET
    @Path("/{id}/buyorders")
    @Produces({ MediaType.APPLICATION_JSON})
    public Response getBuyOrdersBySellOrder(@PathParam("id") int id, @QueryParam("page") @DefaultValue("1") int offerPage){

        Optional<SellOrder> maybeSellOrder = this.sellOrderService.getOrderById(id);
        if (!maybeSellOrder.isPresent()) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }

        long amountOfferPages;
        amountOfferPages = buyOrderService.getAmountPagesBySellOrderId(maybeSellOrder.get());

        if(offerPage > amountOfferPages || offerPage < 0 ) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }

        List<BuyOrderDto> buyOffers;
        buyOffers = buyOrderService.getOrdersBySellOrderId(offerPage, maybeSellOrder.get().getId()).stream().map(n -> BuyOrderDto.fromBuyOrder(n, uriInfo)).collect(Collectors.toList());

        if(buyOffers.isEmpty()){
            return Response.noContent().build();
        }
        Response.ResponseBuilder responseBuilder = Response.ok(new GenericEntity<List<BuyOrderDto>>(buyOffers) {});
        if (offerPage > 1)
            responseBuilder.link(uriInfo.getAbsolutePathBuilder().queryParam("page", offerPage - 1).build(), "prev");
        if (offerPage < amountOfferPages)
            responseBuilder.link(uriInfo.getAbsolutePathBuilder().queryParam("page", offerPage + 1).build(), "next");
        return responseBuilder
                .link(uriInfo.getAbsolutePathBuilder().queryParam("page", 1).build(), "first")
                .link(uriInfo.getAbsolutePathBuilder().queryParam("page", amountOfferPages).build(), "last")
                .build();

    }

    @DELETE
    @Path("/{id}/buyorders/{userId}")
    public Response deleteBuyOrderFromUserId(@PathParam("id") int id, @PathParam("userId") int userId) {

        //TODO VALIDATE USER PERMISSIONS
        Optional<SellOrder> maybeSellOrder = this.sellOrderService.getOrderById(id);
        if (!maybeSellOrder.isPresent()) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }

        buyOrderService.deleteBuyOrder(id, userId);
        return Response.noContent().build();
    }

    @PUT
    @Path("/{id}/buyorders/{userId}/accept")
    public Response acceptBuyOrderFromOwnerId(@PathParam("id") int id, @PathParam("userId") int OwnerId) {

        Optional<User> maybeUser = this.userService.getUserById(OwnerId);
        Optional<SellOrder> maybeSellOrder = this.sellOrderService.getOrderById(id);

        if (!maybeSellOrder.isPresent() || !maybeUser.isPresent()) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        if (!sellOrderService.userOwnsSellOrder(id, maybeUser.get())) {
            return Response.status(Response.Status.FORBIDDEN).build();
        }

        buyOrderService.acceptBuyOrder(id, OwnerId);
        return Response.ok().build();

    }

    @PUT
    @Path("/{id}/buyorders/{userId}/reject")
    public Response rejectBuyOrderFromOwnerId(@PathParam("id") int id, @PathParam("userId") int ownerId) {

        Optional<User> maybeUser = this.userService.getUserById(ownerId);
        Optional<SellOrder> maybeSellOrder = this.sellOrderService.getOrderById(id);

        if (!maybeSellOrder.isPresent() || !maybeUser.isPresent()) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        if (!sellOrderService.userOwnsSellOrder(id, maybeUser.get())) {
            return Response.status(Response.Status.FORBIDDEN).build();
        }

        buyOrderService.rejectBuyOrder(id, ownerId);
        return Response.ok().build();

    }

}
