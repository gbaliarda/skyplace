package ar.edu.itba.paw.service;

import ar.edu.itba.paw.exceptions.InvalidReviewException;
import ar.edu.itba.paw.exceptions.UserNotFoundException;
import ar.edu.itba.paw.model.*;
import ar.edu.itba.paw.persistence.ReviewDao;
import ar.edu.itba.paw.persistence.UserDao;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.runners.MockitoJUnitRunner;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.*;

import static org.junit.Assert.*;

@RunWith(MockitoJUnitRunner.class)
public class ReviewServiceImplTest {

    private final static int ID_USER1 = 1;
    private final static String MAIL_USER1 = "test1@test.test";
    private final static String USERNAME_USER1 = "test1";
    private final static String WALLET_USER1 = "0x3038BfE9acde2Fa667bEbF3322DBf9F33ca22c80";
    private final static Chain WALLETCHAIN_USER1 = Chain.Ethereum;
    private final static String PASSWORD_USER1 = "PASSUSER1";
    private final static Role ROLE_USER1 = Role.User;
    private final static String LOCALE_USER1 = "en";

    private final static int ID_USER2 = 2;
    private final static String MAIL_USER2 = "test2@test.test";
    private final static String USERNAME_USER2 = "test2";
    private final static String WALLET_USER2 = "0xE5Da717E6d0186bE40eEa87De0A5aC9d5c4e5666";
    private final static Chain WALLETCHAIN_USER2 = Chain.Ethereum;
    private final static String PASSWORD_USER2 = "PASSUSER2";
    private final static Role ROLE_USER2 = Role.User;
    private final static String LOCALE_USER2 = "en";

    private final static int REVIEW_ID = 1;
    private final static int REVIEW_SCORE = 3;
    private final static String REVIEW_TITLE = "MY_REVIEW_TITLE";
    private final static String REVIEW_DESC = "MY_REVIEW_DESC";
    private final static String REVIEW_SORT = "MY_REVIEW_SORT";

    //private final static int PURCHASE_ID = 1;
    private final static BigDecimal PURCHASE_PRICE = BigDecimal.ONE;
    private final static Date PURCHASE_BUYDATE = Date.from(Instant.now().minusMillis(1000));            // 1 second ago
    private final static StatusPurchase PURCHASE_STATUS = StatusPurchase.SUCCESS;
    private final static String PURCHASE_TXHASH = "0xE5Ea717E6d0186bE40eEa87De0A5aC8d5c4e5666";

    private final static int NFT_ID = 1;
    private final static String NFT_CONTRACT_ADDR = "0xE5Ea717E6d0187bE40eEa87De0A5aC8d5c4e7666";
    private final static String NFT_NAME = "My testing nft";
    private final static Chain NFT_CHAIN = Chain.Ethereum;
    private final static int NFT_IMAGE_ID = 1;
    private final static String NFT_COLLECTION = "Testing collection";
    private final static String NFT_DESCRIPTION = "";

    private final static int PAGE_NUM = 1;

    @InjectMocks
    @Spy
    private ReviewServiceImpl reviewService;

    @Mock
    private UserDao userDao;

    @Mock
    private ReviewDao reviewDao;

    @Mock
    private MailingService mailingService;

    @Mock
    private PurchaseService purchaseService;

    @Test(expected = InvalidReviewException.class)
    public void testAddReviewOnOneself(){
        reviewService.addReview(ID_USER1, ID_USER1, REVIEW_SCORE, REVIEW_TITLE, REVIEW_DESC);
    }

    @Test(expected = InvalidReviewException.class)
    public void testAddReviewOnUserAlreadyReviewed(){
        Mockito.doReturn(true).when(reviewService).hasReviewByUser(ID_USER1, ID_USER2);

        reviewService.addReview(ID_USER1, ID_USER2, REVIEW_SCORE, REVIEW_TITLE, REVIEW_DESC);
    }

