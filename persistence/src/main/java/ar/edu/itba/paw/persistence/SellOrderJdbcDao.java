package ar.edu.itba.paw.persistence;

import ar.edu.itba.paw.model.NftCard;
import ar.edu.itba.paw.model.SellOrder;
import ar.edu.itba.paw.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;
import org.springframework.web.multipart.MultipartFile;

import javax.sql.DataSource;
import java.math.BigDecimal;
import java.util.*;

@Repository
public class SellOrderJdbcDao implements SellOrderDao {

    private final JdbcTemplate jdbcTemplate;
    private final SimpleJdbcInsert jdbcInsertSellOrder;
    private final SimpleJdbcInsert jdbcInsertNft;
    private final SimpleJdbcInsert jdbcInsertImage;
    private final SimpleJdbcInsert jdbcInsertFavorited;

    private static final RowMapper<SellOrder> ROW_MAPPER = (rs, rowNum) ->
        new SellOrder(rs.getLong("id"), rs.getString("seller_email"), rs.getString("descr"), rs.getBigDecimal("price"), rs.getInt("id_nft"), rs.getString("nft_addr"), rs.getString("category"));

    @Autowired
    public SellOrderJdbcDao(final DataSource ds) {
        jdbcTemplate = new JdbcTemplate(ds);
        jdbcInsertSellOrder = new SimpleJdbcInsert(ds)
                .withTableName("SellOrders")
                .usingGeneratedKeyColumns("id");
        jdbcInsertNft = new SimpleJdbcInsert(ds)
                .withTableName("Nfts");
        jdbcInsertImage = new SimpleJdbcInsert(ds)
                .withTableName("images")
                .usingGeneratedKeyColumns("id_image");
        jdbcInsertFavorited = new SimpleJdbcInsert(ds)
                .withTableName("Favorited");
    }

    private List<NftCard> executeSelectNFTQuery(String query, Object[] args) {
        return jdbcTemplate.query(query, args, (rs, i) -> {
            String name = rs.getString("nft_name");
            String contract_addr = rs.getString("contract_addr");
            String chain = rs.getString("chain");
            long id_product = rs.getLong("id_product");
            long id_image = rs.getLong("id_image");
            BigDecimal price = rs.getBigDecimal("price");
            int score = 0;
            long id_nft = rs.getLong("id_nft");
            String category = rs.getString("category");
            String seller_email = rs.getString("seller_email");
            String descr = rs.getString("descr");
            boolean is_faved = true;
            return new NftCard(id_image, name, chain, price, score, category, seller_email, descr, contract_addr, id_nft, id_product, is_faved);
        });
    }

    @Override
    public Optional<SellOrder> getOrderById(long id) {
        return jdbcTemplate.query("SELECT * FROM SellOrders WHERE id = ?", new Object[]{ id }, ROW_MAPPER).stream().findFirst();
    }

    @Override
    public List<NftCard> getUserSellOrders(String userEmail){
        List<Object> args = new ArrayList<>();
        StringBuilder baseQuery = new StringBuilder("SELECT sellorders.id AS id_product, category, nfts.id AS id_nft, contract_addr, nft_name, id_image, chain, price, descr, seller_email FROM nfts NATURAL JOIN chains INNER JOIN sellorders ON (id_nft = nfts.id AND nft_addr = contract_addr) WHERE seller_email LIKE ? ORDER BY nft_name");
        args.add(userEmail);
        return executeSelectNFTQuery(baseQuery.toString(), args.toArray());
    }

    @Override
    public List<NftCard> getUserFavorites(long userId) {
        List<Object> args = new ArrayList<>();
        String baseQuery =
                "SELECT sellorder_id AS id_product, category, id_nft, contract_addr, nft_name, id_image, chain, price, descr, seller_email " +
                        "FROM nfts INNER JOIN (" +
                        "SELECT * FROM favorited INNER JOIN sellorders " +
                        "ON (sellorder_id = id) WHERE user_id = ?) AS favs " +
                        "ON (nfts.id = id_nft AND contract_addr = nft_addr AND chain = nft_chain) " +
                        "ORDER BY nft_name;";
        args.add(userId);
        return executeSelectNFTQuery(baseQuery, args.toArray());
    }

    @Override
    public boolean addFavorite(long userId, long sellOrderId) {
        Map<String, Object> favoritedData = new HashMap<>();
        favoritedData.put("user_id", userId);
        favoritedData.put("sellorder_id", sellOrderId);
        return jdbcInsertFavorited.execute(favoritedData) == 1;
    }

    @Override
    public boolean removeFavorite(long userId, long sellOrderId) {
        String deleteFavQuery = "DELETE FROM favorited WHERE user_id = ? AND sellorder_id = ?";
        return jdbcTemplate.update(deleteFavQuery, userId, sellOrderId) == 1;
    }

    @Override
    public boolean update(long id, String category, BigDecimal price, String description) {
        String updateQuery = "UPDATE sellorders SET category = ?, price = ?, descr = ? WHERE id = ?";
        // returns the number of affected rows
        return jdbcTemplate.update(updateQuery, category, price, description, id) == 1;
    }

    @Override
    public boolean delete(long id) {
        String updateQuery = "DELETE FROM sellorders WHERE id = ?";
        return jdbcTemplate.update(updateQuery, id) == 1;
    }

}
