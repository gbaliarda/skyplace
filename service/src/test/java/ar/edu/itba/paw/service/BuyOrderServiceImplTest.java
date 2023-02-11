package ar.edu.itba.paw.service;

import ar.edu.itba.paw.exceptions.*;
import ar.edu.itba.paw.model.*;
import ar.edu.itba.paw.persistence.BuyOrderDao;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.*;
import org.mockito.runners.MockitoJUnitRunner;

import java.math.BigDecimal;
import java.util.*;

import static org.junit.Assert.*;

@RunWith(MockitoJUnitRunner.class)
public class BuyOrderServiceImplTest {

    private final static int ID_SELLORDER = 1;

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

    private final static int ID_BUYER = 1;
    private final static int ID_SELLER = 2;
    private final static int ID_PURCHASE = 1;

    private final static int ID_NFT = 1;
    private final static String NFT_CONTRACT_ADDR = "0xC37575F40b525FCA00554cB627CfFF8693d6b8B4";
    private final static String NFT_NAME = "Test nft";
    private final static Chain NFT_CHAIN = Chain.Cardano;
    private final static int ID_IMAGE_NFT = 1;
    private final static String NFT_COLLECTION = "Test nfts";
    private final static String NFT_DESCRIPTION = "A test nft";

    private final static StatusBuyOrder DEFAULT_STATUS_BUY_ORDER = StatusBuyOrder.NEW;
    private final static Date PENDING_DATE_BUY_ORDER = null;
    private final static int PAGE_NUM = 1;

    private final static String TX_HASH = "3E5XGTx46hAJYUNFZ5a8EhGZ7hXR8irenxTozSdZ84gq4N1hqvSGAtuvm9aTKGTRjaFbU1TDLtdQFg4GHK5XxiSi";

    private final static Category CATEGORY = Category.Photography;
    private final static BigDecimal testPrice = new BigDecimal("0.001");

    @InjectMocks
    @Spy
    private BuyOrderServiceImpl buyOrderService;

    @Mock
    private BuyOrderDao buyOrderDao;
    @Mock
    private UserService userService;
    @Mock
    private SellOrderService sellOrderService;
    @Mock
    private ImageService imageService;
    @Mock
    private PurchaseService purchaseService;
    @Mock
    private EtherscanService etherscanService;
    @Mock
    private MailingService mailingService;

    @Test(expected = UserNoPermissionException.class)
    public void testCreateBuyOrderOnOwnedSellOrder() {
        Mockito.when(sellOrderService.currentUserOwnsSellOrder(ID_SELLORDER)).thenReturn(true);

        buyOrderService.create(ID_SELLORDER, testPrice, ID_USER1);
    }

    @Test(expected = SellOrderNotFoundException.class)
    public void testCreateBuyOrderWithUnexistentSellOrderId() {
        Mockito.when(sellOrderService.currentUserOwnsSellOrder(ID_SELLORDER)).thenReturn(false);
        Mockito.when(sellOrderService.getOrderById(ID_SELLORDER)).thenReturn(Optional.empty());

        buyOrderService.create(ID_SELLORDER, testPrice, ID_USER1);
    }

    @Test(expected = UserNotFoundException.class)
    public void testCreateBuyOrderWithUnexistentBidderData() {
        User seller = new User(USERNAME_USER2, WALLET_USER2, MAIL_USER2, PASSWORD_USER2, WALLETCHAIN_USER2, ROLE_USER2, LOCALE_USER2);
        Nft nft = new Nft(ID_NFT, NFT_CONTRACT_ADDR, NFT_NAME, NFT_CHAIN, ID_IMAGE_NFT, NFT_COLLECTION, NFT_DESCRIPTION, seller);
        SellOrder sellOrder = new SellOrder(testPrice, nft, CATEGORY);

        Mockito.when(sellOrderService.currentUserOwnsSellOrder(ID_SELLORDER)).thenReturn(false);
        Mockito.when(sellOrderService.getOrderById(ID_SELLORDER)).thenReturn(Optional.of(sellOrder));
        Mockito.when(userService.getUserById(ID_USER1)).thenReturn(Optional.empty());

        buyOrderService.create(ID_SELLORDER, testPrice, ID_USER1);
    }

