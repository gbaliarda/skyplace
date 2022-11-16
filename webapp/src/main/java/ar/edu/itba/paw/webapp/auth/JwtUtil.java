package ar.edu.itba.paw.webapp.auth;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;

public class JwtUtil {

    public static String generateToken(String subject){
        Claims claims = Jwts.claims().setSubject(subject);

        return Jwts.builder()
                .setClaims(claims)
                .compact();
    }

}
