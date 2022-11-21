package ar.edu.itba.paw.persistence;

import ar.edu.itba.paw.model.Chain;
import ar.edu.itba.paw.model.Role;
import ar.edu.itba.paw.model.User;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import java.math.BigInteger;
import java.util.Optional;

@Repository
public class UserJpaDao implements UserDao {

    @PersistenceContext
    private EntityManager em;

    @Override
    public User create(String email, String username, String wallet, Chain walletChain, String password, String locale) {
        final User user = new User(username, wallet, email, password, walletChain, Role.User, locale);
        em.persist(user);
        return user;
    }

    @Override
    public Optional<User> getUserByEmail(String email) {
        if(email == null)
            return Optional.empty();
        final TypedQuery<User> query = em.createQuery("from User as u where lower(u.email) = lower(:email)", User.class);
        query.setParameter("email", email);
        return query.getResultList().stream().findFirst();
    }

    @Override
    public Optional<User> getUserByUsername(String username) {
        if(username == null)
            return Optional.empty();
        final TypedQuery<User> query = em.createQuery("from User as u where lower(u.username) = lower(:username)", User.class);
        query.setParameter("username", username);
        return query.getResultList().stream().findFirst();
    }

    @Override
    public Optional<User> getUserById(int id) {
        return Optional.ofNullable(em.find(User.class, id));
    }

    @Override
    public boolean userOwnsNft(int productId, User user) {
        final Query query = em.createNativeQuery("SELECT count(*) FROM users INNER JOIN nfts ON users.id=nfts.id_owner WHERE users.id=:userId AND nfts.id=:productId");
        query.setParameter("userId", user.getId());
        query.setParameter("productId", productId);
        return ((BigInteger)query.getSingleResult()).intValue() > 0;
    }
}
