package ar.edu.itba.paw.service;

import ar.edu.itba.paw.exceptions.*;
import ar.edu.itba.paw.model.*;
import ar.edu.itba.paw.persistence.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

@Service
public class BuyOrderServiceImpl implements BuyOrderService {
    private final BuyOrderDao buyOrderDao;
    private final UserService userService;
    private final SellOrderService sellOrderService;
    private final ImageService imageService;
    private final MailingService mailingService;
    private final PurchaseService purchaseService;
    private final EtherscanService etherscanService;

    private final int pageSize = 5;
    private static final long SECONDS_IN_ONE_DAY = 24*60*60;

    @Autowired
    public BuyOrderServiceImpl(BuyOrderDao buyOrderDao, UserService userService, SellOrderService sellOrderService, ImageService imageService, MailingService mailingService, PurchaseService purchaseService, EtherscanService etherscanService) {
        this.buyOrderDao = buyOrderDao;
        this.userService = userService;
        this.sellOrderService = sellOrderService;
        this.imageService = imageService;
        this.mailingService = mailingService;
        this.purchaseService = purchaseService;
        this.etherscanService = etherscanService;
    }

    @Transactional
    @Override
    public boolean create(int idSellOrder, BigDecimal price, int userId) {
        if(sellOrderService.currentUserOwnsSellOrder(idSellOrder))
            throw new UserNoPermissionException();

        SellOrder sellOrder = sellOrderService.getOrderById(idSellOrder).orElseThrow(SellOrderNotFoundException::new);
        User bidder = userService.getUserById(userId).orElseThrow(UserNotFoundException::new);
        buyOrderDao.create(sellOrder, price, bidder);
        Nft nft = sellOrder.getNft();
        User seller = userService.getUserById(nft.getOwner().getId()).orElseThrow(UserNotFoundException::new);
        Image image = imageService.getImage(nft.getIdImage()).orElseThrow(ImageNotFoundException::new);
        Locale locale = Locale.forLanguageTag(seller.getLocale());
        mailingService.sendOfferMail(bidder.getEmail(), seller.getEmail(), nft.getNftName(), nft.getNftId(), nft.getContractAddr(), price, image.getImage(), locale, nft.getId());
        return true;
    }

    private void checkPendingOrdersDateBySellOrderId(int sellOrderId) {
        rejectBuyOrder(getPendingBuyOrder(sellOrderId));
    }

    protected void checkPendingOrdersDateForUser(User user) {
        List<BuyOrder> buyOrders = buyOrderDao.getExpiredPendingOffersByUser(user);
        for(BuyOrder b:buyOrders)
            rejectBuyOrder(Optional.of(b));
    }

    @Transactional
    @Override
    public List<BuyOrder> getOrdersBySellOrderId(int page, int sellOrderId, String status) {
        checkPendingOrdersDateBySellOrderId(sellOrderId);
        return buyOrderDao.getOrdersBySellOrderId(page, sellOrderId, status, getPageSize());
    }

    @Override
    public int getAmountPagesBySellOrderId(SellOrder sellOrder, String status) {
        return (buyOrderDao.getAmountBuyOrders(sellOrder, status) - 1) / getPageSize() + 1;
    }

    @Transactional
    @Override
    public List<BuyOrder> getBuyOrdersForUser(User user, int page, String status) {
        checkPendingOrdersDateForUser(user);
        if(!status.equals("MYSALES"))
            return buyOrderDao.getBuyOrdersForUser(user, page, status, getPageSize());
        return buyOrderDao.getPendingBuyOrdersToUser(user, page, getPageSize());
    }

    @Override
    public int getAmountPagesForUser(User user, String status) {
        int size = buyOrderDao.getAmountBuyOrdersForUser(user, status);
        return (size - 1) / getPageSize() + 1;
    }

    protected Optional<Integer> confirmBuyOrder(int sellOrderId, int buyerId, String txHash) {
        Optional<BuyOrder> buyOrder = buyOrderDao.getBuyOrder(sellOrderId, buyerId);
        if(!buyOrder.isPresent())
            return Optional.empty();
        SellOrder sellOrder = buyOrder.get().getOfferedFor();
        User buyer = buyOrder.get().getOfferedBy();
        Nft nft = sellOrder.getNft();
        User seller = nft.getOwner();

        sellOrder.getNft().setOwner(buyer);

        sellOrderService.delete(sellOrder.getId(), buyOrder.get());
        Integer purchaseId = purchaseService.createPurchase(buyerId, seller.getId(), nft.getId(), buyOrder.get().getAmount(), txHash, StatusPurchase.SUCCESS);
        return Optional.of(purchaseId);
    }

    @Transactional
    @Override
    public void acceptBuyOrder(int sellOrderId, int buyerId) {
        if(!sellOrderService.getOrderById(sellOrderId).isPresent())
            throw new SellOrderNotFoundException();
        if(!userService.getUserById(buyerId).isPresent())
            throw new UserNotFoundException();
        if(sellOrderPendingBuyOrder(sellOrderId))
            throw new SellOrderHasPendingBuyOrderException();
        buyOrderDao.acceptBuyOrder(sellOrderId, buyerId);

        Optional<BuyOrder> buyOrder = buyOrderDao.getBuyOrder(sellOrderId, buyerId);
        SellOrder sellOrder = buyOrder.get().getOfferedFor();
        User buyer = buyOrder.get().getOfferedBy();
        Nft nft = sellOrder.getNft();
        User seller = nft.getOwner();
        Image image = imageService.getImage(nft.getIdImage()).orElseThrow(ImageNotFoundException::new);

        Locale locale = Locale.forLanguageTag(buyer.getLocale());
        mailingService.sendOfferAcceptedMail(buyer.getEmail(), seller.getEmail(), seller.getId(), buyer.getUsername(), nft.getNftName(), nft.getNftId(), nft.getContractAddr(), buyOrder.get().getAmount(), image.getImage(), locale, nft.getId());
    }

