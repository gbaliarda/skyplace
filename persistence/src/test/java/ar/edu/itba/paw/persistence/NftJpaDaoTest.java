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

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.Assert.*;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = TestConfig.class)
@Transactional
public class NftJpaDaoTest {

    private final static int NFT_ID = 1547;
    private final static String NFT_CONTRACT_ADDRESS = "0x29714cafe792ef8b8c649451d13c89e21a0d7f5b";
    private final static String NFT_NAME = "CryptoApe";
    private final static Chain CHAIN = Chain.Ethereum;
    private final static String COLLECTION = "Crypto Apes Official";
    private final static String DESCRIPTION = "CryptoApes strive to unite the BAYC and CryptoPunk communities, with randomly generated and unique artwork. We are not affiliated with Yuga Labs or Larva Labs.";
    private final static String NFT_TABLE = "nfts";
    private final static int PAGE_SIZE = 12;

    private JdbcTemplate jdbcTemplate;

    @Autowired
    private NftJpaDao nftJpaDao;

    @PersistenceContext
    private EntityManager em;

    @Autowired
    private DataSource ds;

    private User owner;
    private Image img;

    @Before
    public void setUp() {
        jdbcTemplate = new JdbcTemplate(ds);
        img = new Image(new byte[1]);
        em.persist(img);

        owner = new User("username", "wallet", "email@email.com", "password", Chain.Ethereum, Role.User, "en");
        em.persist(owner);

        JdbcTestUtils.deleteFromTables(jdbcTemplate, NFT_TABLE);
    }

    @Test
    public void testCreateNft() {
        nftJpaDao.create(NFT_ID, NFT_CONTRACT_ADDRESS, NFT_NAME, CHAIN, img.getIdImage(), owner, COLLECTION, DESCRIPTION);

        em.flush();
        assertEquals(1, JdbcTestUtils.countRowsInTable(jdbcTemplate, NFT_TABLE));
    }

    @Test
    public void testGetNftById() {
        Nft nft = new Nft(NFT_ID, NFT_CONTRACT_ADDRESS, NFT_NAME, CHAIN, img.getIdImage(), COLLECTION, DESCRIPTION, owner);
        em.persist(nft);

        Optional<Nft> maybeNft = nftJpaDao.getNFTById(nft.getId());

        em.flush();
        assertTrue(maybeNft.isPresent());
        assertEquals(1, JdbcTestUtils.countRowsInTable(jdbcTemplate, NFT_TABLE));
    }

    @Test
    public void testGetNftNotExists() {
        Optional<Nft> nft = nftJpaDao.getNFTById(1500);

        assertFalse(nft.isPresent());
    }

    @Test
    public void testGetAllPublications() {
        for (int i = 0; i < 5; i++) {
            Nft nft = new Nft(NFT_ID + i, NFT_CONTRACT_ADDRESS, NFT_NAME, CHAIN, img.getIdImage(), COLLECTION, DESCRIPTION, owner);
            em.persist(nft);
        }

        List<Nft> publications = nftJpaDao.getAllPublications(1, PAGE_SIZE, null, null, null, null, null, "noSort", null, null, null);

        assertEquals(5, publications.size());
    }

    @Test
    public void testGetAllPublicationsPages() {
        for (int i = 0; i < PAGE_SIZE*1.5; i++) {
            Nft nft = new Nft(NFT_ID + i, NFT_CONTRACT_ADDRESS, NFT_NAME, CHAIN, img.getIdImage(), COLLECTION, DESCRIPTION, owner);
            em.persist(nft);
        }

        List<Nft> publications = nftJpaDao.getAllPublications(1, PAGE_SIZE, null, null, Collections.singletonList(CHAIN.name()), null, null, "noSort", null, null, null);

        assertEquals(PAGE_SIZE, publications.size());
    }

    @Test
    public void testGetAllOnSalePublications() {
        for (int i = 0; i < 5; i++) {
            Nft nft = new Nft(NFT_ID + i, NFT_CONTRACT_ADDRESS, NFT_NAME, CHAIN, img.getIdImage(), COLLECTION, DESCRIPTION, owner);
            em.persist(nft);
        }

        List<Nft> publications = nftJpaDao.getAllPublications(1, PAGE_SIZE, Collections.singletonList("onSale"),  Arrays.asList("Other","Art"), Collections.singletonList(CHAIN.name()), new BigDecimal(1), new BigDecimal(2), "name", "abc", null, null);

        assertEquals(0, publications.size());
    }

