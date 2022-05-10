package ar.edu.itba.paw.persistence;

import ar.edu.itba.paw.exceptions.UserAlreadyExistsException;
import ar.edu.itba.paw.model.User;
import ar.edu.itba.paw.exceptions.CreateUserException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Repository
public class UserJdbcDao implements UserDao{

    private static final Logger LOGGER = LoggerFactory.getLogger(UserJdbcDao.class);

    private final JdbcTemplate jdbcTemplate;
    private final SimpleJdbcInsert jdbcInsertSellOrder;

    private static final RowMapper<User> ROW_MAPPER = (rs, rowNum) ->
            new User(rs.getInt("id"), rs.getString("email"), rs.getString("username"), rs.getString("wallet"), rs.getString("wallet_chain"), rs.getString("password"), rs.getString("role"));

    @Autowired
    public UserJdbcDao(final DataSource ds) {
        jdbcTemplate = new JdbcTemplate(ds);
        jdbcInsertSellOrder = new SimpleJdbcInsert(ds)
                .withTableName("Users")
                .usingGeneratedKeyColumns("id");
    }

    @Override
    public Optional<User> create(String email, String username, String wallet, String walletChain, String password) {
        final Map<String, Object> userData = new HashMap<>();
        userData.put("email", email);
        userData.put("username", username);
        userData.put("wallet", wallet);
        userData.put("wallet_chain", walletChain);
        userData.put("password", password);
        userData.put("role", "User");

        try {
            final int userId = jdbcInsertSellOrder.executeAndReturnKey(userData).intValue();
            return Optional.of(new User(userId, email, username, wallet, walletChain, password, "User"));
        } catch (DuplicateKeyException e) {
            LOGGER.error("User already exists {}", e.getMessage());
            throw new UserAlreadyExistsException();
        } catch (DataIntegrityViolationException e) {
            LOGGER.error("Invalid user data {}", e.getMessage());
            throw new CreateUserException();
        } catch (Exception e) {
            LOGGER.error("Can not create user {}", e.getMessage());
            throw new CreateUserException();
        }
    }

    @Override
    public Optional<User> getUserByEmail(final String email) {
        return jdbcTemplate.query("SELECT * FROM users WHERE email = ?", new Object[]{ email }, ROW_MAPPER).stream().findFirst();
    }

    @Override
    public Optional<User> getUserById(final int id) {
        return jdbcTemplate.query("SELECT * FROM users WHERE id = ?", new Object[]{ id }, ROW_MAPPER).stream().findFirst();
    }

    @Override
    public boolean userOwnsNft(int productId, User user) {
        return jdbcTemplate.query("SELECT * FROM users JOIN nfts ON (users.id=nfts.id_owner) WHERE users.id=? AND nfts.nft_id=?", new Object[]{user.getId(), productId}, (rs, rn) -> rs.getString("username")).size() > 0;
    }

    @Override
    public boolean userOwnsSellOrder(int productId, User user) {
        return jdbcTemplate.query("SELECT * FROM users JOIN nfts ON (users.id=nfts.id_owner) JOIN sellorders ON (nfts.id=sellorders.id_nft) WHERE users.id=? AND sellorders.id=?", new Object[]{user.getId(), productId}, (rs, rn) -> rs.getString("username")).size() > 0;
    }

}