    @Test(expected = UserNotFoundException.class)
    public void testAddReviewOnInvalidReviewer(){
        User reviewee = new User(ID_USER2, USERNAME_USER2, WALLET_USER2, MAIL_USER2, PASSWORD_USER2, WALLETCHAIN_USER2, ROLE_USER2, LOCALE_USER2);
        User reviewer = new User(ID_USER1, USERNAME_USER1, WALLET_USER1, MAIL_USER1, PASSWORD_USER1, WALLETCHAIN_USER1, ROLE_USER1, LOCALE_USER1);
        Nft boughtNft = new Nft(NFT_ID, NFT_CONTRACT_ADDR, NFT_NAME, NFT_CHAIN, NFT_IMAGE_ID, NFT_COLLECTION, NFT_DESCRIPTION, reviewer);

        Mockito.doReturn(false).when(reviewService).hasReviewByUser(ID_USER1, ID_USER2);
        Mockito.when(userDao.getUserById(ID_USER1)).thenReturn(Optional.empty());
        Mockito.when(userDao.getUserById(ID_USER2)).thenReturn(Optional.of(reviewee));
        Mockito.when(purchaseService.getTransactionsBetweenUsers(ID_USER1, ID_USER2, PAGE_NUM))
                .thenReturn(Collections.singletonList(new Purchase(PURCHASE_PRICE, PURCHASE_BUYDATE, boughtNft, reviewer, reviewee, PURCHASE_STATUS, PURCHASE_TXHASH)));

        reviewService.addReview(ID_USER1, ID_USER2, REVIEW_SCORE, REVIEW_TITLE, REVIEW_DESC);
    }

    @Test(expected = UserNotFoundException.class)
    public void testAddReviewOnInvalidReviewee(){
        User reviewee = new User(ID_USER2, USERNAME_USER2, WALLET_USER2, MAIL_USER2, PASSWORD_USER2, WALLETCHAIN_USER2, ROLE_USER2, LOCALE_USER2);
        User reviewer = new User(ID_USER1, USERNAME_USER1, WALLET_USER1, MAIL_USER1, PASSWORD_USER1, WALLETCHAIN_USER1, ROLE_USER1, LOCALE_USER1);
        Nft boughtNft = new Nft(NFT_ID, NFT_CONTRACT_ADDR, NFT_NAME, NFT_CHAIN, NFT_IMAGE_ID, NFT_COLLECTION, NFT_DESCRIPTION, reviewer);

        Mockito.doReturn(false).when(reviewService).hasReviewByUser(ID_USER1, ID_USER2);
        Mockito.when(userDao.getUserById(ID_USER1)).thenReturn(Optional.of(reviewer));
        Mockito.when(userDao.getUserById(ID_USER2)).thenReturn(Optional.empty());
        Mockito.when(purchaseService.getTransactionsBetweenUsers(ID_USER1, ID_USER2, PAGE_NUM))
                .thenReturn(Collections.singletonList(new Purchase(PURCHASE_PRICE, PURCHASE_BUYDATE, boughtNft, reviewer, reviewee, PURCHASE_STATUS, PURCHASE_TXHASH)));

        reviewService.addReview(ID_USER1, ID_USER2, REVIEW_SCORE, REVIEW_TITLE, REVIEW_DESC);
    }

    @Test
    public void testAddReviewWithValidData(){
        User reviewee = new User(ID_USER2, USERNAME_USER2, WALLET_USER2, MAIL_USER2, PASSWORD_USER2, WALLETCHAIN_USER2, ROLE_USER2, LOCALE_USER2);
        User reviewer = new User(ID_USER1, USERNAME_USER1, WALLET_USER1, MAIL_USER1, PASSWORD_USER1, WALLETCHAIN_USER1, ROLE_USER1, LOCALE_USER1);
        Review review = new Review(REVIEW_ID, reviewer, reviewee, REVIEW_SCORE, REVIEW_TITLE, REVIEW_DESC);
        Nft boughtNft = new Nft(NFT_ID, NFT_CONTRACT_ADDR, NFT_NAME, NFT_CHAIN, NFT_IMAGE_ID, NFT_COLLECTION, NFT_DESCRIPTION, reviewer);
        Locale revieweeLocale = new Locale(LOCALE_USER2);

        Mockito.doReturn(false).when(reviewService).hasReviewByUser(ID_USER1, ID_USER2);
        Mockito.when(userDao.getUserById(ID_USER1)).thenReturn(Optional.of(reviewer));
        Mockito.when(userDao.getUserById(ID_USER2)).thenReturn(Optional.of(reviewee));
        Mockito.when(purchaseService.getTransactionsBetweenUsers(ID_USER1, ID_USER2, PAGE_NUM))
                .thenReturn(Collections.singletonList(new Purchase(PURCHASE_PRICE, PURCHASE_BUYDATE, boughtNft, reviewer, reviewee, PURCHASE_STATUS, PURCHASE_TXHASH)));
        Mockito.when(reviewDao.addReview(reviewer, reviewee, REVIEW_SCORE, REVIEW_TITLE, REVIEW_DESC)).thenReturn(review);
        Mockito.doNothing().when(mailingService).sendNewReviewMail(USERNAME_USER2, MAIL_USER2, ID_USER2, USERNAME_USER1, MAIL_USER1, REVIEW_SCORE, REVIEW_TITLE, REVIEW_DESC, revieweeLocale);

        reviewService.addReview(ID_USER1, ID_USER2, REVIEW_SCORE, REVIEW_TITLE, REVIEW_DESC);
    }