    // TODO: Check create test errors (getUserId() returns null) -> add User constructor with id?
    @Test(expected = UserNotFoundException.class)
    public void testCreateBuyOrderWithUnexistentSellerData() {
        User seller = new User(ID_USER2, USERNAME_USER2, WALLET_USER2, MAIL_USER2, PASSWORD_USER2, WALLETCHAIN_USER2, ROLE_USER2, LOCALE_USER2);
        User bidder = new User(ID_USER1, USERNAME_USER1, WALLET_USER1, MAIL_USER1, PASSWORD_USER1, WALLETCHAIN_USER1, ROLE_USER1, LOCALE_USER1);
        Nft nft = new Nft(ID_NFT, NFT_CONTRACT_ADDR, NFT_NAME, NFT_CHAIN, ID_IMAGE_NFT, NFT_COLLECTION, NFT_DESCRIPTION, seller);
        SellOrder sellOrder = new SellOrder(testPrice, nft, CATEGORY);

        Mockito.when(sellOrderService.currentUserOwnsSellOrder(ID_SELLORDER)).thenReturn(false);
        Mockito.when(sellOrderService.getOrderById(ID_SELLORDER)).thenReturn(Optional.of(sellOrder));
        Mockito.when(userService.getUserById(ID_USER1)).thenReturn(Optional.of(bidder));
        Mockito.when(userService.getUserById(ID_USER2)).thenReturn(Optional.empty());

        buyOrderService.create(ID_SELLORDER, testPrice, ID_USER1);
    }

    @Test(expected = ImageNotFoundException.class)
    public void testCreateBuyOrderWithUnexistentNftImage() {
        User seller = new User(ID_USER2, USERNAME_USER2, WALLET_USER2, MAIL_USER2, PASSWORD_USER2, WALLETCHAIN_USER2, ROLE_USER2, LOCALE_USER2);
        User bidder = new User(ID_USER1, USERNAME_USER1, WALLET_USER1, MAIL_USER1, PASSWORD_USER1, WALLETCHAIN_USER1, ROLE_USER1, LOCALE_USER1);
        Nft nft = new Nft(ID_NFT, NFT_CONTRACT_ADDR, NFT_NAME, NFT_CHAIN, ID_IMAGE_NFT, NFT_COLLECTION, NFT_DESCRIPTION, seller);
        SellOrder sellOrder = new SellOrder(testPrice, nft, CATEGORY);

        Mockito.when(sellOrderService.currentUserOwnsSellOrder(ID_SELLORDER)).thenReturn(false);
        Mockito.when(sellOrderService.getOrderById(ID_SELLORDER)).thenReturn(Optional.of(sellOrder));
        Mockito.when(userService.getUserById(ID_USER1)).thenReturn(Optional.of(bidder));
        Mockito.when(userService.getUserById(ID_USER2)).thenReturn(Optional.of(seller));
        Mockito.when(imageService.getImage(ID_IMAGE_NFT)).thenReturn(Optional.empty());

        buyOrderService.create(ID_SELLORDER, testPrice, ID_USER1);
    }

    @Test
    public void testCreateBuyOrderWithValidData(){
        User seller = new User(ID_USER2, USERNAME_USER2, WALLET_USER2, MAIL_USER2, PASSWORD_USER2, WALLETCHAIN_USER2, ROLE_USER2, LOCALE_USER2);
        User bidder = new User(ID_USER1, USERNAME_USER1, WALLET_USER1, MAIL_USER1, PASSWORD_USER1, WALLETCHAIN_USER1, ROLE_USER1, LOCALE_USER1);
        Nft nft = new Nft(ID_NFT, NFT_CONTRACT_ADDR, NFT_NAME, NFT_CHAIN, ID_IMAGE_NFT, NFT_COLLECTION, NFT_DESCRIPTION, seller);
        SellOrder sellOrder = new SellOrder(testPrice, nft, CATEGORY);
        byte[] imageBytes = new byte[]{};
        Image image = new Image(ID_IMAGE_NFT, imageBytes);

        Mockito.when(sellOrderService.currentUserOwnsSellOrder(ID_SELLORDER)).thenReturn(false);
        Mockito.when(sellOrderService.getOrderById(ID_SELLORDER)).thenReturn(Optional.of(sellOrder));
        Mockito.when(userService.getUserById(ID_USER1)).thenReturn(Optional.of(bidder));
        Mockito.when(userService.getUserById(ID_USER2)).thenReturn(Optional.of(seller));
        Mockito.when(imageService.getImage(ID_IMAGE_NFT)).thenReturn(Optional.of(image));

        buyOrderService.create(ID_SELLORDER, testPrice, ID_USER1);
    }

