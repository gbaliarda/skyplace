package ar.edu.itba.paw.service;

import ar.edu.itba.paw.model.Purchase;
import ar.edu.itba.paw.model.StatusPurchase;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

public interface PurchaseService {

    int getPageSize();

    List<Purchase> getUserSales(int userId);

    List<Purchase> getUserPurchases(int userId);

    List<Purchase> getAllTransactions(int userId, int page);

    int getAmountPagesByUserId(int userId);

    int createPurchase(int idBuyer, int idSeller, int idNft, BigDecimal price, String txHash, StatusPurchase statusPurchase);

    boolean isTxHashAlreadyInUse(String txHash);

    Optional<Purchase> getPurchaseById(int userId, int purchaseId);
}