    private void rejectBuyOrder(Optional<BuyOrder> buyOrder) {
        if(!buyOrder.isPresent() || buyOrder.get().getStatus().equals(StatusBuyOrder.NEW) || buyOrder.get().getPendingDate() == null)
            return;

        if(Instant.now().minusMillis(buyOrder.get().getPendingDate().getTime()).getEpochSecond() < SECONDS_IN_ONE_DAY)
            return;

        buyOrderDao.rejectBuyOrder(buyOrder.get().getOfferedFor().getId(), buyOrder.get().getOfferedBy().getId());
        Nft nft = buyOrder.get().getOfferedFor().getNft();
        User seller = buyOrder.get().getOfferedFor().getNft().getOwner();
        purchaseService.createPurchase(buyOrder.get().getOfferedBy().getId(), seller.getId(), nft.getId(), buyOrder.get().getAmount(), null, StatusPurchase.CANCELLED);
    }

    @Transactional
    @Override
    public void rejectBuyOrder(int sellOrderId, int buyerId) {
        rejectBuyOrder(buyOrderDao.getBuyOrder(sellOrderId, buyerId));
    }

    @Transactional
    @Override
    public void deleteBuyOrder(int sellOrderId, int buyerId) {
        Optional<User> currentUser = userService.getCurrentUser();
        if(!currentUser.isPresent())
            throw new UserNotLoggedInException();

        if (!sellOrderService.currentUserOwnsSellOrder(sellOrderId) && buyerId != currentUser.get().getId())
            throw new UserIsNotNftOwnerException();
        Optional<BuyOrder> buyOrder = buyOrderDao.getBuyOrder(sellOrderId, buyerId);
        if(!buyOrder.isPresent())
            return;
        if(buyOrder.get().getStatus().equals(StatusBuyOrder.PENDING))
            throw new BuyOrderIsPendingException();
        SellOrder sellOrder = buyOrder.get().getOfferedFor();
        User buyer = buyOrder.get().getOfferedBy();
        Nft nft = sellOrder.getNft();
        User seller = nft.getOwner();
        Image image = imageService.getImage(nft.getIdImage()).orElseThrow(ImageNotFoundException::new);
        BigDecimal offerAmount = buyOrder.get().getAmount();
        buyOrderDao.deleteBuyOrder(sellOrderId, buyerId);
        Locale locale = Locale.forLanguageTag(buyer.getLocale());
        if (sellOrderService.currentUserOwnsSellOrder(sellOrderId)) {
            mailingService.sendOfferRejectedMail(buyer.getEmail(), seller.getEmail(), buyer.getUsername(), nft.getNftName(), nft.getNftId(), nft.getContractAddr(), offerAmount, image.getImage(), locale, nft.getId());
        }
    }

    @Override
    public boolean sellOrderPendingBuyOrder(int sellOrderId) {
        return buyOrderDao.sellOrderPendingBuyOrder(sellOrderId);
    }

    @Override
    public Optional<BuyOrder> getPendingBuyOrder(int sellOrderId) {
        return buyOrderDao.getPendingBuyOrder(sellOrderId);
    }

    @Transactional
    @Override
    public Optional<Integer> validateTransaction(String txHash, int sellOrderId, int buyerId) {
        if(!sellOrderService.getOrderById(sellOrderId).isPresent())
            throw new SellOrderNotFoundException();
        if(purchaseService.isTxHashAlreadyInUse(txHash))
            return Optional.empty();

        Optional<BuyOrder> buyOrder = getPendingBuyOrder(sellOrderId);
        if(!buyOrder.isPresent() || !buyOrder.get().getStatus().equals(StatusBuyOrder.PENDING))
            return Optional.empty();
        User seller = buyOrder.get().getOfferedFor().getNft().getOwner();
        User buyer = userService.getUserById(buyerId).orElseThrow(UserNotFoundException::new);
        boolean isValid = etherscanService.isTransactionValid(txHash, buyer.getWallet(), seller.getWallet(), buyOrder.get().getAmount());
        if(!isValid)
            return Optional.empty();

        return confirmBuyOrder(sellOrderId, buyerId, txHash);
    }

    public int getPageSize() {
        return pageSize;
    }


    @Override
    public boolean nftHasValidTransaction(int sellOrderId, int idBuyer, BigDecimal price, String txHash) {
        Optional<SellOrder> sellOrder = sellOrderService.getOrderById(sellOrderId);
        Optional<User> buyer = userService.getUserById(idBuyer);
        if(!sellOrder.isPresent() || !buyer.isPresent())
            return false;
        return etherscanService.isTransactionValid(txHash,buyer.get().getWallet(),sellOrder.get().getNft().getOwner().getWallet(),price);
    }

    @Override
    public List<String> getBuyOrderStatusNames() {
        List<String> statusNames = StatusBuyOrder.getStatusNames();
        statusNames.add("MYSALES");
        return statusNames;
    }

    @Override
    public boolean hasValidFilterName(String status) {
        return StatusBuyOrder.hasStatus(status) || status.equals("MYSALES");
    }

    @Override
    public Optional<BuyOrder> getBuyOrder(int sellOrderId, int buyerId) {
        return buyOrderDao.getBuyOrder(sellOrderId, buyerId);
    }
}