    @Test
    public void testCheckPendingOrdersDateForUser(){
        User seller = new User(ID_USER2, USERNAME_USER2, WALLET_USER2, MAIL_USER2, PASSWORD_USER2, WALLETCHAIN_USER2, ROLE_USER2, LOCALE_USER2);
        User bidder = new User(ID_USER1, USERNAME_USER1, WALLET_USER1, MAIL_USER1, PASSWORD_USER1, WALLETCHAIN_USER1, ROLE_USER1, LOCALE_USER1);
        Nft nft = new Nft(ID_NFT, NFT_CONTRACT_ADDR, NFT_NAME, NFT_CHAIN, ID_IMAGE_NFT, NFT_COLLECTION, NFT_DESCRIPTION, seller);
        SellOrder sellOrder = new SellOrder(testPrice, nft, CATEGORY);
        BuyOrder buyOrder = new BuyOrder(testPrice, sellOrder, bidder, DEFAULT_STATUS_BUY_ORDER, PENDING_DATE_BUY_ORDER);
        List<BuyOrder> buyOrderList = new ArrayList<>();
        buyOrderList.add(buyOrder);

        Mockito.when(buyOrderDao.getExpiredPendingOffersByUser(bidder)).thenReturn(buyOrderList);

        buyOrderService.checkPendingOrdersDateForUser(bidder);
    }

    @Test
    public void testGetAmountPagesBySellOrderIdOnSellOrderWithoutOffers() {
        User seller = new User(USERNAME_USER1, WALLET_USER1, MAIL_USER1, PASSWORD_USER1, WALLETCHAIN_USER1, ROLE_USER1, LOCALE_USER1);
        Nft nft = new Nft(ID_NFT, NFT_CONTRACT_ADDR, NFT_NAME, NFT_CHAIN, ID_IMAGE_NFT, NFT_COLLECTION, NFT_DESCRIPTION, seller);
        SellOrder sellOrder = new SellOrder(testPrice, nft, CATEGORY);

        Mockito.when(buyOrderDao.getAmountBuyOrders(sellOrder, "ALL")).thenReturn(0);

        int pageAmount = buyOrderService.getAmountPagesBySellOrderId(sellOrder, "ALL");

        assertEquals(1, pageAmount);
    }

    @Test
    public void testGetAmountPagesForUserOnUserWithoutSellOrders() {
        User user = new User(USERNAME_USER1, WALLET_USER1, MAIL_USER1, PASSWORD_USER1, WALLETCHAIN_USER1, ROLE_USER1, LOCALE_USER1);

        Mockito.when(buyOrderDao.getAmountBuyOrdersForUser(user, "ALL")).thenReturn(0);

        int pageAmount = buyOrderService.getAmountPagesForUser(user, "ALL");

        assertEquals(1, pageAmount);
    }

    @Test
    public void testConfirmBuyOrderOnUnexistentBuyOrder(){
        Mockito.when(buyOrderDao.getBuyOrder(ID_SELLORDER, ID_BUYER)).thenReturn(Optional.empty());

        Optional<Integer> result = buyOrderService.confirmBuyOrder(ID_SELLORDER, ID_BUYER, TX_HASH);

        assertFalse(result.isPresent());
    }

