package ar.edu.itba.paw.webapp.dto.reviews;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

public class ReviewsDto {

    private double score;
    private List<ReviewStarScoreDto> ratings;
    private List<ReviewDto> reviews;

    public static ReviewsDto fromReviewList(final List<ReviewDto> reviews, final double score, final List<ReviewStarScoreDto> ratings) {
        final ReviewsDto dto = new ReviewsDto();
        BigDecimal bd = new BigDecimal(score).setScale(2, RoundingMode.HALF_UP);

        dto.score = bd.doubleValue();
        dto.ratings = ratings;
        dto.reviews = reviews;

        return dto;
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
