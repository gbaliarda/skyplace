package ar.edu.itba.paw.persistence;

import ar.edu.itba.paw.model.*;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;

@Repository
public class BuyOrderJpaDao implements BuyOrderDao {

    @PersistenceContext
    private EntityManager em;

    private Optional<BuyOrder> getBuyOrderForUserInSellOrder(int sellOrderId, int buyerId) {
        final TypedQuery<BuyOrder> buyOrder = em.createQuery("FROM BuyOrder b WHERE b.buyOrderId.sellOrderId = :sellOrderId AND b.buyOrderId.buyerId = :buyerId ",BuyOrder.class);
        buyOrder.setParameter("sellOrderId", sellOrderId);
        buyOrder.setParameter("buyerId", buyerId);
        return buyOrder.getResultList().stream().findFirst();
    }

    @Override
    public BuyOrder create(SellOrder sellOrder, BigDecimal price, User bidder) {
        Optional<BuyOrder> existBuyOrder = getBuyOrderForUserInSellOrder(sellOrder.getId(), bidder.getId());
        if(existBuyOrder.isPresent()) {
            existBuyOrder.get().setAmount(price);
            return existBuyOrder.get();
        }
        final BuyOrder buyOrder = new BuyOrder(price, sellOrder, bidder, StatusBuyOrder.NEW, null);
        em.persist(buyOrder);
        return buyOrder;
    }

    private void changeBuyOrderStatus(int sellOrderId, int buyerId, StatusBuyOrder status) {
        final TypedQuery<BuyOrder> query = em.createQuery("FROM BuyOrder b WHERE b.offeredFor.id = :sellOrderId AND b.offeredBy.id = :buyerId", BuyOrder.class);
        query.setParameter("sellOrderId", sellOrderId);
        query.setParameter("buyerId", buyerId);
        Optional<BuyOrder> buyOrder = query.getResultList().stream().findFirst();
        if(!buyOrder.isPresent())
            return;
        buyOrder.get().setStatus(status);
        if(status.equals(StatusBuyOrder.PENDING))
            buyOrder.get().setPendingDate(Date.from(Instant.now()));
        else
            buyOrder.get().setPendingDate(null);
        em.merge(buyOrder.get());
    }

    @Override
    public void acceptBuyOrder(int sellOrderId, int buyerId) {
        if(getPendingBuyOrder(sellOrderId).isPresent())
            return;
        changeBuyOrderStatus(sellOrderId, buyerId, StatusBuyOrder.PENDING);
    }

    @Override
    public void rejectBuyOrder(int sellOrderId, int buyerId) {
        stopPendingBuyOrder(sellOrderId, buyerId);
    }

    @Override
    public void stopPendingBuyOrder(int sellOrderId, int buyerId) {
        changeBuyOrderStatus(sellOrderId, buyerId, StatusBuyOrder.NEW);
    }

    @Override
    public int getAmountBuyOrdersForUser(User user) {
        final Query query = em.createNativeQuery("SELECT count(*) FROM buyorders WHERE id_buyer = :buyerId");
        query.setParameter("buyerId",user.getId());
        return ((BigInteger)query.getSingleResult()).intValue();
    }

    @Override
    public void deleteBuyOrder(int sellOrderId, int buyerId) {
        final Query query = em.createQuery("DELETE FROM BuyOrder b WHERE b.offeredFor.id = :sellOrderId AND b.offeredBy.id = :buyerId ");
        query.setParameter("sellOrderId", sellOrderId);
        query.setParameter("buyerId",buyerId);
        query.executeUpdate();
    }

    @Override
    public boolean sellOrderPendingBuyOrder(int sellOrderId) {
        final Query query = em.createNativeQuery("SELECT count(*) > 0 AS v FROM buyorders WHERE id_sellorder = :sellOrderId AND status LIKE :statusPending");
        query.setParameter("sellOrderId", sellOrderId);
        query.setParameter("statusPending", StatusBuyOrder.PENDING.name());

        @SuppressWarnings("unchecked")
        Optional<Boolean> res = (Optional<Boolean>) query.getResultList().stream().findFirst();

        return res.orElse(false);
    }