    @Test
    public void testConfirmBuyOrderOnBuyOrderWtihValidData(){
        User seller = new User(ID_USER2, USERNAME_USER2, WALLET_USER2, MAIL_USER2, PASSWORD_USER2, WALLETCHAIN_USER2, ROLE_USER2, LOCALE_USER2);
        User bidder = new User(ID_USER1, USERNAME_USER1, WALLET_USER1, MAIL_USER1, PASSWORD_USER1, WALLETCHAIN_USER1, ROLE_USER1, LOCALE_USER1);
        Nft nft = new Nft(ID_NFT, NFT_CONTRACT_ADDR, NFT_NAME, NFT_CHAIN, ID_IMAGE_NFT, NFT_COLLECTION, NFT_DESCRIPTION, seller);
        SellOrder sellOrder = new SellOrder(testPrice, nft, CATEGORY);
        BuyOrder buyOrder = new BuyOrder(testPrice, sellOrder, bidder, DEFAULT_STATUS_BUY_ORDER, PENDING_DATE_BUY_ORDER);
        byte[] imageBytes = new byte[]{10, 20, 30};
        Image image = new Image(ID_IMAGE_NFT, imageBytes);
        Locale sellerLocale = new Locale(LOCALE_USER2);

        Mockito.when(buyOrderDao.getBuyOrder(ID_SELLORDER, ID_BUYER)).thenReturn(Optional.of(buyOrder));
        Mockito.when(imageService.getImage(ID_IMAGE_NFT)).thenReturn(Optional.of(image));
        Mockito.doNothing().when(mailingService).sendOfferAcceptedMail(MAIL_USER1, MAIL_USER2, ID_SELLER, USERNAME_USER1, NFT_NAME, ID_NFT, NFT_CONTRACT_ADDR, testPrice, imageBytes, sellerLocale, ID_NFT);

        buyOrderService.confirmBuyOrder(ID_SELLORDER, ID_BUYER, TX_HASH);
    }

    @Test(expected = UserNotLoggedInException.class)
    public void testDeleteBuyOrderOnUserNotLoggedin(){
        Mockito.when(userService.getCurrentUser()).thenReturn(Optional.empty());

        buyOrderService.deleteBuyOrder(ID_SELLORDER, ID_BUYER);
    }

    @Test(expected = UserIsNotNftOwnerException.class)
    public void testDeleteBuyOrderOnUserNotNftOwner() {
        User seller = new User(ID_USER2, USERNAME_USER2, WALLET_USER2, MAIL_USER2, PASSWORD_USER2, WALLETCHAIN_USER2, ROLE_USER2, LOCALE_USER2);

        Mockito.when(userService.getCurrentUser()).thenReturn(Optional.of(seller));
        Mockito.when(sellOrderService.currentUserOwnsSellOrder(ID_SELLORDER)).thenReturn(false);

        buyOrderService.deleteBuyOrder(ID_SELLORDER, ID_BUYER);
    }

    @Test
    public void testDeleteBuyOrderOnBuyOrderNotPresent(){
        User seller = new User(ID_USER2, USERNAME_USER2, WALLET_USER2, MAIL_USER2, PASSWORD_USER2, WALLETCHAIN_USER2, ROLE_USER2, LOCALE_USER2);

        Mockito.when(userService.getCurrentUser()).thenReturn(Optional.of(seller));
        Mockito.when(sellOrderService.currentUserOwnsSellOrder(ID_SELLORDER)).thenReturn(true);
        Mockito.when(buyOrderDao.getBuyOrder(ID_SELLORDER, ID_BUYER)).thenReturn(Optional.empty());

        buyOrderService.deleteBuyOrder(ID_SELLORDER, ID_BUYER);
    }

    @Test(expected = ImageNotFoundException.class)
    public void testDeleteBuyOrderOnBuyOrderWithoutImage(){
        User seller = new User(ID_USER2, USERNAME_USER2, WALLET_USER2, MAIL_USER2, PASSWORD_USER2, WALLETCHAIN_USER2, ROLE_USER2, LOCALE_USER2);
        User bidder = new User(ID_USER1, USERNAME_USER1, WALLET_USER1, MAIL_USER1, PASSWORD_USER1, WALLETCHAIN_USER1, ROLE_USER1, LOCALE_USER1);
        Nft nft = new Nft(ID_NFT, NFT_CONTRACT_ADDR, NFT_NAME, NFT_CHAIN, ID_IMAGE_NFT, NFT_COLLECTION, NFT_DESCRIPTION, seller);
        SellOrder sellOrder = new SellOrder(testPrice, nft, CATEGORY);
        BuyOrder buyOrder = new BuyOrder(testPrice, sellOrder, bidder, DEFAULT_STATUS_BUY_ORDER, PENDING_DATE_BUY_ORDER);

        Mockito.when(userService.getCurrentUser()).thenReturn(Optional.of(seller));
        Mockito.when(sellOrderService.currentUserOwnsSellOrder(ID_SELLORDER)).thenReturn(true);
        Mockito.when(buyOrderDao.getBuyOrder(ID_SELLORDER, ID_BUYER)).thenReturn(Optional.of(buyOrder));
        Mockito.when(imageService.getImage(ID_IMAGE_NFT)).thenReturn(Optional.empty());

        buyOrderService.deleteBuyOrder(ID_SELLORDER, ID_BUYER);
    }

