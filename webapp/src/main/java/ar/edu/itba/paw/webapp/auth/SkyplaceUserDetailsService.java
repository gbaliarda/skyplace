package ar.edu.itba.paw.webapp.auth;

import ar.edu.itba.paw.model.Role;
import ar.edu.itba.paw.model.User;
import ar.edu.itba.paw.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import javax.ws.rs.BadRequestException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

// TODO: Refactor methods used
@Component
public class SkyplaceUserDetailsService implements UserDetailsService {

    private final UserService us;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    public SkyplaceUserDetailsService(final UserService us) {
        this.us = us;
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        final User user = us.getUserByEmail(email).
                orElseThrow(()->new UsernameNotFoundException("No such user with email: " + email));
        final List<GrantedAuthority> roles = new ArrayList<>();
        for(String rol:Role.getRoles()) {
            if (user.getRole().name().equals(rol)) {
                roles.add(new SimpleGrantedAuthority(String.format("ROLE_%s", rol.toUpperCase())));
            }
        }
        return new org.springframework.security.core.userdetails.User(user.getEmail(), user.getPassword(), roles);
    }

    public UsernamePasswordAuthenticationToken restLogin(String email, String password) throws UsernameNotFoundException {
        final User user = us.getUserByEmail(email).
                orElseThrow(() -> new UsernameNotFoundException("UsernameNotFoundException.message"));
        if(!passwordEncoder.matches(password, user.getPassword())) {
            throw new UsernameNotFoundException("UsernameNotFoundException.message");
        }

        final List<GrantedAuthority> roles = new ArrayList<>();
        for(String rol : Role.getRoles()) {
            if (user.getRole().name().equals(rol)) {
                roles.add(new SimpleGrantedAuthority(String.format("ROLE_%s", rol.toUpperCase())));
            }
        }
        return new UsernamePasswordAuthenticationToken(email, password, roles);
    }

    public UsernamePasswordAuthenticationToken jwtLogin(String email) throws UsernameNotFoundException {
        final User user = us.getUserByEmail(email).
                orElseThrow(() -> new UsernameNotFoundException("UsernameNotFoundException.message"));

        final List<GrantedAuthority> roles = new ArrayList<>();
        for(String rol:Role.getRoles()) {
            if (user.getRole().name().equals(rol)) {
                roles.add(new SimpleGrantedAuthority(String.format("ROLE_%s", rol.toUpperCase())));
            }
        }
        return new UsernamePasswordAuthenticationToken(email, user.getPassword(), roles);
    }

}
