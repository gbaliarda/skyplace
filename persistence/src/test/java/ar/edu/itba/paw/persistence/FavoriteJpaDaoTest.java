package ar.edu.itba.paw.persistence;

import ar.edu.itba.paw.model.*;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.jdbc.JdbcTestUtils;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.sql.DataSource;

import java.util.Optional;

import static org.junit.Assert.*;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = TestConfig.class)
@Transactional
public class FavoriteJpaDaoTest {
    @Autowired
    private
    FavoriteJpaDao favoriteJpaDao;

    @PersistenceContext
    private EntityManager em;

    @Autowired
    private DataSource ds;

    private JdbcTemplate jdbcTemplate;

    private static final String FAVORITE_TABLE = "favorited";

    private User owner;
    private Image img;
    private Nft nft;

    @Before
    public void setUp() {
        jdbcTemplate = new JdbcTemplate(ds);
        img = new Image(new byte[1]);
        em.persist(img);

        owner = new User("username", "wallet", "email@email.com", "password", Chain.Ethereum, Role.User, "en");
        em.persist(owner);

        nft = new Nft(123, "addr", "name", Chain.Ethereum, img.getIdImage(), "collection", "description", owner);
        em.persist(nft);
    }

    @Test
    public void testAddNftFavorite() {
        favoriteJpaDao.addNftFavorite(nft, owner);

        em.flush();
        assertEquals(1, JdbcTestUtils.countRowsInTable(jdbcTemplate, FAVORITE_TABLE));
    }

    @Test
    public void testRemoveNftFavorite() {
        Favorited favorited = new Favorited(owner, nft);
        em.persist(favorited);

        favoriteJpaDao.removeNftFavorite(nft, owner);

        em.flush();
        assertEquals(0, JdbcTestUtils.countRowsInTable(jdbcTemplate, FAVORITE_TABLE));
    }


    @Test
    public void testUserFavedNft() {
        Favorited favorited = new Favorited(owner, nft);
        em.persist(favorited);

        Optional<Favorited> maybeFavorited = favoriteJpaDao.userFavedNft(owner.getId(), nft.getId());

        assertTrue(maybeFavorited.isPresent());
    }

    @Test
    public void testGetNftFavorites() {
        Favorited favorited = new Favorited(owner, nft);
        em.persist(favorited);
        User user2 = new User("username2", "wallet", "email2@email.com", "password", Chain.Ethereum, Role.User, "en");
        em.persist(user2);
        Favorited favorited2 = new Favorited(user2, nft);
        em.persist(favorited2);
        int amountFavorites = favoriteJpaDao.getNftFavorites(nft.getId());

        assertEquals(2, amountFavorites);
    }
}