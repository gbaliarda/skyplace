package ar.edu.itba.paw.persistence;

import ar.edu.itba.paw.model.User;

import java.util.Optional;

public interface UserDao {

    User create(String email, String username, String wallet, String password);

    Optional<User> getUserByEmail(String email);
}