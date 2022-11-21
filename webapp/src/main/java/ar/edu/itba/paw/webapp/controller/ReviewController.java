package ar.edu.itba.paw.webapp.controller;//package ar.edu.itba.paw.webapp.controller;

import ar.edu.itba.paw.model.Review;
import ar.edu.itba.paw.service.ReviewService;
import ar.edu.itba.paw.webapp.dto.ReviewDto;
import ar.edu.itba.paw.webapp.exceptions.NoBodyException;
import ar.edu.itba.paw.webapp.form.ReviewForm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.validation.Valid;
import javax.ws.rs.*;
import javax.ws.rs.core.*;
import java.net.URI;
import java.util.Optional;

@Path("reviews")
@Component
public class ReviewController {

    private final ReviewService reviewService;

    @Context
    private UriInfo uriInfo;

    @Autowired
    public ReviewController(final ReviewService reviewService) {
        this.reviewService = reviewService;
    }


    // TODO: Make method on service to retrieve review by id?
    @GET()
    @Path("/{id}")
    @Produces({ MediaType.APPLICATION_JSON, })
    public Response getReview(@PathParam("id") final int reviewId) {
        Optional<Review> maybeReview = reviewService.getReview(reviewId);
        if(maybeReview.isPresent()){
            ReviewDto dto = ReviewDto.fromReview(uriInfo, maybeReview.get());
            return Response.ok(dto).build();
        }
        throw new NotFoundException();
    }


    @POST
    @Consumes({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_FORM_URLENCODED, })
    public Response createUserReview(@Valid final ReviewForm reviewForm){
        if(reviewForm == null)
            throw new NoBodyException();
        int reviewerId = Integer.parseInt(reviewForm.getReviewerId());
        int revieweeId = Integer.parseInt(reviewForm.getRevieweeId());
        int score = Integer.parseInt(reviewForm.getScore());
        Review newReview = reviewService.addReview(reviewerId, revieweeId, score, reviewForm.getTitle(), reviewForm.getComments());
        final URI location = uriInfo.getAbsolutePathBuilder()
                .path(String.valueOf(newReview.getId())).build();
        return Response.created(location).build();
    }

    @DELETE
    @Path("/{id}")
    public Response deleteReview(@PathParam("id") final int reviewId){
        Optional<Review> maybeReview = reviewService.getReview(reviewId);
        maybeReview.ifPresent(r -> reviewService.deleteReview(r.getId()));
        return Response.noContent().build();
    }


    /*
    // TODO: Make summary dto?
    @GET()
    @Path("/{id}/summary")
    @Produces({ MediaType.APPLICATION_JSON, })
    public Response getUserReviewsSummary(@PathParam("id") final int revieweeId) {
        Response.ResponseBuilder responseBuilder = Response.ok();
        responseBuilder.
    }
    */

}
