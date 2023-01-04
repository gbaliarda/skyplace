package ar.edu.itba.paw.service;

import ar.edu.itba.paw.exceptions.ImageNotFoundException;
import ar.edu.itba.paw.exceptions.InvalidChainException;
import ar.edu.itba.paw.exceptions.UserNotFoundException;
import ar.edu.itba.paw.model.*;
import ar.edu.itba.paw.persistence.NftDao;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.util.*;

import static org.junit.Assert.*;

@RunWith(MockitoJUnitRunner.class)
public class NftServiceImplTest {

    // Chain name should not be part of Chain enum values
    private static final String INVALID_CHAIN_TEXT = "MyPrivateChain";

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

    private final static int ID_OWNER = 1;
    private final static int ID_NFT = 1;
    private final static String NFT_CONTRACT_ADDR = "0xC37575F40b525FCA00554cB627CfFF8693d6b8B4";
    private final static String NFT_NAME = "Test nft";
    private final static Chain NFT_CHAIN = Chain.Cardano;
    private final static String NFT_CHAIN_TEXT = String.valueOf(NFT_CHAIN);
    private final static int ID_IMAGE_NFT = 1;
    private final static String NFT_COLLECTION = "Test nfts";
    private final static String NFT_DESCRIPTION = "A test nft";

    private final static int PAGE_NUM = 1;
    private final static int PAGE_SIZE = 5;
    private final static String DEFAULT_STATUS_BUY_ORDER = StatusBuyOrder.NEW.toString();

    private final String PUBLICATIONS_CATEGORY = Category.Gaming.toString();
    private final static String PUBLICATIONS_CHAIN = Chain.Ethereum.toString();
    private final static BigDecimal PUBLICATIONS_MIN_PRICE = BigDecimal.ZERO;
    private final static BigDecimal PUBLICATIONS_MAX_PRICE = BigDecimal.TEN;
    private final static String PUBLICATIONS_SORT = "MYSORT";
    private final static String PUBLICATIONS_SEARCH = "MYSEARCH";
    private final static String PUBLICATIONS_SEARCHFOR = "MYSEARCHFOR";
    private final static String PUBLICATIONS_TYPE = "MYPUBLICATIONSTYPE";

    @InjectMocks
    private NftServiceImpl nftService;

    @Mock
    private UserService userService;

    @Mock
    private ImageService imageService;

    @Mock
    private MailingService mailingService;

    @Mock
    private NftDao nftDao;

    @Test(expected = InvalidChainException.class)
    public void testCreateNftOnInvalidChain(){
        byte[] imageBytes = new byte[]{};
        MultipartFile image = new MockMultipartFile("image", imageBytes);
        nftService.create(ID_NFT, NFT_CONTRACT_ADDR, NFT_NAME, INVALID_CHAIN_TEXT, image, ID_OWNER, NFT_COLLECTION, NFT_DESCRIPTION);
    }

    @Test(expected = UserNotFoundException.class)
    public void testCreateNftOnInvalidOwner() {
        User user = new User(ID_USER1, USERNAME_USER1, WALLET_USER1, MAIL_USER1, PASSWORD_USER1, WALLETCHAIN_USER1, ROLE_USER1, LOCALE_USER1);
        Nft nft = new Nft(ID_NFT, NFT_CONTRACT_ADDR, NFT_NAME, NFT_CHAIN, ID_IMAGE_NFT, NFT_COLLECTION, NFT_DESCRIPTION, user);

        byte[] imageBytes = new byte[]{};
        MultipartFile image = new MockMultipartFile("image", imageBytes);

        Mockito.when(nftDao.getNftByPk(ID_NFT, NFT_CONTRACT_ADDR, NFT_CHAIN_TEXT)).thenReturn(Optional.of(nft));
        Mockito.when(userService.getUserById(ID_OWNER)).thenReturn(Optional.empty());

        nftService.create(ID_NFT, NFT_CONTRACT_ADDR, NFT_NAME, NFT_CHAIN_TEXT, image, ID_OWNER, NFT_COLLECTION, NFT_DESCRIPTION);
    }

    @Test(expected = ImageNotFoundException.class)
    public void testCreateNftOnInvalidNftImage() {
        User user = new User(ID_USER1, USERNAME_USER1, WALLET_USER1, MAIL_USER1, PASSWORD_USER1, WALLETCHAIN_USER1, ROLE_USER1, LOCALE_USER1);
        Nft nft = new Nft(ID_NFT, NFT_CONTRACT_ADDR, NFT_NAME, NFT_CHAIN, ID_IMAGE_NFT, NFT_COLLECTION, NFT_DESCRIPTION, user);

        byte[] imageBytes = new byte[]{};
        MultipartFile image = new MockMultipartFile("image", imageBytes);

        Mockito.when(nftDao.getNftByPk(ID_NFT, NFT_CONTRACT_ADDR, NFT_CHAIN_TEXT)).thenReturn(Optional.of(nft));
        Mockito.when(userService.getUserById(ID_OWNER)).thenReturn(Optional.of(user));
        Mockito.when(imageService.getImage(ID_IMAGE_NFT)).thenReturn(Optional.empty());

        nftService.create(ID_NFT, NFT_CONTRACT_ADDR, NFT_NAME, NFT_CHAIN_TEXT, image, ID_OWNER, NFT_COLLECTION, NFT_DESCRIPTION);
    }

