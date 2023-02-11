package ar.edu.itba.paw.persistence;

import ar.edu.itba.paw.model.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.*;
import java.util.stream.Collectors;

@Repository
public class ReviewJpaDao implements ReviewDao{

    private static final Logger LOGGER = LoggerFactory.getLogger(ReviewJpaDao.class);

    @PersistenceContext
    private EntityManager em;

    @Override
    public Review addReview(User reviewer, User reviewee, int score, String title, String comments) {
        final Review newReview = new Review(reviewer, reviewee, score, title, comments);
        em.persist(newReview);
        return newReview;
    }

    @Override
    public List<Review> getUserReviews(int page, int userId, int pageSize) {
        final Query idQuery = em.createNativeQuery("SELECT id FROM reviews WHERE id_reviewee = :id_reviewee LIMIT :pageSize OFFSET :pageOffset");
        idQuery.setParameter("id_reviewee", userId);
        idQuery.setParameter("pageSize", pageSize);
        idQuery.setParameter("pageOffset", (page-1)*pageSize);
        @SuppressWarnings("unchecked")
        final List<Integer> reviewsIds = (List<Integer>) idQuery.getResultList().stream().collect(Collectors.toList());

        if(reviewsIds.size() == 0)
            return Collections.emptyList();

        final TypedQuery<Review> query = em.createQuery("FROM Review AS r WHERE r.id IN :ids", Review.class);
        query.setParameter("ids", reviewsIds);
        return query.getResultList();
    }


    @Override
    public boolean hasReviewByUser(int reviewerId, int revieweeId) {
        final TypedQuery<Review> query = em.createQuery("FROM Review r WHERE r.usersByIdReviewer.id = :idReviewer AND r.usersByIdReviewee.id = :idReviewee", Review.class);
        query.setParameter("idReviewer", reviewerId);
        query.setParameter("idReviewee", revieweeId);
        return query.getResultList().size() > 0;
    }

    @Override
    public int getUserReviewsAmount(int userId){
        final Query query = em.createNativeQuery("SELECT count(*) FROM reviews WHERE id_reviewee = :id_reviewee");
        query.setParameter("id_reviewee", userId);
        return ((BigInteger)query.getSingleResult()).intValue();
    }

    @Override
    public double getUserScore(int userId){
        final Query query = em.createNativeQuery("SELECT avg(score) FROM reviews WHERE id_reviewee = :id_reviewee");
        query.setParameter("id_reviewee", userId);
        BigDecimal score = (BigDecimal)query.getSingleResult();
        return score == null ? 0 : score.doubleValue();
    }

    @Override
    public Map<Integer, Integer> getUserReviewsRatings(int userId, int minScore, int maxScore) {
        long userReviewsAmount = getUserReviewsAmount(userId);
        Map<Integer, Integer> ratingsPercentages = new HashMap<>();

        final Query query = em.createNativeQuery("SELECT count(*) FROM reviews WHERE score = :score AND id_reviewee = :id_reviewee");
        query.setParameter("id_reviewee", userId);
        for(int i = minScore ; i <= maxScore ; i++){
            query.setParameter("score", i);
            ratingsPercentages.put(i, (int)(((BigInteger)query.getSingleResult()).doubleValue() * 100 / userReviewsAmount));
        }

        return ratingsPercentages;
    }

    @Override
    public List<Integer> getUserReviewsRatingsListSorted(int userId, int minScore, int maxScore, String sort) {
        long userReviewsAmount = getUserReviewsAmount(userId);
        List<Integer> ratingsPercentages = new ArrayList<>();

        final Query query = em.createNativeQuery("SELECT count(*) FROM reviews WHERE score = :score AND id_reviewee = :id_reviewee");
        query.setParameter("id_reviewee", userId);

        switch(sort){
            case "desc":
                for(int i = maxScore ; i >= minScore ; i--){
                    query.setParameter("score", i);
                    ratingsPercentages.add((int)(((BigInteger)query.getSingleResult()).doubleValue() * 100 / userReviewsAmount));
                }
                break;
            case "asc":
            default:
                for(int i = minScore ; i <= maxScore ; i++){
                    query.setParameter("score", i);
                    ratingsPercentages.add((int)(((BigInteger)query.getSingleResult()).doubleValue() * 100 / userReviewsAmount));
                }
                break;
        }

        return ratingsPercentages;
    }

    @Override
    public Optional<Review> getReview(int reviewId) {
        Review review = em.find(Review.class, reviewId);
        if(review != null){
            return Optional.of(review);
        }
        return Optional.empty();
    }

    @Override
    public void deleteReview(int reviewId) {
        Review review = em.find(Review.class, reviewId);
        em.remove(review);
    }

    @Override
    public boolean purchaseExists(int buyerId, int sellerId) {
        if(buyerId == sellerId)
            return false;
        final Query query = em.createQuery("FROM Purchase AS purchase WHERE purchase.buyer.id = :buyerId AND purchase.seller.id = :sellerId ");
        query.setParameter("buyerId",buyerId);
        query.setParameter("sellerId", sellerId);
        return query.getResultList().stream().findFirst().isPresent();
    }
}