    @Test
    public void testDeleteBuyOrderWithValidData(){
        User seller = new User(ID_USER2, USERNAME_USER2, WALLET_USER2, MAIL_USER2, PASSWORD_USER2, WALLETCHAIN_USER2, ROLE_USER2, LOCALE_USER2);
        User bidder = new User(ID_USER1, USERNAME_USER1, WALLET_USER1, MAIL_USER1, PASSWORD_USER1, WALLETCHAIN_USER1, ROLE_USER1, LOCALE_USER1);
        Nft nft = new Nft(ID_NFT, NFT_CONTRACT_ADDR, NFT_NAME, NFT_CHAIN, ID_IMAGE_NFT, NFT_COLLECTION, NFT_DESCRIPTION, seller);
        SellOrder sellOrder = new SellOrder(testPrice, nft, CATEGORY);
        BuyOrder buyOrder = new BuyOrder(testPrice, sellOrder, bidder, DEFAULT_STATUS_BUY_ORDER, PENDING_DATE_BUY_ORDER);
        byte[] imageBytes = new byte[]{};
        Image image = new Image(ID_IMAGE_NFT, imageBytes);

        Mockito.when(userService.getCurrentUser()).thenReturn(Optional.of(seller));
        Mockito.when(sellOrderService.currentUserOwnsSellOrder(ID_SELLORDER)).thenReturn(true);
        Mockito.when(buyOrderDao.getBuyOrder(ID_SELLORDER, ID_BUYER)).thenReturn(Optional.of(buyOrder));
        Mockito.when(imageService.getImage(ID_IMAGE_NFT)).thenReturn(Optional.of(image));

        buyOrderService.deleteBuyOrder(ID_SELLORDER, ID_BUYER);
    }

    @Test
    public void testValidateTransactionOnTxAlreadyInUse(){
        Mockito.when(purchaseService.isTxHashAlreadyInUse(TX_HASH)).thenReturn(true);
        Mockito.doReturn(Optional.of(new SellOrder(null, null, null))).when(sellOrderService).getOrderById(ID_SELLORDER);
        Optional<Integer> result = buyOrderService.validateTransaction(TX_HASH, ID_SELLORDER, ID_BUYER);

        assertFalse(result.isPresent());
    }

    @Test
    public void testValidateTransactionOnBuyOrderNotPresent(){
        Mockito.when(purchaseService.isTxHashAlreadyInUse(TX_HASH)).thenReturn(false);
        Mockito.when(buyOrderService.getPendingBuyOrder(ID_SELLORDER)).thenReturn(Optional.empty());
        Mockito.doReturn(Optional.of(new SellOrder(null, null, null))).when(sellOrderService).getOrderById(ID_SELLORDER);

        Optional<Integer> result = buyOrderService.validateTransaction(TX_HASH, ID_SELLORDER, ID_BUYER);

        assertFalse(result.isPresent());
    }

    @Test
    public void testValidateTransactionOnBuyOrderNotPending(){
        User seller = new User(ID_USER2, USERNAME_USER2, WALLET_USER2, MAIL_USER2, PASSWORD_USER2, WALLETCHAIN_USER2, ROLE_USER2, LOCALE_USER2);
        User bidder = new User(ID_USER1, USERNAME_USER1, WALLET_USER1, MAIL_USER1, PASSWORD_USER1, WALLETCHAIN_USER1, ROLE_USER1, LOCALE_USER1);
        Nft nft = new Nft(ID_NFT, NFT_CONTRACT_ADDR, NFT_NAME, NFT_CHAIN, ID_IMAGE_NFT, NFT_COLLECTION, NFT_DESCRIPTION, seller);
        SellOrder sellOrder = new SellOrder(testPrice, nft, CATEGORY);
        BuyOrder buyOrder = new BuyOrder(testPrice, sellOrder, bidder, DEFAULT_STATUS_BUY_ORDER, PENDING_DATE_BUY_ORDER);

        Mockito.when(purchaseService.isTxHashAlreadyInUse(TX_HASH)).thenReturn(false);
        Mockito.when(buyOrderService.getPendingBuyOrder(ID_SELLORDER)).thenReturn(Optional.of(buyOrder));
        Mockito.doReturn(Optional.of(new SellOrder(null, null, null))).when(sellOrderService).getOrderById(ID_SELLORDER);

        Optional<Integer> result = buyOrderService.validateTransaction(TX_HASH, ID_SELLORDER, ID_BUYER);

        assertFalse(result.isPresent());
    }

