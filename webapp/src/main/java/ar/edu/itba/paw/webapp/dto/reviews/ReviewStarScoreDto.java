package ar.edu.itba.paw.webapp.dto.reviews;

public class ReviewStarScoreDto {

    private int star;
    private int score;

    public static ReviewStarScoreDto fromReviewStarScore(final int star, final int score){
        final ReviewStarScoreDto dto = new ReviewStarScoreDto();

        dto.star = star;
        dto.score = score;

        return dto;
    }

    public int getStar() {
        return star;
    }

    public void setStar(int star) {
        this.star = star;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }
}
