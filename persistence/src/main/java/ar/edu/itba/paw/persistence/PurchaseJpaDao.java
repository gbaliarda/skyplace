package ar.edu.itba.paw.persistence;

import ar.edu.itba.paw.model.Nft;
import ar.edu.itba.paw.model.Purchase;
import ar.edu.itba.paw.model.StatusPurchase;
import ar.edu.itba.paw.model.User;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Repository
public class PurchaseJpaDao implements PurchaseDao {

    @PersistenceContext
    private EntityManager em;

    @Override
    public List<Purchase> getUserSales(int userId) {
        final TypedQuery<Purchase> query = em.createQuery("FROM Purchase p WHERE p.buyer.id = :userId", Purchase.class);
        query.setParameter("userId", userId);
        return query.getResultList();
    }

    @Override
    public List<Purchase> getUserPurchases(int userId) {
        final TypedQuery<Purchase> query = em.createQuery("FROM Purchase p WHERE p.seller.id = :userId", Purchase.class);
        query.setParameter("userId", userId);
        return query.getResultList();
    }

    @Override
    public List<Purchase> getTransactionsBetweenUsers(int user1Id, int user2Id, int page, int pageSize) {
        final Query idQuery = em.createNativeQuery("SELECT id FROM purchases WHERE id_buyer = :user1 AND id_seller = :user2 OR id_buyer = :user2 AND id_seller = :user1 ORDER BY buy_date DESC LIMIT :pageSize OFFSET :offset");
        idQuery.setParameter("user1", user1Id);
        idQuery.setParameter("user2", user2Id);
        idQuery.setParameter("pageSize", pageSize);
        idQuery.setParameter("offset", pageSize * (page - 1));
        @SuppressWarnings("unchecked")
        List<Integer> ids = (List<Integer>) idQuery.getResultList();

        if(ids.size() == 0)
            return new ArrayList<>();

        final TypedQuery<Purchase> query = em.createQuery("FROM Purchase WHERE id in :ids ORDER BY buyDate DESC", Purchase.class);
        query.setParameter("ids", ids);
        return query.getResultList();
    }

    @Override
    public long getTransactionPagesBetweenUsers(int user1Id, int user2Id, int pageSize) {
        final Query query = em.createNativeQuery("SELECT count(*) FROM purchases WHERE id_buyer = :user1 AND id_seller = :user2 OR id_buyer = :user2 AND id_seller = :user1");
        query.setParameter("user1", user1Id);
        query.setParameter("user2", user2Id);
        long transactionsAmount = ((BigInteger)query.getSingleResult()).intValue();
        return (transactionsAmount + pageSize - 1) / pageSize;
    }

    @Override
    public List<Purchase> getAllTransactions(int userId, int page, int pageSize) {
        final Query idQuery = em.createNativeQuery("SELECT id FROM purchases WHERE id_buyer = :userId OR id_seller = :userId ORDER BY buy_date DESC LIMIT :pageSize OFFSET :offset");
        idQuery.setParameter("userId", userId);
        idQuery.setParameter("pageSize", pageSize);
        idQuery.setParameter("offset", pageSize * (page - 1));
        @SuppressWarnings("unchecked")
        List<Integer> ids = (List<Integer>) idQuery.getResultList();

        if(ids.size() == 0)
            return new ArrayList<>();

        final TypedQuery<Purchase> query = em.createQuery("from Purchase WHERE id IN :ids ORDER BY buyDate DESC", Purchase.class);
        query.setParameter("ids", ids);
        return query.getResultList();
    }

    @Override
    public Purchase createPurchase(User buyer, User seller, Nft nft, BigDecimal price, String txHash, StatusPurchase statusPurchase) {
        Timestamp currentTime = new Timestamp(Instant.now().toEpochMilli());
        currentTime.setNanos(0);
        final Purchase newPurchase = new Purchase(price, currentTime, nft, buyer, seller, statusPurchase, txHash);
        em.persist(newPurchase);
        return newPurchase;
    }

    @Override
    public boolean isTxHashAlreadyInUse(String txHash) {
        final TypedQuery<Purchase> query = em.createQuery("FROM Purchase AS p WHERE p.status = :statusPurchase AND p.txHash = :txHash",Purchase.class);
        query.setParameter("statusPurchase", StatusPurchase.SUCCESS);
        query.setParameter("txHash", txHash);
        return query.getResultList().stream().findFirst().isPresent();
    }

    @Override
    public int getTransactionAmount(int userId) {
        final Query query = em.createNativeQuery("SELECT count(id) FROM purchases WHERE id_buyer=:userId OR id_seller=:userId");
        query.setParameter("userId",userId);
        return ((BigInteger)query.getSingleResult()).intValue();
    }

    @Override
    public Optional<Purchase> getPurchaseById(int userId, int purchaseId) {
        final TypedQuery<Purchase> query = em.createQuery("FROM Purchase AS p WHERE p.id = :purchaseId AND (p.buyer.id = :userId OR p.seller.id = :userId)",Purchase.class);
        query.setParameter("userId", userId);
        query.setParameter("purchaseId", purchaseId);
        return query.getResultList().stream().findFirst();
    }

}