    @Override
    public Optional<BuyOrder> getPendingBuyOrder(int sellOrderId) {
        final TypedQuery<BuyOrder> query = em.createQuery("FROM BuyOrder AS b WHERE b.offeredFor.id = :sellOrderId AND b.status = :statusPending", BuyOrder.class);
        query.setParameter("sellOrderId", sellOrderId);
        query.setParameter("statusPending", StatusBuyOrder.PENDING);
        return query.getResultList().stream().findFirst();
    }

    @Override
    public Optional<BuyOrder> getBuyOrder(int sellOrderId, int buyerId) {
        final TypedQuery<BuyOrder> query = em.createQuery("FROM BuyOrder AS b WHERE b.offeredFor.id = :sellOrderId AND b.offeredBy.id = :buyerId", BuyOrder.class);
        query.setParameter("sellOrderId", sellOrderId);
        query.setParameter("buyerId", buyerId);
        return query.getResultList().stream().findFirst();
    }

    @Override
    public List<BuyOrder> getBuyOrdersForUser(User user, int page, String status, int pageSize) {
        final StringBuilder nativeQueryText = new StringBuilder("SELECT id_sellorder FROM buyorders WHERE id_buyer = :buyerId");
        boolean hasStatusOnQuery = false;
        if(StatusBuyOrder.hasStatus(status)){
            nativeQueryText.append(" AND status = :status");
            hasStatusOnQuery = true;
        }
        nativeQueryText.append(" LIMIT :pageSize OFFSET :pageOffset");
        final Query idQuery = em.createNativeQuery(nativeQueryText.toString());
        idQuery.setParameter("buyerId", user.getId());
        idQuery.setParameter("pageSize", pageSize);
        idQuery.setParameter("pageOffset", (page-1) * pageSize);
        if(hasStatusOnQuery)
            idQuery.setParameter("status", status);
        @SuppressWarnings("unchecked")
        final List<Integer> ids = (List<Integer>) idQuery.getResultList().stream().collect(Collectors.toList());

        if(ids.size() == 0)
            return Collections.emptyList();

        final TypedQuery<BuyOrder> query = em.createQuery("FROM BuyOrder AS b WHERE b.offeredFor.id IN :ids AND b.offeredBy.id = :buyerId", BuyOrder.class);
        query.setParameter("ids", ids);
        query.setParameter("buyerId", user.getId());
        return query.getResultList();
    }

    @Override
    public List<BuyOrder> getOrdersBySellOrderId(int page, int sellOrderId, int pageSize) {
        final Query idQuery = em.createNativeQuery("SELECT id_buyer FROM buyorders WHERE id_sellorder = :sellOrderId LIMIT :pageSize OFFSET :pageOffset");
        idQuery.setParameter("sellOrderId", sellOrderId);
        idQuery.setParameter("pageSize", pageSize);
        idQuery.setParameter("pageOffset", (page-1) * pageSize);

        @SuppressWarnings("unchecked")
        final List<Integer> ids = (List<Integer>) idQuery.getResultList().stream().collect(Collectors.toList());
        if(ids.size() == 0)
            return Collections.emptyList();

        final TypedQuery<BuyOrder> query = em.createQuery("FROM BuyOrder AS b WHERE b.offeredFor.id = :sellOrderId AND b.offeredBy.id IN :ids", BuyOrder.class);
        query.setParameter("ids", ids);
        query.setParameter("sellOrderId", sellOrderId);
        return query.getResultList();
    }

    @Override
    public List<BuyOrder> getExpiredPendingOffersByUser(User user) {
        final TypedQuery<BuyOrder> query = em.createQuery("FROM BuyOrder AS b WHERE b.offeredBy.id = :buyerId AND EXTRACT(EPOCH FROM CURRENT_TIMESTAMP-b.pendingDate) >= 86400000 ",BuyOrder.class);
        query.setParameter("buyerId", user.getId());
        return query.getResultList();
    }

}
