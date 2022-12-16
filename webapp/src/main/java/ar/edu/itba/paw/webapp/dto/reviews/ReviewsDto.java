package ar.edu.itba.paw.webapp.dto.reviews;

import javafx.util.Pair;

import javax.ws.rs.core.UriInfo;
import java.util.List;

public class ReviewsDto {

    private long total;
    private double score;
    private List<ReviewStarScoreDto> ratings;
    private List<ReviewDto> reviews;

    public static ReviewsDto fromReviewList(final UriInfo uriInfo, final List<ReviewDto> reviews, final long total, final double score, final List<ReviewStarScoreDto> ratings) {
        final ReviewsDto dto = new ReviewsDto();

        dto.total = total;
        dto.score = score;
        dto.ratings = ratings;
        dto.reviews = reviews;

        return dto;
    }

    public long getTotal() {
        return total;
    }

    public void setTotal(long total) {
        this.total = total;
    }

    public double getScore() {
        return score;
    }

    public void setScore(double score) {
        this.score = score;
    }

    public List<ReviewStarScoreDto> getRatings() {
        return ratings;
    }

    public void setRatings(List<ReviewStarScoreDto> ratings) {
        this.ratings = ratings;
    }

    public List<ReviewDto> getReviews() {
        return reviews;
    }

    public void setReviews(List<ReviewDto> reviews) {
        this.reviews = reviews;
    }
}
