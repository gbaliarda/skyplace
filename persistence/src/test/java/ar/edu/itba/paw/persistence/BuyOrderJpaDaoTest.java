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
import java.util.*;

import static org.junit.Assert.*;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = TestConfig.class)
@Transactional
public class BuyOrderJpaDaoTest {
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private BuyOrderJpaDao buyOrderJpaDao;

    private final int pageSize = 5;

    private static final BigDecimal PRICE = new BigDecimal(10);

    @PersistenceContext
    private EntityManager em;

    @Autowired
    private DataSource ds;

    private User owner;
    private Image img;
    private Nft nft;
    private SellOrder sellOrder;

    private static final String BUYORDER_TABLE = "buyorders";

    @Before
    public void setUp() {
        jdbcTemplate = new JdbcTemplate(ds);
        img = new Image(new byte[1]);
        em.persist(img);

        owner = new User("username", "wallet", "email@email.com", "password", Chain.Ethereum, Role.User, "en");
        em.persist(owner);

        nft = new Nft(123, "addr", "name", Chain.Ethereum, img.getIdImage(), "collection", "description", owner);
        em.persist(nft);

        sellOrder = new SellOrder(BigDecimal.ONE, nft, Category.Collectible);
        em.persist(sellOrder);
    }

    @Test
    public void testCreateNewBuyOrder() {

        buyOrderJpaDao.create(sellOrder, PRICE, owner);

        em.flush();
        assertEquals(1,JdbcTestUtils.countRowsInTable(jdbcTemplate, BUYORDER_TABLE));
    }

    @Test
    public void testUpdateBuyOrder() {
        BuyOrder buyOrder = new BuyOrder(BigDecimal.ONE, sellOrder, owner, StatusBuyOrder.NEW, null);
        em.persist(buyOrder);

        buyOrderJpaDao.create(sellOrder, PRICE, owner);

        em.flush();;
        assertEquals(1, JdbcTestUtils.countRowsInTable(jdbcTemplate, BUYORDER_TABLE));
        assertEquals(1, JdbcTestUtils.countRowsInTableWhere(jdbcTemplate, BUYORDER_TABLE, "amount = 10"));
    }


    @Test
    public void testGetOrdersBySellOrderId() {
        BuyOrder buyOrder = new BuyOrder(BigDecimal.ONE, sellOrder, owner, StatusBuyOrder.NEW, null);
        em.persist(buyOrder);

        List<BuyOrder> buyOrders = buyOrderJpaDao.getOrdersBySellOrderId(1, sellOrder.getId() ,"ALL", pageSize);

        assertEquals(1, buyOrders.size());
    }

    @Test
    public void testAcceptBuyOrder() {
        BuyOrder buyOrder = new BuyOrder(BigDecimal.ONE, sellOrder, owner, StatusBuyOrder.NEW, null);
        em.persist(buyOrder);

        buyOrderJpaDao.acceptBuyOrder(sellOrder.getId(), owner.getId());

        assertEquals(StatusBuyOrder.PENDING, buyOrder.getStatus());
    }

    @Test
    public void testDeleteBuyOrder() {
        BuyOrder buyOrder = new BuyOrder(BigDecimal.ONE, sellOrder, owner, StatusBuyOrder.NEW, null);
        em.persist(buyOrder);

        buyOrderJpaDao.deleteBuyOrder(sellOrder.getId(), owner.getId());

        em.flush();
        assertEquals(0, JdbcTestUtils.countRowsInTable(jdbcTemplate, BUYORDER_TABLE));
    }

}