    @Test(expected = UserNotFoundException.class)
    public void testGetUserReviewsForInvalidUser(){
        Mockito.when(userDao.getUserById(ID_USER1)).thenReturn(Optional.empty());

        reviewService.getUserReviews(PAGE_NUM, ID_USER1);
    }

    @Test(expected = UserNotFoundException.class)
    public void testGetUserScoreForInvalidUser(){
        Mockito.when(userDao.getUserById(ID_USER1)).thenReturn(Optional.empty());

        reviewService.getUserScore(ID_USER1);
    }

    @Test(expected = UserNotFoundException.class)
    public void testGetUserReviewsRatingsListSortedForInvalidUser(){
        Mockito.when(userDao.getUserById(ID_USER1)).thenReturn(Optional.empty());

        reviewService.getUserReviewsRatingsListSorted(ID_USER1, REVIEW_SORT);
    }

    @Test(expected = UserNotFoundException.class)
    public void testGetUserReviewsPageAmountForInvalidUser(){
        Mockito.when(userDao.getUserById(ID_USER1)).thenReturn(Optional.empty());

        reviewService.getUserReviewsPageAmount(ID_USER1);
    }

    @Test
    public void testGetUserReviewsPageAmountOnNoReviews(){
        User user = new User(ID_USER2, USERNAME_USER2, WALLET_USER2, MAIL_USER2, PASSWORD_USER2, WALLETCHAIN_USER2, ROLE_USER2, LOCALE_USER2);

        Mockito.when(userDao.getUserById(ID_USER1)).thenReturn(Optional.of(user));
        Mockito.when(reviewDao.getUserReviewsAmount(ID_USER1)).thenReturn(0);

        int pageAmount = reviewService.getUserReviewsPageAmount(ID_USER1);

        assertEquals(1, pageAmount);
    }

    @Test(expected = UserNotFoundException.class)
    public void testHasReviewByUserOnInvalidReviewer(){
        User reviewee = new User(ID_USER2, USERNAME_USER2, WALLET_USER2, MAIL_USER2, PASSWORD_USER2, WALLETCHAIN_USER2, ROLE_USER2, LOCALE_USER2);

        Mockito.when(userDao.getUserById(ID_USER2)).thenReturn(Optional.of(reviewee));
        Mockito.when(userDao.getUserById(ID_USER1)).thenReturn(Optional.empty());

        reviewService.hasReviewByUser(ID_USER1, ID_USER2);
    }

    @Test(expected = UserNotFoundException.class)
    public void testHasReviewByUserOnInvalidReviewee(){
        User reviewer = new User(ID_USER1, USERNAME_USER1, WALLET_USER1, MAIL_USER1, PASSWORD_USER1, WALLETCHAIN_USER1, ROLE_USER1, LOCALE_USER1);

        Mockito.when(userDao.getUserById(ID_USER2)).thenReturn(Optional.empty());
        Mockito.when(userDao.getUserById(ID_USER1)).thenReturn(Optional.of(reviewer));

        reviewService.hasReviewByUser(ID_USER1, ID_USER2);
    }

    @Test(expected = UserNotFoundException.class)
    public void testGetUserReviewsAmountOnInvalidUser(){
        Mockito.when(userDao.getUserById(ID_USER1)).thenReturn(Optional.empty());

        reviewService.getUserReviewsAmount(ID_USER1);
    }

}
