package ar.edu.itba.paw.webapp.controller;


import ar.edu.itba.paw.exceptions.UserNoPermissionException;
import ar.edu.itba.paw.exceptions.UserNotFoundException;
import ar.edu.itba.paw.model.Publication;
import ar.edu.itba.paw.model.Review;
import ar.edu.itba.paw.model.User;
import ar.edu.itba.paw.service.*;
import ar.edu.itba.paw.webapp.dto.*;
import ar.edu.itba.paw.webapp.dto.reviews.ReviewDto;
import ar.edu.itba.paw.webapp.dto.reviews.ReviewStarScoreDto;
import ar.edu.itba.paw.webapp.dto.reviews.ReviewsDto;
import ar.edu.itba.paw.webapp.exceptions.NoBodyException;
import ar.edu.itba.paw.webapp.form.ReviewForm;
import ar.edu.itba.paw.webapp.form.UserForm;
import javafx.util.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.validation.Valid;
import javax.ws.rs.*;
import javax.ws.rs.core.*;
import java.net.URI;
import java.util.ArrayList;
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
    private final PurchaseService purchaseService;
    private final NftService nftService;
    private final FavoriteService favoriteService;

    @Context
    private UriInfo uriInfo;

    @Autowired
    public UserController(UserService userService, ReviewService reviewService, BuyOrderService buyOrderService, NftService nftService, PurchaseService purchaseService, FavoriteService favoriteService) {
        this.userService = userService;
        this.reviewService = reviewService;
        this.buyOrderService = buyOrderService;
        this.nftService = nftService;
        this.purchaseService = purchaseService;
        this.favoriteService = favoriteService;
    }

    @Consumes({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_FORM_URLENCODED, })
    @POST
    public Response createUser(@Valid final UserForm userForm) {
        if(userForm == null)
            throw new NoBodyException();
        final User newUser = userService.create(userForm.getEmail(), userForm.getUsername(), userForm.getWalletAddress(), userForm.getWalletChain(), userForm.getPassword());
        // appends the new ID to the path of this route (/users)
        final URI location = uriInfo.getAbsolutePathBuilder()
                .path(String.valueOf(newUser.getId())).build();
        return Response.created(location).build();
    }

    // GET /users/{id}
    @GET
    @Path("/{id}")
    public Response getUser(@PathParam("id") int id) {
        Optional<User> maybeUser = userService.getUserById(id);
        if (!maybeUser.isPresent()) {
            throw new UserNotFoundException();
        }
        return Response.ok(UserDto.fromUser(this.uriInfo, maybeUser.get())).build();
    }

    @GET
    @Path("/{id}/buyorders")
    @Produces({ MediaType.APPLICATION_JSON, })
    public Response getBuyOrdersByUserId(
            @PathParam("id") final int userId,
            @QueryParam("page") @DefaultValue("1") final int page,
            @QueryParam("status") @DefaultValue("ALL") final String status
    ) {
        Optional<User> maybeUser = userService.getUserById(userId);
        if (!maybeUser.isPresent()) {
            throw new UserNotFoundException();
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
    @Path("/{id}/favorites")
    public Response listUserFavorites(
            @PathParam("id") final int userId,
            @QueryParam("page") @DefaultValue("1") int page,
            @QueryParam("sort") final String sort
    ){
        User currentUser = userService.getCurrentUser().get();

        if(currentUser.getId() != userId)
            throw new UserNoPermissionException();

        List<NftDto> userFavorites = nftService.getAllPublicationsByUser(page, currentUser, "favorited", sort)
                .stream().map(Publication::getNft).map(n -> NftDto.fromNft(uriInfo, n)).collect(Collectors.toList());
        if (userFavorites.isEmpty())
            return Response.noContent().build();

        Response.ResponseBuilder responseBuilder = Response.ok(new GenericEntity<List<NftDto>>(userFavorites) {});
        if (page > 1)
            responseBuilder.link(uriInfo.getAbsolutePathBuilder().queryParam("page", page - 1).build(), "prev");
        int lastPage = nftService.getAmountPublicationPagesByUser(currentUser, currentUser, "favorited");
        if (page < lastPage)
            responseBuilder.link(uriInfo.getAbsolutePathBuilder().queryParam("page", page + 1).build(), "next");

        return responseBuilder
                .link(uriInfo.getAbsolutePathBuilder().queryParam("page", 1).build(), "first")
                .link(uriInfo.getAbsolutePathBuilder().queryParam("page", lastPage).build(), "last")
                .build();
    }

    @PUT
    @Path("/{id}/favorites/{nftId}")
    @Consumes({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_FORM_URLENCODED, })
    public Response addUserFavorite(
            @PathParam("id") final int userId,
            @PathParam("nftId") final int nftId
    ) {
        User currentUser = userService.getCurrentUser().get();

        if(currentUser.getId() != userId)
            throw new UserNoPermissionException();

        Optional<NftDto> maybeNft = nftService.getNFTById(nftId).map(n -> NftDto.fromNft(uriInfo, n));
        if (!maybeNft.isPresent()) {
            throw new NotFoundException("Nft not found");
        }

        favoriteService.addNftFavorite(nftId, currentUser);
        return Response.noContent().build();
    }

    @DELETE
    @Path("/{id}/favorites/{nftId}")
    @Consumes({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_FORM_URLENCODED, })
    public Response removeUserFavorite(
            @PathParam("id") int userId,
            @PathParam("nftId") final int nftId
    ) {
        User currentUser = userService.getCurrentUser().get();

        if(currentUser.getId() != userId)
            throw new UserNoPermissionException();

        Optional<NftDto> maybeNft = nftService.getNFTById(nftId).map(n -> NftDto.fromNft(uriInfo, n));
        if (!maybeNft.isPresent()) {
            throw new NotFoundException("Nft not found");
        }

        favoriteService.removeNftFavorite(nftId, currentUser);
        return Response.noContent().build();
    }

    @GET
    @Path("/{id}/purchases")
    @Produces({ MediaType.APPLICATION_JSON})
    public Response getHistoryTransactions(@PathParam("id") int userId, @QueryParam("page") @DefaultValue("1") int page){
        Optional<User> currentUser = userService.getCurrentUser();
        if(!currentUser.isPresent() || currentUser.get().getId() != userId)
            throw new UserNoPermissionException();

        long amountPurchasesPages;
        amountPurchasesPages = purchaseService.getAmountPagesByUserId(userId);

        if(page > amountPurchasesPages || page < 0 ) {
            throw new NotFoundException();
        }

        List<PurchaseDto> historyPurchases;
        historyPurchases = purchaseService.getAllTransactions(userId, page).stream().map(n -> PurchaseDto.fromPurchase(uriInfo, n)).collect(Collectors.toList());

        if(historyPurchases.isEmpty()){
            return Response.noContent().build();
        }
        Response.ResponseBuilder responseBuilder = Response.ok(new GenericEntity<List<PurchaseDto>>(historyPurchases) {});

        //TODO REUSE THIS CODE
        if (page > 1)
            responseBuilder.link(uriInfo.getAbsolutePathBuilder().queryParam("page", page - 1).build(), "prev");
        if (page < amountPurchasesPages)
            responseBuilder.link(uriInfo.getAbsolutePathBuilder().queryParam("page", page + 1).build(), "next");
        return responseBuilder
                .link(uriInfo.getAbsolutePathBuilder().queryParam("page", 1).build(), "first")
                .link(uriInfo.getAbsolutePathBuilder().queryParam("page", amountPurchasesPages).build(), "last")
                .build();
    }

    @GET
    @Path("/{id}/purchases/{purchaseId}")
    @Produces({ MediaType.APPLICATION_JSON})
    public Response getHistoryTransactionById(@PathParam("id") int userId, @PathParam("purchaseId") int purchaseId){
        Optional<User> currentUser = userService.getCurrentUser();

        Optional<PurchaseDto> maybePurchase = purchaseService.getPurchaseById(currentUser.get().getId(), purchaseId).map(n -> PurchaseDto.fromPurchase(uriInfo, n));
        if (!maybePurchase.isPresent()) {
            throw new UserNoPermissionException();
        }
        return Response.ok(maybePurchase.get()).build();
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
        ReviewsDto reviewsInfo;
        List<ReviewStarScoreDto> reviewRatings = new ArrayList<>();

        final int reviewMaxScore = reviewService.getMaxScore();
        final int reviewMinScore = reviewService.getMinScore();

        if(reviewerId > 0)
            reviewStream = reviewStream.filter(r -> r.getUsersByIdReviewer().getId() == reviewerId);
        reviewList = reviewStream.map(n -> ReviewDto.fromReview(uriInfo, n)).collect(Collectors.toList());

        List<Integer> userRatingsDesc = reviewService.getUserReviewsRatingsListSorted(revieweeId, "desc");
        for(int i=reviewMaxScore, j=0 ; i >= reviewMinScore ; i--, j++){
            reviewRatings.add(ReviewStarScoreDto.fromReviewStarScore(i, userRatingsDesc.get(j)));
        }

        reviewsInfo = ReviewsDto.fromReviewList(uriInfo, reviewList, reviewService.getUserReviewsAmount(revieweeId),
                reviewService.getUserScore(revieweeId), reviewRatings);

        /* if(reviewList.isEmpty())
            return Response.noContent().build(); */

        Response.ResponseBuilder responseBuilder = Response.ok(new GenericEntity<ReviewsDto>(reviewsInfo) {});
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

    @POST
    @Path("/{id}/reviews")
    @Consumes({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_FORM_URLENCODED, })
    public Response createUserReview(@PathParam("id") final int userId, @Valid final ReviewForm reviewForm){
        if(reviewForm == null)
            throw new NoBodyException();

        int reviewerId = userService.getCurrentUser().get().getId();
        int score = Integer.parseInt(reviewForm.getScore());
        Review newReview = reviewService.addReview(reviewerId, userId, score, reviewForm.getTitle(), reviewForm.getComments());
        final URI location = uriInfo.getAbsolutePathBuilder()
                .replacePath("/users").path(String.valueOf(userId))
                .path("/reviews").path(String.valueOf(newReview.getId())).build();
        return Response.created(location).build();
    }

    @GET()
    @Path("/{id}/reviews/{reviewId}")
    @Produces({ MediaType.APPLICATION_JSON, })
    public Response getReview(@PathParam("id") final int userId, @PathParam("reviewId") final int reviewId) {
        Optional<Review> maybeReview = reviewService.getReview(reviewId);
        if(maybeReview.isPresent()){
            ReviewDto dto = ReviewDto.fromReview(uriInfo, maybeReview.get());
            return Response.ok(dto).build();
        }
        throw new NotFoundException();
    }

    @DELETE
    @Path("/{id}/reviews/{reviewId}")
    public Response deleteReview(@PathParam("id") final int userId, @PathParam("reviewId") final int reviewId){
        Optional<Review> maybeReview = reviewService.getReview(reviewId);
        maybeReview.ifPresent(r -> reviewService.deleteReview(r.getId()));
        return Response.noContent().build();
    }

    /*
    // TODO: Make summary dto?
    @GET()
    @Path("/{id}/reviews/summary")
    @Produces({ MediaType.APPLICATION_JSON, })
    public Response getUserReviewsSummary(@PathParam("id") final int revieweeId) {
        Response.ResponseBuilder responseBuilder = Response.ok();
        responseBuilder.
    }
    */

}
