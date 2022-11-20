package ar.edu.itba.paw.webapp.controller;


import ar.edu.itba.paw.model.Publication;
import ar.edu.itba.paw.model.Review;
import ar.edu.itba.paw.model.User;
import ar.edu.itba.paw.service.BuyOrderService;
import ar.edu.itba.paw.service.NftService;
import ar.edu.itba.paw.service.ReviewService;
import ar.edu.itba.paw.service.UserService;
import ar.edu.itba.paw.webapp.dto.BuyOrderDto;
import ar.edu.itba.paw.webapp.dto.NftDto;
import ar.edu.itba.paw.webapp.dto.ReviewDto;
import ar.edu.itba.paw.webapp.dto.UserDto;
import ar.edu.itba.paw.webapp.form.UserForm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.validation.Valid;
import javax.ws.rs.*;
import javax.ws.rs.core.*;
import java.net.URI;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Path("users")
@Component
public class UserController {

    private final UserService userService;
    private final ReviewService reviewService;
    private final BuyOrderService buyOrderService;

    private final NftService nftService;

    @Context
    private UriInfo uriInfo;

    @Autowired
    public UserController(UserService userService, ReviewService reviewService, BuyOrderService buyOrderService, NftService nftService) {
        this.userService = userService;
        this.reviewService = reviewService;
        this.buyOrderService = buyOrderService;
        this.nftService = nftService;
    }

    @Consumes({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_FORM_URLENCODED, })
    @POST
    public Response createUser(@Valid final UserForm userForm) {
        final User newUser = userService.create(userForm.getEmail(), userForm.getUsername(), userForm.getWalletAddress(), userForm.getWalletChain(), userForm.getPassword());
        // appends the new ID to the path of this route (/users)
        final URI location = uriInfo.getAbsolutePathBuilder()
                .path(String.valueOf(newUser.getId())).build();
        return Response.created(location).build();
    }

    @GET
    @Path("/current")
    public Response getCurrentUser(){
        final Optional<UserDto> maybeUser = userService.getCurrentUser().map(u -> UserDto.fromUser(uriInfo, u));
        if(maybeUser.isPresent()) {
            return Response.ok(maybeUser.get()).build();
        }
        return Response.status(Response.Status.NOT_FOUND).build();
    }

    // GET /users/{id}
    @GET
    @Path("/{id}")
    public Response getUser(@PathParam("id") int id) {
        Optional<User> maybeUser = userService.getUserById(id);
        if (maybeUser.isPresent()) {
            return Response.ok(UserDto.fromUser(this.uriInfo, maybeUser.get())).build();
        }
        return Response.status(Response.Status.NOT_FOUND).build();
    }

    // TODO: Move method to reviews controller (if possible)
    @GET()
    @Path("/{id}/reviews")
    @Produces({ MediaType.APPLICATION_JSON, })
    public Response listUserReviews(
            @PathParam("id") final int revieweeId,
            @QueryParam("page") @DefaultValue("1") final int page,
            @QueryParam("reviewer") @DefaultValue("-1") final int reviewerId
    ) {
        Stream<Review> reviewStream = reviewService.getUserReviews(page, revieweeId).stream();
        List<ReviewDto> reviewList;

        if(reviewerId > 0)
            reviewStream = reviewStream.filter(r -> r.getUsersByIdReviewer().getId() == reviewerId);
        reviewList = reviewStream.map(n -> ReviewDto.fromReview(uriInfo, n)).collect(Collectors.toList());

        if(reviewList.isEmpty())
            return Response.noContent().build();

        Response.ResponseBuilder responseBuilder = Response.ok(new GenericEntity<List<ReviewDto>>(reviewList) {});
        if (page > 1)
            responseBuilder.link(uriInfo.getAbsolutePathBuilder().queryParam("page", page - 1).build(), "prev");
        int lastPage = (int) Math.ceil(reviewService.getUserReviewsAmount(revieweeId) / (double) reviewService.getPageSize());
        if (page < lastPage)
            responseBuilder.link(uriInfo.getAbsolutePathBuilder().queryParam("page", page + 1).build(), "next");

        return responseBuilder
                .link(uriInfo.getAbsolutePathBuilder().queryParam("page", 1).build(), "first")
                .link(uriInfo.getAbsolutePathBuilder().queryParam("page", lastPage).build(), "last")
                .build();
    }

    @GET
    @Path("/{id}/buyorders")
    @Produces({ MediaType.APPLICATION_JSON, })
    public Response getBuyOrdersByUserId(@PathParam("id") final int userId, @QueryParam("page") @DefaultValue("1") final int page, @QueryParam("status") @DefaultValue("ALL") final String status) {
        Optional<User> maybeUser = userService.getUserById(userId);
        if (!maybeUser.isPresent()) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }

        long amountOfferPages;
        amountOfferPages = buyOrderService.getAmountPagesForUser(maybeUser.get());

        if(page > amountOfferPages || page < 0 ) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }

        List<BuyOrderDto> buyOffers;
        buyOffers = buyOrderService.getBuyOrdersForUser(maybeUser.get(), page, status).stream().map(n -> BuyOrderDto.fromBuyOrder(n, uriInfo)).collect(Collectors.toList());

        if(buyOffers.isEmpty()){
            return Response.noContent().build();
        }
        Response.ResponseBuilder responseBuilder = Response.ok(new GenericEntity<List<BuyOrderDto>>(buyOffers) {});
        if (page > 1)
            responseBuilder.link(uriInfo.getAbsolutePathBuilder().queryParam("page", page - 1).build(), "prev");
        if (page < amountOfferPages)
            responseBuilder.link(uriInfo.getAbsolutePathBuilder().queryParam("page", page + 1).build(), "next");
        return responseBuilder
                .link(uriInfo.getAbsolutePathBuilder().queryParam("page", 1).build(), "first")
                .link(uriInfo.getAbsolutePathBuilder().queryParam("page", amountOfferPages).build(), "last")
                .build();
    }


    @GET
    @Path("/{id}/inventory")
    @Produces({ MediaType.APPLICATION_JSON, })
    public Response getUserInventoryByUserId(@PathParam("id") final int userId, @QueryParam("page") @DefaultValue("1") final int page, @QueryParam("sort") final String sort) {
        Optional<User> maybeUser = userService.getUserById(userId);

        if(!maybeUser.isPresent()) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }

        List<NftDto> userInventory = nftService.getAllPublicationsByUser(page, maybeUser.get(), "inventory", sort)
                .stream().map(Publication::getNft).map(n -> NftDto.fromNft(uriInfo, n)).collect(Collectors.toList());
        if (userInventory.isEmpty())
            return Response.noContent().build();

        Response.ResponseBuilder responseBuilder = Response.ok(new GenericEntity<List<NftDto>>(userInventory) {});
        if (page > 1)
            responseBuilder.link(uriInfo.getAbsolutePathBuilder().queryParam("page", page - 1).build(), "prev");
        int lastPage = nftService.getAmountPublicationPagesByUser(maybeUser.get(), maybeUser.get(), "inventory");
        if (page < lastPage)
            responseBuilder.link(uriInfo.getAbsolutePathBuilder().queryParam("page", page + 1).build(), "next");

        return responseBuilder
                .link(uriInfo.getAbsolutePathBuilder().queryParam("page", 1).build(), "first")
                .link(uriInfo.getAbsolutePathBuilder().queryParam("page", lastPage).build(), "last")
                .build();

    }

}
