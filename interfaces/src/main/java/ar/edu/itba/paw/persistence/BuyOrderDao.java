package ar.edu.itba.paw.persistence;

import ar.edu.itba.paw.model.BuyOrder;
import ar.edu.itba.paw.model.SellOrder;
import ar.edu.itba.paw.model.User;

import javax.swing.text.html.Option;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

public interface BuyOrderDao {

    BuyOrder create(SellOrder sellOrder, BigDecimal price, User bidder);

    void acceptBuyOrder(int sellOrderId, int buyerId);

    void rejectBuyOrder(int sellOrderId, int buyerId);

    boolean sellOrderPendingBuyOrder(int sellOrderId);

    Optional<BuyOrder> getPendingBuyOrder(int sellOrderId);

    Optional<BuyOrder> getBuyOrder(int sellOrderId, int buyerId);

    List<BuyOrder> getBuyOrdersForUser(User user, int page, String status, int pageSize);

    List<BuyOrder> getPendingBuyOrdersToUser(User user, int page, int pageSize);

    List<BuyOrder> getAllBuyOrdersForUser(User user, int page, int pageSize);

    List<BuyOrder> getOrdersBySellOrderId(int page, int sellOrderId, String status, int pageSize);

    List<BuyOrder> getExpiredPendingOffersByUser(User user);

    int getAmountBuyOrders(SellOrder sellOrder, String status);

    int getAmountBuyOrdersForUser(User user, String status);

    void deleteBuyOrder(int sellOrderId, int buyerId);
}
