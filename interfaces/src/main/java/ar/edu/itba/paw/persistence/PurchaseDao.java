package ar.edu.itba.paw.persistence;

import ar.edu.itba.paw.model.Nft;
import ar.edu.itba.paw.model.Purchase;
import ar.edu.itba.paw.model.StatusPurchase;
import ar.edu.itba.paw.model.User;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

public interface PurchaseDao {

    List<Purchase> getUserSales(int userId);

    List<Purchase> getUserPurchases(int userId);

    List<Purchase> getAllTransactions(int userId, int page, int pageSize);

    Purchase createPurchase(User buyer, User seller, Nft nft, BigDecimal price, String txHash, StatusPurchase statusPurchase);

    boolean isTxHashAlreadyInUse(String txHash);

    int getTransactionAmount(int userId);

    Optional<Purchase> getPurchaseById(int userId, int purchaseId);

    List<Purchase> getTransactionsBetweenUsers(int user1Id, int user2Id);

}
