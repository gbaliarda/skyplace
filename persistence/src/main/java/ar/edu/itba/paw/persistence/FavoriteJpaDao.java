package ar.edu.itba.paw.persistence;

import ar.edu.itba.paw.model.Favorited;
import ar.edu.itba.paw.model.FavoritedId;
import ar.edu.itba.paw.model.Nft;
import ar.edu.itba.paw.model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import java.math.BigInteger;
import java.util.Optional;

@Repository
public class FavoriteJpaDao implements FavoriteDao{

    private static final Logger LOGGER = LoggerFactory.getLogger(FavoriteJpaDao.class);

    @PersistenceContext
    private EntityManager em;


    /**
     * Adds a new favorite from a user for a certain product
     */
    @Override
    public void addNftFavorite(Nft nft, User user) {
        Favorited newFavorited = new Favorited(user, nft);
        em.persist(newFavorited);
    }

    /**
     * Removes an existing favorite from a user for a certain product
     */
    @Override
    public void removeNftFavorite(Nft nft, User user) {
        Favorited favoriteToRemove = em.find(Favorited.class, new FavoritedId(user.getId(), nft.getId()));
        em.remove(favoriteToRemove);
    }

    /**
     * @return Optional of the entity that contains who faved and for what product.
     */
    @Override
    public Optional<Favorited> userFavedNft(int userId, int idNft) {
        final TypedQuery<Favorited> query = em.createQuery("FROM Favorited f WHERE f.user.id = :userId AND f.nft.id = :nftId", Favorited.class);
        query.setParameter("userId", userId);
        query.setParameter("nftId", idNft);
        return query.getResultList().stream().findFirst();
    }

    /**
     * Retrieves the amount of users that faved a certain product
     * @return Number of favorites for a product.
     */
    @Override
    public int getNftFavorites(int productId) {
        final Query query = em.createNativeQuery("SELECT count(*) FROM Favorited WHERE id_nft = :id_nft");
        query.setParameter("id_nft", productId);
        return ((BigInteger)query.getSingleResult()).intValue();
    }

    /**
     * Retrieves the amount of favorites a user has
     * @return User favorites amount.
     */
    @Override
    public int getUserFavoritesAmount(int userId) {
        final Query query = em.createNativeQuery("SELECT count(*) FROM Favorited where user_id = :user_id");
        query.setParameter("user_id", userId);
        return ((BigInteger)query.getSingleResult()).intValue();
    }
}
