package ar.edu.itba.paw.webapp.dto;

import ar.edu.itba.paw.model.Review;

import javax.ws.rs.core.UriBuilder;
import javax.ws.rs.core.UriInfo;
import java.net.URI;

public class ReviewDto {

    private int score;
    private String title;
    private String comments;
    private UserDto reviewer;
    private UserDto reviewee;

    private URI self;

    private final static String USERS_URI_PREFIX = "users";
    private final static String REVIEWS_URI_PREFIX = "reviews";

    public static ReviewDto fromReview(final UriInfo uriInfo, final Review review) {
        final ReviewDto dto = new ReviewDto();

        final UriBuilder reviewUriBuilder = uriInfo.getAbsolutePathBuilder()
                .replacePath(USERS_URI_PREFIX)
                .path(String.valueOf(review.getUsersByIdReviewee().getId()))
                .path(REVIEWS_URI_PREFIX)
                .path(String.valueOf(review.getId()));

        dto.self = reviewUriBuilder.build();
        dto.reviewer = UserDto.fromUser(uriInfo, review.getUsersByIdReviewer());
        dto.reviewee = UserDto.fromUser(uriInfo, review.getUsersByIdReviewee());

        dto.score = review.getScore();
        dto.title = review.getTitle();
        dto.comments = review.getComments();

        return dto;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getComments() {
        return comments;
    }

    public void setComments(String comments) {
        this.comments = comments;
    }

    public URI getSelf() {
        return self;
    }

    public void setSelf(URI self) {
        this.self = self;
    }

    public UserDto getReviewer() {
        return reviewer;
    }

    public void setReviewer(UserDto reviewer) {
        this.reviewer = reviewer;
    }

    public UserDto getReviewee() {
        return reviewee;
    }

    public void setReviewee(UserDto reviewee) {
        this.reviewee = reviewee;
    }
}
