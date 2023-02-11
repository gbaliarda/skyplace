package ar.edu.itba.paw.service;

import ar.edu.itba.paw.model.BuyOrder;
import ar.edu.itba.paw.model.SellOrder;
import ar.edu.itba.paw.model.User;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

public interface BuyOrderService {

    boolean create(int sellOrderId, BigDecimal price, int userId);

    List<BuyOrder> getOrdersBySellOrderId(int offerPage, int sellOrderId, String status);

    int getAmountPagesBySellOrderId(SellOrder sellOrder, String status);

    List<BuyOrder> getBuyOrdersForUser(User user, int page, String status);

    int getAmountPagesForUser(User user, String status);

    void acceptBuyOrder(int sellOrderId, int buyerId);

    void rejectBuyOrder(int sellOrderId, int buyerId);

    void deleteBuyOrder(int sellOrderId, int buyerId);

    boolean nftHasValidTransaction(int sellOrderId, int idBuyer, BigDecimal price, String txHash);

    boolean sellOrderPendingBuyOrder(int sellOrderId);

    Optional<BuyOrder> getPendingBuyOrder(int sellOrderId);

    Optional<Integer> validateTransaction(String txHash, int sellOrderId, int buyerId);

    List<String> getBuyOrderStatusNames();

    boolean hasValidFilterName(String status);

    Optional<BuyOrder> getBuyOrder(int sellOrderId, int buyerId);
}