    @Test
    public void testCreateNftOnValidData(){
        User user = new User(ID_USER1, USERNAME_USER1, WALLET_USER1, MAIL_USER1, PASSWORD_USER1, WALLETCHAIN_USER1, ROLE_USER1, LOCALE_USER1);
        Nft nft = new Nft(ID_NFT, NFT_CONTRACT_ADDR, NFT_NAME, NFT_CHAIN, ID_IMAGE_NFT, NFT_COLLECTION, NFT_DESCRIPTION, user);

        byte[] imageBytes = new byte[]{ 10, 20, 30, 40 };
        MultipartFile imageFile = new MockMultipartFile("image", imageBytes);
        Image image = new Image(ID_IMAGE_NFT, imageBytes);
        Locale userLocale = new Locale(LOCALE_USER1);

        Mockito.when(nftDao.getNftByPk(ID_NFT, NFT_CONTRACT_ADDR, NFT_CHAIN_TEXT)).thenReturn(Optional.of(nft));
        Mockito.when(userService.getUserById(ID_OWNER)).thenReturn(Optional.of(user));
        Mockito.when(imageService.getImage(ID_IMAGE_NFT)).thenReturn(Optional.of(image));
        Mockito.doNothing().when(mailingService).sendNftCreatedMail(MAIL_USER1, USERNAME_USER1, ID_NFT, NFT_NAME, NFT_CONTRACT_ADDR, imageBytes, userLocale, ID_NFT);

        nftService.create(ID_NFT, NFT_CONTRACT_ADDR, NFT_NAME, NFT_CHAIN_TEXT, imageFile, ID_OWNER, NFT_COLLECTION, NFT_DESCRIPTION);
    }

    @Test
    public void testGetAllPublicationsWithNoPublications(){
        User user = new User(ID_USER1, USERNAME_USER1, WALLET_USER1, MAIL_USER1, PASSWORD_USER1, WALLETCHAIN_USER1, ROLE_USER1, LOCALE_USER1);

        Mockito.when(nftDao.getAllPublications(PAGE_NUM, PAGE_SIZE, Collections.singletonList(DEFAULT_STATUS_BUY_ORDER), Collections.singletonList(PUBLICATIONS_CATEGORY), Collections.singletonList(PUBLICATIONS_CHAIN),
                PUBLICATIONS_MIN_PRICE, PUBLICATIONS_MAX_PRICE, PUBLICATIONS_SORT, PUBLICATIONS_SEARCH, PUBLICATIONS_SEARCHFOR, null)).thenReturn(Collections.emptyList());
        Mockito.when(userService.getCurrentUser()).thenReturn(Optional.of(user));

        List<Publication> publications = nftService.getAllPublications(PAGE_NUM, Collections.singletonList(DEFAULT_STATUS_BUY_ORDER), Collections.singletonList(PUBLICATIONS_CATEGORY), Collections.singletonList(PUBLICATIONS_CHAIN),
                PUBLICATIONS_MIN_PRICE, PUBLICATIONS_MAX_PRICE, PUBLICATIONS_SORT, PUBLICATIONS_SEARCH, PUBLICATIONS_SEARCHFOR);

        assertEquals(0, publications.size());
    }

    @Test
    public void testGetAllPublicationsByUserOnUserNotLoggedIn(){
        User otherUser = new User(ID_USER1, USERNAME_USER1, WALLET_USER1, MAIL_USER1, PASSWORD_USER1, WALLETCHAIN_USER1, ROLE_USER1, LOCALE_USER1);
        Mockito.when(userService.getCurrentUser()).thenReturn(Optional.empty());

        List<Publication> userPublications = nftService.getAllPublicationsByUser(PAGE_NUM, otherUser, PUBLICATIONS_TYPE, PUBLICATIONS_SORT);

        assertEquals(0, userPublications.size());
    }

    @Test
    public void testGetAllPublicationsByUserOnUserNotCurrentUser(){
        User currentUser = new User(ID_USER1, USERNAME_USER1, WALLET_USER1, MAIL_USER1, PASSWORD_USER1, WALLETCHAIN_USER1, ROLE_USER1, LOCALE_USER1);
        User otherUser = new User(ID_USER2, USERNAME_USER2, WALLET_USER2, MAIL_USER2, PASSWORD_USER2, WALLETCHAIN_USER2, ROLE_USER2, LOCALE_USER2);

        Mockito.when(userService.getCurrentUser()).thenReturn(Optional.of(currentUser));

        List<Publication> userPublications = nftService.getAllPublicationsByUser(PAGE_NUM, otherUser, PUBLICATIONS_TYPE, PUBLICATIONS_SORT);

        assertEquals(0, userPublications.size());
    }

    @Test
    public void testGetAllPublicationsByUserOnInvalidCollectionType(){
        User currentUser = new User(ID_USER1, USERNAME_USER1, WALLET_USER1, MAIL_USER1, PASSWORD_USER1, WALLETCHAIN_USER1, ROLE_USER1, LOCALE_USER1);

        Mockito.when(userService.getCurrentUser()).thenReturn(Optional.of(currentUser));

        List<Publication> userPublications = nftService.getAllPublicationsByUser(PAGE_NUM, currentUser, PUBLICATIONS_TYPE, PUBLICATIONS_SORT);

        assertEquals(0, userPublications.size());
    }

    @Test
    public void tesGetAmountPublicationPagesByUserOnInvalidPublicationType(){
        User currentUser = new User(ID_USER1, USERNAME_USER1, WALLET_USER1, MAIL_USER1, PASSWORD_USER1, WALLETCHAIN_USER1, ROLE_USER1, LOCALE_USER1);
        User otherUser = new User(ID_USER2, USERNAME_USER2, WALLET_USER2, MAIL_USER2, PASSWORD_USER2, WALLETCHAIN_USER2, ROLE_USER2, LOCALE_USER2);

        int amountPublicationPagesByUser = nftService.getAmountPublicationPagesByUser(otherUser, currentUser, PUBLICATIONS_TYPE);

        assertEquals(1, amountPublicationPagesByUser);
    }

}