    @Test
    public void testGetAllPublicationsByUser() {
        for(int i = 0; i < 3; i++) {
            Nft nft = new Nft(NFT_ID + i, NFT_CONTRACT_ADDRESS, NFT_NAME, CHAIN, img.getIdImage(), COLLECTION, DESCRIPTION, owner);
            em.persist(nft);
        }

        List<Nft> publications = nftJpaDao.getAllPublicationsByUser(1, PAGE_SIZE, owner, false, false, "noSort");

        assertEquals(3, publications.size());
    }

    @Test
    public void testGetAmountPublications() {
        for(int i = 0; i < 15; i++) {
            Nft nft = new Nft(NFT_ID + i, NFT_CONTRACT_ADDRESS, NFT_NAME, CHAIN, img.getIdImage(), COLLECTION, DESCRIPTION, owner);
            em.persist(nft);
        }

        int amountPublications = nftJpaDao.getAmountPublications( null, null, Collections.singletonList(CHAIN.name()), null, null, null, null, null);

        assertEquals(15, amountPublications);
    }

    @Test
    public void testGetAmountPublicationPagesByUser() {
        for(int i = 0; i < 12; i++) {
            Nft nft = new Nft(NFT_ID + i, NFT_CONTRACT_ADDRESS, NFT_NAME, CHAIN, img.getIdImage(), COLLECTION, DESCRIPTION, owner);
            em.persist(nft);
        }

        int amountPublications = nftJpaDao.getAmountPublicationPagesByUser( 2, owner, null, false, false);

        assertEquals(6, amountPublications);
    }

    @Test
    public void testGetNftByPk() {
        Nft nft = new Nft(NFT_ID, NFT_CONTRACT_ADDRESS, NFT_NAME, CHAIN, img.getIdImage(), COLLECTION, DESCRIPTION, owner);
        em.persist(nft);

        Optional<Nft> maybeNft = nftJpaDao.getNftByPk(NFT_ID, NFT_CONTRACT_ADDRESS, CHAIN.name());

        assertTrue(maybeNft.isPresent());
    }

    @Test
    public void testIsNftCreated() {
        Nft nft = new Nft(NFT_ID, NFT_CONTRACT_ADDRESS, NFT_NAME, CHAIN, img.getIdImage(), COLLECTION, DESCRIPTION, owner);
        em.persist(nft);

        boolean nftExists = nftJpaDao.isNftCreated(NFT_ID, NFT_CONTRACT_ADDRESS, CHAIN.name());

        assertTrue(nftExists);
    }

    @Test
    public void testGetRandomNftFromCollection() {
        Nft nft = new Nft(NFT_ID, NFT_CONTRACT_ADDRESS, NFT_NAME, CHAIN, img.getIdImage(), COLLECTION, DESCRIPTION, owner);
        em.persist(nft);
        Nft extraNft = new Nft(NFT_ID+1, NFT_CONTRACT_ADDRESS, NFT_NAME, CHAIN, img.getIdImage(), COLLECTION, DESCRIPTION, owner);
        em.persist(extraNft);

        Optional<Nft> maybeNft = nftJpaDao.getRandomNftFromCollection(nft.getId(), COLLECTION, 1);

        assertTrue(maybeNft.isPresent());
        assertEquals(extraNft.getId(), maybeNft.get().getId());
    }

    @Test
    public void testGetRandomNftFromCategory() {
        Nft nft = new Nft(NFT_ID, NFT_CONTRACT_ADDRESS, NFT_NAME, CHAIN, img.getIdImage(), COLLECTION, DESCRIPTION, owner);
        em.persist(nft);
        SellOrder sellOrder = new SellOrder(BigDecimal.ONE, nft, Category.Collectible);
        em.persist(sellOrder);
        Nft extraNft = new Nft(NFT_ID+1, NFT_CONTRACT_ADDRESS, NFT_NAME, CHAIN, img.getIdImage(), COLLECTION, DESCRIPTION, owner);
        em.persist(extraNft);
        SellOrder extraSellOrder = new SellOrder(BigDecimal.ONE, extraNft, Category.Collectible);
        em.persist(extraSellOrder);

        Optional<Nft> maybeNft = nftJpaDao.getRandomNftFromCategory(nft.getId(), Category.Collectible, 1);

        assertTrue(maybeNft.isPresent());
        assertEquals(extraNft.getId(), maybeNft.get().getId());
    }
}