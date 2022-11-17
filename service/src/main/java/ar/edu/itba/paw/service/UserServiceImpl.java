package ar.edu.itba.paw.service;

import ar.edu.itba.paw.exceptions.UserAlreadyExistsException;
import ar.edu.itba.paw.exceptions.UserNotLoggedInException;
import ar.edu.itba.paw.model.Chain;
import ar.edu.itba.paw.model.User;
import ar.edu.itba.paw.persistence.UserDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Locale;
import java.util.Optional;

@Service
public class UserServiceImpl implements UserService{

    private final UserDao userDao;
    private final PasswordEncoder passwordEncoder;
    private final MailingService mailingService;

    @Autowired
    public UserServiceImpl(final UserDao userDao, final MailingService mailingService, final PasswordEncoder passwordEncoder) {
        this.userDao = userDao;
        this.passwordEncoder = passwordEncoder;
        this.mailingService = mailingService;
    }
    @Transactional
    @Override
    public User create(String email, String username, String wallet, String walletChain, String password) {
        if(userDao.getUserByEmail(email).isPresent())
            throw new UserAlreadyExistsException();
        Locale locale = LocaleContextHolder.getLocale();
        User user = userDao.create(email, username, wallet, Chain.valueOf(walletChain), passwordEncoder.encode(password), locale.toLanguageTag());
        mailingService.sendRegisterMail(email, username, locale);
        return user;
    }

    @Override
    public Optional<User> getUserByEmail(String email) {
        return userDao.getUserByEmail(email);
    }

    @Override
    public Optional<User> getUserByUsername(String username) {
        return userDao.getUserByUsername(username);
    }

    @Override
    public Optional<User> getUserById(int id) {
        return userDao.getUserById(id);
    }

    @Override
    public Optional<User> getCurrentUser() {
        String principal = (String)SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return getUserByEmail(principal);
    }

    @Override
    public boolean userOwnsNft(int productId, User user) {
        return userDao.userOwnsNft(productId, user);
    }

    @Override
    public boolean currentUserOwnsNft(int nftId) {
        User currentUser = getCurrentUser().orElseThrow(UserNotLoggedInException::new);
        return userOwnsNft(nftId, currentUser);
    }

    @Override
    public boolean isAdmin(){
        return SecurityContextHolder.getContext().getAuthentication().getAuthorities()
                .stream().anyMatch(r -> r.getAuthority().equals("ROLE_ADMIN"));
    }

}