    @Test(expected = UserNotFoundException.class)
    public void testValidateTransactionOnInvalidUser(){
        User seller = new User(ID_USER2, USERNAME_USER2, WALLET_USER2, MAIL_USER2, PASSWORD_USER2, WALLETCHAIN_USER2, ROLE_USER2, LOCALE_USER2);
        User bidder = new User(ID_USER1, USERNAME_USER1, WALLET_USER1, MAIL_USER1, PASSWORD_USER1, WALLETCHAIN_USER1, ROLE_USER1, LOCALE_USER1);
        Nft nft = new Nft(ID_NFT, NFT_CONTRACT_ADDR, NFT_NAME, NFT_CHAIN, ID_IMAGE_NFT, NFT_COLLECTION, NFT_DESCRIPTION, seller);
        SellOrder sellOrder = new SellOrder(testPrice, nft, CATEGORY);
        BuyOrder buyOrder = new BuyOrder(testPrice, sellOrder, bidder, StatusBuyOrder.PENDING, PENDING_DATE_BUY_ORDER);

        Mockito.when(purchaseService.isTxHashAlreadyInUse(TX_HASH)).thenReturn(false);
        Mockito.when(buyOrderService.getPendingBuyOrder(ID_SELLORDER)).thenReturn(Optional.of(buyOrder));
        Mockito.when(userService.getUserById(ID_BUYER)).thenReturn(Optional.empty());
        Mockito.doReturn(Optional.of(new SellOrder(null, null, null))).when(sellOrderService).getOrderById(ID_SELLORDER);

        buyOrderService.validateTransaction(TX_HASH, ID_SELLORDER, ID_BUYER);
    }

    @Test
    public void testValidateTransactionOnInvalidTransaction(){
        User seller = new User(ID_USER2, USERNAME_USER2, WALLET_USER2, MAIL_USER2, PASSWORD_USER2, WALLETCHAIN_USER2, ROLE_USER2, LOCALE_USER2);
        User bidder = new User(ID_USER1, USERNAME_USER1, WALLET_USER1, MAIL_USER1, PASSWORD_USER1, WALLETCHAIN_USER1, ROLE_USER1, LOCALE_USER1);
        Nft nft = new Nft(ID_NFT, NFT_CONTRACT_ADDR, NFT_NAME, NFT_CHAIN, ID_IMAGE_NFT, NFT_COLLECTION, NFT_DESCRIPTION, seller);
        SellOrder sellOrder = new SellOrder(testPrice, nft, CATEGORY);
        BuyOrder buyOrder = new BuyOrder(testPrice, sellOrder, bidder, StatusBuyOrder.PENDING, PENDING_DATE_BUY_ORDER);

        Mockito.when(purchaseService.isTxHashAlreadyInUse(TX_HASH)).thenReturn(false);
        Mockito.when(buyOrderService.getPendingBuyOrder(ID_SELLORDER)).thenReturn(Optional.of(buyOrder));
        Mockito.when(userService.getUserById(ID_BUYER)).thenReturn(Optional.of(bidder));
        Mockito.when(etherscanService.isTransactionValid(TX_HASH, WALLET_USER1, WALLET_USER2, testPrice)).thenReturn(false);
        Mockito.doReturn(Optional.of(new SellOrder(null, null, null))).when(sellOrderService).getOrderById(ID_SELLORDER);

        Optional<Integer> result = buyOrderService.validateTransaction(TX_HASH, ID_SELLORDER, ID_BUYER);

        assertFalse(result.isPresent());
    }

    @Test
    public void testNftHasValidTransactionOnSellOrderNotValid(){
        User bidder = new User(USERNAME_USER1, WALLET_USER1, MAIL_USER1, PASSWORD_USER1, WALLETCHAIN_USER1, ROLE_USER1, LOCALE_USER1);

        Mockito.when(userService.getUserById(ID_BUYER)).thenReturn(Optional.of(bidder));
        Mockito.when(sellOrderService.getOrderById(ID_SELLORDER)).thenReturn(Optional.empty());

        boolean result = buyOrderService.nftHasValidTransaction(ID_SELLORDER, ID_BUYER, testPrice, TX_HASH);

        assertFalse(result);
    }

