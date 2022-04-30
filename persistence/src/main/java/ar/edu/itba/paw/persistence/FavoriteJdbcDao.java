package ar.edu.itba.paw.persistence;

import ar.edu.itba.paw.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

@Repository
public class FavoriteJdbcDao implements FavoriteDao{

    private final JdbcTemplate jdbcTemplate;
    private final SimpleJdbcInsert jdbcInsertFavorited;
    private final NftDao nftDao;

    @Autowired
    public FavoriteJdbcDao(DataSource ds, NftDao nftDao) {
        jdbcTemplate = new JdbcTemplate(ds);
        jdbcInsertFavorited = new SimpleJdbcInsert(ds)
                .withTableName("favorited");
        this.nftDao = nftDao;
    }


    @Override
    public void addNftFavorite(String productId, User user) {
        if(!nftDao.getNFTById(productId).isPresent())
            return;
        long id_nft;
        try {
            id_nft = Long.parseLong(productId);
        } catch(Exception e) {
            return;
        }

        Map<String, Object> favoriteData = new HashMap<>();
        favoriteData.put("user_id", user.getId());
        favoriteData.put("id_nft", id_nft);
        try {
            jdbcInsertFavorited.execute(favoriteData);
        } catch (Exception ignored){}
    }

    @Override
    public void removeNftFavorite(String productId, User user) {
        if(!nftDao.getNFTById(productId).isPresent())
            return;
        long id_nft;
        try {
            id_nft = Long.parseLong(productId);
        } catch(Exception e) {
            return;
        }
        try {
            jdbcTemplate.update("DELETE FROM favorited WHERE user_id=? AND id_nft=?", user.getId(), id_nft);
        } catch (Exception ignored){}
    }

    @Override
    public boolean userFavedNft(long user_id, long id_nft) {
        return jdbcTemplate.query("SELECT * FROM favorited WHERE user_id=? AND id_nft=?", new Object[]{user_id, id_nft}, (rs, rownum) -> rs.getString("id_nft")).size() > 0;
    }
}
