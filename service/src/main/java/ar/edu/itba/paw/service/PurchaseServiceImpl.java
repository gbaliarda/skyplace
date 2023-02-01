package ar.edu.itba.paw.service;

import ar.edu.itba.paw.model.Nft;
import ar.edu.itba.paw.model.Purchase;
import ar.edu.itba.paw.model.StatusPurchase;
import ar.edu.itba.paw.model.User;
import ar.edu.itba.paw.persistence.PurchaseDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Service
public class PurchaseServiceImpl implements PurchaseService {

    private final PurchaseDao purchaseDao;
    private final UserService userService;
    private final NftService nftService;
    private final int pageSize = 5;

    @Autowired
    public PurchaseServiceImpl(PurchaseDao purchaseDao, UserService userService, NftService nftService) {
        this.purchaseDao = purchaseDao;
        this.userService = userService;
        this.nftService = nftService;
    }

    @Override
    public int getPageSize() {
        return pageSize;
    }

    @Override
    public List<Purchase> getUserSales(int userId) {
        return purchaseDao.getUserSales(userId);
    }

    @Override
    public List<Purchase> getUserPurchases(int userId) {
        return purchaseDao.getUserPurchases(userId);
    }

    @Override
    public int getAmountPagesByUserId(int userId) {
        return (purchaseDao.getTransactionAmount(userId) - 1) / getPageSize() + 1;
    }

    @Override
    public List<Purchase> getAllTransactions(int userId, int page) {
        User currentUser = userService.getCurrentUser().orElse(null);
        if (currentUser == null || currentUser.getId() != userId)
            return Collections.emptyList();
        return purchaseDao.getAllTransactions(userId, page, pageSize);
    }

    @Override
    public List<Purchase> getTransactionsBetweenUsers(int user1Id, int user2Id, int page) {
        return purchaseDao.getTransactionsBetweenUsers(user1Id, user2Id, page, pageSize);
    }

    @Override
    public long getTransactionPagesBetweenUsers(int user1Id, int user2Id) {
        return purchaseDao.getTransactionPagesBetweenUsers(user1Id, user2Id, pageSize);
    }

    @Override
    public int createPurchase(int idBuyer, int idSeller, int idNft, BigDecimal price, String txHash, StatusPurchase status) {
        Optional<User> maybeBuyer = userService.getUserById(idBuyer);
        Optional<User> maybeSeller = userService.getUserById(idSeller);
        Optional<Nft> maybeNft = nftService.getNFTById(idNft);
        if (!maybeBuyer.isPresent() || !maybeNft.isPresent() || !maybeSeller.isPresent())
            return 0;
        return purchaseDao.createPurchase(maybeBuyer.get(), maybeSeller.get(), maybeNft.get(), price, txHash, status).getId();
    }

    @Override
    public boolean isTxHashAlreadyInUse(String txHash) {
        return purchaseDao.isTxHashAlreadyInUse(txHash);
    }

    @Override
    public Optional<Purchase> getPurchaseById(int userId, int purchaseId) {
        return purchaseDao.getPurchaseById(userId, purchaseId);
    }
}
