package ar.edu.itba.paw.service;

import ar.edu.itba.paw.exceptions.InvalidReviewException;
import ar.edu.itba.paw.exceptions.NoTransactionBetweenUsersException;
import ar.edu.itba.paw.exceptions.UserNotFoundException;
import ar.edu.itba.paw.model.Review;
import ar.edu.itba.paw.model.User;
import ar.edu.itba.paw.persistence.PurchaseDao;
import ar.edu.itba.paw.persistence.ReviewDao;
import ar.edu.itba.paw.persistence.UserDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Locale;
import java.util.Optional;

@Service
public class ReviewServiceImpl implements ReviewService{

    private final ReviewDao reviewDao;
    private final UserDao userDao;
    private final PurchaseService purchaseService;
    private final UserService userService;

    private final MailingService mailingService;
    private final static int pageSize = 5;
    private final static int minScore = 1;
    private final static int maxScore = 5;

    @Autowired
    public ReviewServiceImpl(ReviewDao reviewDao, UserDao userDao, MailingService mailingService, PurchaseService purchaseService, UserService userService) {
        this.reviewDao = reviewDao;
        this.userDao = userDao;
        this.mailingService = mailingService;
        this.purchaseService = purchaseService;
        this.userService = userService;
    }

    @Transactional
    @Override
    public Review addReview(int reviewerId, int revieweeId, int score, String title, String comments) {
        if(reviewerId == revieweeId || hasReviewByUser(reviewerId, revieweeId))
            throw new InvalidReviewException();
        if(purchaseService.getTransactionsBetweenUsers(reviewerId, revieweeId, 1).isEmpty())
            throw new NoTransactionBetweenUsersException();
        User reviewer = userDao.getUserById(reviewerId).orElseThrow(UserNotFoundException::new);
        User reviewee = userDao.getUserById(revieweeId).orElseThrow(UserNotFoundException::new);
        Review newReview = reviewDao.addReview(reviewer, reviewee, score, title, comments);
        Locale locale = Locale.forLanguageTag(reviewee.getLocale());
        mailingService.sendNewReviewMail(reviewee.getUsername(), reviewee.getEmail(), revieweeId, reviewer.getUsername(), reviewer.getEmail(), newReview.getScore(), newReview.getTitle(), newReview.getComments(), locale);
        return newReview;
    }

    @Override
    public Optional<Review> getReview(int reviewId) {
        return reviewDao.getReview(reviewId);
    }

    @Override
    public List<Review> getUserReviews(int page, int userId) {
        if(!userDao.getUserById(userId).isPresent())
            throw new UserNotFoundException();
        return reviewDao.getUserReviews(page, userId, pageSize);
    }

    @Override
    public double getUserScore(int userId) {
        if(!userDao.getUserById(userId).isPresent())
            throw new UserNotFoundException();
        return reviewDao.getUserScore(userId);
    }

    @Override
    public List<Integer> getUserReviewsRatingsListSorted(int userId, String sort) {
        if(!userDao.getUserById(userId).isPresent())
            throw new UserNotFoundException();
        return reviewDao.getUserReviewsRatingsListSorted(userId, minScore, maxScore, sort);
    }

    @Override
    public int getUserReviewsPageAmount(int userId) {
        if(!userDao.getUserById(userId).isPresent())
            throw new UserNotFoundException();
        long userReviewsAmount = reviewDao.getUserReviewsAmount(userId);
        if(userReviewsAmount == 0)
            return 1;
        return (int)(userReviewsAmount-1)/pageSize + 1;
    }

    @Override
    public boolean hasReviewByUser(int idReviewer, int idReviewee) {
        if(!userDao.getUserById(idReviewer).isPresent() ||
                !userDao.getUserById(idReviewee).isPresent())
            throw new UserNotFoundException();
        return reviewDao.hasReviewByUser(idReviewer, idReviewee);
    }

    @Override
    public int getUserReviewsAmount(int userId) {
        if(!userDao.getUserById(userId).isPresent())
            throw new UserNotFoundException();
        return reviewDao.getUserReviewsAmount(userId);
    }

    protected boolean hasDeletePermission(int reviewId, int deleterId) {
        Optional<Review> maybeReview = getReview(reviewId);
        return maybeReview.filter(review -> userService.isAdmin() || review.getUsersByIdReviewer().getId() == deleterId).isPresent();
    }

    @Transactional
    @Override
    public void deleteReview(int reviewId) {
        Optional<User> maybeUser = userService.getCurrentUser();
        if(maybeUser.isPresent() && hasDeletePermission(reviewId, maybeUser.get().getId()))
            reviewDao.deleteReview(reviewId);
    }

    @Override
    public int getPageSize() {
        return pageSize;
    }

    @Override
    public int getMinScore() {
        return minScore;
    }

    @Override
    public int getMaxScore() {
        return maxScore;
    }

    @Override
    public boolean purchaseExists(int buyerId, int sellerId) {
        return reviewDao.purchaseExists(buyerId, sellerId);
    }
}