    @Test
    public void testNftHasValidTransactionOnBuyerNotValid(){
        User seller = new User(ID_USER2, USERNAME_USER2, WALLET_USER2, MAIL_USER2, PASSWORD_USER2, WALLETCHAIN_USER2, ROLE_USER2, LOCALE_USER2);
        Nft nft = new Nft(ID_NFT, NFT_CONTRACT_ADDR, NFT_NAME, NFT_CHAIN, ID_IMAGE_NFT, NFT_COLLECTION, NFT_DESCRIPTION, seller);
        SellOrder sellOrder = new SellOrder(testPrice, nft, CATEGORY);

        Mockito.when(sellOrderService.getOrderById(ID_SELLORDER)).thenReturn(Optional.of(sellOrder));
        Mockito.when(userService.getUserById(ID_BUYER)).thenReturn(Optional.empty());

        boolean result = buyOrderService.nftHasValidTransaction(ID_SELLORDER, ID_BUYER, testPrice, TX_HASH);

        assertFalse(result);
    }

    @Test
    public void testNftHasValidTransactionOnInvalidTransaction(){
        User seller = new User(ID_USER2, USERNAME_USER2, WALLET_USER2, MAIL_USER2, PASSWORD_USER2, WALLETCHAIN_USER2, ROLE_USER2, LOCALE_USER2);
        User bidder = new User(ID_USER1, USERNAME_USER1, WALLET_USER1, MAIL_USER1, PASSWORD_USER1, WALLETCHAIN_USER1, ROLE_USER1, LOCALE_USER1);
        Nft nft = new Nft(ID_NFT, NFT_CONTRACT_ADDR, NFT_NAME, NFT_CHAIN, ID_IMAGE_NFT, NFT_COLLECTION, NFT_DESCRIPTION, seller);
        SellOrder sellOrder = new SellOrder(testPrice, nft, CATEGORY);

        Mockito.when(sellOrderService.getOrderById(ID_SELLORDER)).thenReturn(Optional.of(sellOrder));
        Mockito.when(userService.getUserById(ID_BUYER)).thenReturn(Optional.of(bidder));
        Mockito.when(etherscanService.isTransactionValid(TX_HASH, WALLET_USER1, WALLET_USER2, testPrice)).thenReturn(false);
        Mockito.doReturn(Optional.empty()).when(buyOrderService).confirmBuyOrder(ID_SELLORDER, ID_BUYER, TX_HASH);

        boolean result = buyOrderService.nftHasValidTransaction(ID_SELLORDER, ID_BUYER, testPrice, TX_HASH);

        assertFalse(result);
    }

    @Test
    public void testNftHasValidTransactionOnValidTransaction(){
        User seller = new User(ID_USER2, USERNAME_USER2, WALLET_USER2, MAIL_USER2, PASSWORD_USER2, WALLETCHAIN_USER2, ROLE_USER2, LOCALE_USER2);
        User bidder = new User(ID_USER1, USERNAME_USER1, WALLET_USER1, MAIL_USER1, PASSWORD_USER1, WALLETCHAIN_USER1, ROLE_USER1, LOCALE_USER1);
        Nft nft = new Nft(ID_NFT, NFT_CONTRACT_ADDR, NFT_NAME, NFT_CHAIN, ID_IMAGE_NFT, NFT_COLLECTION, NFT_DESCRIPTION, seller);
        SellOrder sellOrder = new SellOrder(testPrice, nft, CATEGORY);

        Mockito.when(sellOrderService.getOrderById(ID_SELLORDER)).thenReturn(Optional.of(sellOrder));
        Mockito.when(userService.getUserById(ID_BUYER)).thenReturn(Optional.of(bidder));
        Mockito.when(etherscanService.isTransactionValid(TX_HASH, WALLET_USER1, WALLET_USER2, testPrice)).thenReturn(true);
        Mockito.doReturn(Optional.of(ID_PURCHASE)).when(buyOrderService).confirmBuyOrder(ID_SELLORDER, ID_BUYER, TX_HASH);

        boolean result = buyOrderService.nftHasValidTransaction(ID_SELLORDER, ID_BUYER, testPrice, TX_HASH);

        assertTrue(result);
    }

}
