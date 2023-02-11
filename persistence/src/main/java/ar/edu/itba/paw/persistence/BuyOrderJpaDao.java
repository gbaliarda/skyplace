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

    /**
     * Updates buy order price or creates a new one depending on existance.
     * @return New or updated Buy Order.
     */
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

    /**
     * Accepts a buy order and sets its status to pending.
     */
    @Override
    public void acceptBuyOrder(int sellOrderId, int buyerId) {
        if(getPendingBuyOrder(sellOrderId).isPresent())
            return;
        changeBuyOrderStatus(sellOrderId, buyerId, StatusBuyOrder.PENDING);
    }

    /**
     * Stops a pending buy order setting its status to new.
     */
    @Override
    public void rejectBuyOrder(int sellOrderId, int buyerId) {
        changeBuyOrderStatus(sellOrderId, buyerId, StatusBuyOrder.NEW);
    }

    /**
     * @return The amount of buy orders for a specific sell order.
     */
    @Override
    public int getAmountBuyOrders(SellOrder sellOrder, String status) {
        final Query query = em.createNativeQuery("SELECT count(*) FROM buyorders WHERE id_sellorder = :sellOrderId AND status = :status");
        query.setParameter("sellOrderId", sellOrder.getId());
        query.setParameter("status", status);
        return ((BigInteger)query.getSingleResult()).intValue();
    }

    /**
     * @return The amount of buy orders that a certain user has despite the status.
     */
    @Override
    public int getAmountBuyOrdersForUser(User user, String status) {
        StringBuilder queryString = new StringBuilder("SELECT count(*) FROM buyorders WHERE id_buyer = :buyerId");
        boolean addStatusFilter = StatusBuyOrder.hasStatus(status);
        if (addStatusFilter)
            queryString.append(" AND status = :status");
        final Query query = em.createNativeQuery(queryString.toString());
        query.setParameter("buyerId",user.getId());
        if (addStatusFilter)
            query.setParameter("status", status);
        return ((BigInteger)query.getSingleResult()).intValue();
    }

    /**
     * Deletes a certain buy order.
     */
    @Override
    public void deleteBuyOrder(int sellOrderId, int buyerId) {
        final Query query = em.createQuery("DELETE FROM BuyOrder b WHERE b.offeredFor.id = :sellOrderId AND b.offeredBy.id = :buyerId ");
        query.setParameter("sellOrderId", sellOrderId);
        query.setParameter("buyerId",buyerId);
        query.executeUpdate();
    }

    /**
     * @return true or false depending on if the specific sell order contains or not a pending buy order.
     */
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

    /**
     * Retrieves a list of buy orders for a certain user, with or without filters.
     * @param page number of the page to retrieve.
     * @param status Status name to filter, if no valid status received, no filter applied.
     * @param pageSize number of buy orders that can contain a single page
     * @return A list of pageSize or less elements with all the different buy orders in a certain page for a certain user.
     */
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

    /**
     * Retrieves a list of pending buy orders for a user.
     * @param page number of the page to retrieve.
     * @param pageSize number of buy orders that can contain a single page
     * @return A list of pageSize or less elements with all the different pending buy orders in a certain page for a certain user.
     */
    @Override
    public List<BuyOrder> getPendingBuyOrdersToUser(User user, int page, int pageSize) {
        final Query idQuery = em.createNativeQuery("SELECT id_sellorder, id_buyer FROM  nfts INNER JOIN users ON nfts.id_owner=users.id INNER JOIN sellorders ON sellorders.id_nft=nfts.id INNER JOIN buyorders ON sellorders.id=buyorders.id_sellorder WHERE users.id=:userId AND status=:pendingStatus");
        idQuery.setParameter("userId",user.getId());
        idQuery.setParameter("pendingStatus", StatusBuyOrder.PENDING.name());

        @SuppressWarnings("unchecked")
        List<Object[]> res = (List<Object[]>) idQuery.getResultList();
        //List<BuyOrderId> buyOrderIds2 = res.stream().map(o -> new BuyOrderId(((BigInteger)o[0]).intValue(),((BigInteger)o[1]).intValue())).collect(Collectors.toList());
        List<BuyOrderId> buyOrderIds = new ArrayList<>();

        for(Object[] r:res)
            buyOrderIds.add(new BuyOrderId((int)r[0],(int)r[1]));

        if(buyOrderIds.size() == 0)
            return Collections.emptyList();
        final TypedQuery<BuyOrder> query = em.createQuery("FROM BuyOrder b WHERE b.buyOrderId IN :buyOrderIds",BuyOrder.class);
        query.setParameter("buyOrderIds",buyOrderIds);
        return query.getResultList();
    }

    /**
     * Retrieve a list of buy orders for a certain sell order.
     * @param page number of the page to retrieve.
     * @param pageSize number of buy orders that can contain a single page
     * @return A list of pageSize or less elements with all the different buy orders in a certain page for a certain sell order.
     */
    @Override
    public List<BuyOrder> getOrdersBySellOrderId(int page, int sellOrderId, String status, int pageSize) {
        boolean isValidStatus = StatusBuyOrder.hasStatus(status);
        StringBuilder baseQuery = new StringBuilder("SELECT id_buyer FROM buyorders WHERE id_sellorder = :sellOrderId");
        if(isValidStatus) {
            baseQuery.append(" AND status = :status");
        }
        final Query idQuery = em.createNativeQuery(baseQuery.append(" LIMIT :pageSize OFFSET :pageOffset").toString());
        idQuery.setParameter("sellOrderId", sellOrderId);
        idQuery.setParameter("pageSize", pageSize);
        idQuery.setParameter("pageOffset", (page-1) * pageSize);

        if(isValidStatus) {
            idQuery.setParameter("status", status);
        }

        @SuppressWarnings("unchecked")
        final List<Integer> ids = (List<Integer>) idQuery.getResultList();
        if(ids.size() == 0)
            return Collections.emptyList();

        final TypedQuery<BuyOrder> query = em.createQuery("FROM BuyOrder AS b WHERE b.offeredFor.id = :sellOrderId AND b.offeredBy.id IN :ids", BuyOrder.class);
        query.setParameter("ids", ids);
        query.setParameter("sellOrderId", sellOrderId);
        return query.getResultList();
    }

    /**
     * Retrieves a list of all pending buy orders that expired, that is to say, the buy orders that exceeded the time limit to confirm a buy offer.
     * @return A list of expired pending buy orders.
     */
    @Override
    public List<BuyOrder> getExpiredPendingOffersByUser(User user) {
        final TypedQuery<BuyOrder> query = em.createQuery("FROM BuyOrder AS b WHERE b.offeredBy.id = :buyerId AND EXTRACT(EPOCH FROM CURRENT_TIMESTAMP-b.pendingDate) >= 86400000 ",BuyOrder.class);
        query.setParameter("buyerId", user.getId());
        return query.getResultList();
    }

}
