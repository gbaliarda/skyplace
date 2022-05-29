package ar.edu.itba.paw.service;

import ar.edu.itba.paw.exceptions.InvalidReviewException;
import ar.edu.itba.paw.exceptions.UserNotFoundException;
import ar.edu.itba.paw.model.Review;
import ar.edu.itba.paw.model.User;
import ar.edu.itba.paw.persistence.ReviewDao;
import ar.edu.itba.paw.persistence.UserDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class ReviewServiceImpl implements ReviewService{

    private final ReviewDao reviewDao;
    private final UserDao userDao;
    private final static int pageSize = 5;

    @Autowired
    public ReviewServiceImpl(ReviewDao reviewDao, UserDao userDao) {
        this.reviewDao = reviewDao;
        this.userDao = userDao;
    }

    @Transactional
    @Override
    public void addReview(int reviewerId, int revieweeId, int score, String title, String comments) {
        if(reviewerId == revieweeId)
            throw new InvalidReviewException();
        User reviewer = userDao.getUserById(reviewerId).orElseThrow(UserNotFoundException::new);
        User reviewee = userDao.getUserById(revieweeId).orElseThrow(UserNotFoundException::new);
        reviewDao.addReview(reviewer, reviewee, score, title, comments);
    }

    @Override
    public List<Review> getUserReviews(int page, int userId) {
        if(!userDao.getUserById(userId).isPresent())
            throw new UserNotFoundException();
        return reviewDao.getUserReviews(page, userId, pageSize);
    }

    @Override
    public List<Review> getAllUserReviews(int userId) {
        if(!userDao.getUserById(userId).isPresent())
            throw new UserNotFoundException();
        return reviewDao.getAllUserReviews(userId);
    }

    @Override
    public int getUserReviewsPageAmount(int userId) {
        long userReviewsAmount = reviewDao.getUserReviewsAmount(userId);
        if(userReviewsAmount == 0)
            return 1;
        return (int)(userReviewsAmount-1)/pageSize + 1;
    }

    @Override
    public long getUserReviewsAmount(int userId) {
        return reviewDao.getUserReviewsAmount(userId);
    }

    @Transactional
    @Override
    public void deleteReview(int reviewId) {
        reviewDao.deleteReview(reviewId);
    }

    @Override
    public int getPageSize() {
        return pageSize;
    }
}
