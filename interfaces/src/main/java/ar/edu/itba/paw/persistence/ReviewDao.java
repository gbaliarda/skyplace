package ar.edu.itba.paw.persistence;

import ar.edu.itba.paw.model.Review;
import ar.edu.itba.paw.model.User;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface ReviewDao {

    Review addReview(User reviewer, User reviewee, int score, String title, String comments);

    List<Review> getUserReviews(int page, int userId, int pageSize);

    boolean hasReviewByUser(int idReviewer, int idReviewee);

    int getUserReviewsAmount(int userId);

    double getUserScore(int userId);

    Map<Integer, Integer> getUserReviewsRatings(int userId, int minScore, int maxScore);

    List<Integer> getUserReviewsRatingsListSorted(int userId, int minScore, int maxScore, String sort);

    Optional<Review> getReview(int reviewId);

    void deleteReview(int reviewId);

    boolean purchaseExists(int buyerId, int sellerId);
}
