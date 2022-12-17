package ar.edu.itba.paw.webapp.auth;

import ar.edu.itba.paw.webapp.helpers.Pair;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Header;
import io.jsonwebtoken.Jwts;

import java.security.Key;
import java.util.Date;
import java.util.Map;
import java.util.UUID;

public class JwtUtils {

    public static Pair<String, String> validateAccessToken(String accessToken, Key jwtKey, Date now){
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(jwtKey)
                .build()
                .parseClaimsJws(accessToken)
                .getBody();
        if(claims.getExpiration().before(now)) {
            return new Pair<>("", null);
        }
        return new Pair<>((String)claims.get("token"), claims.getSubject());
    }

    public static String generateAccessToken(Map<String, Object> claims, String issuer, String subject, Date expiration, Date notValidBefore, Date issuedAt, Key signKey){
        return generateJwtToken(claims, issuer, subject, expiration, notValidBefore, issuedAt, signKey, "access");
    }

    public static String generateRefreshToken(Map<String, Object> claims, String issuer, String subject, Date expiration, Date notValidBefore, Date issuedAt, Key signKey){
        return generateJwtToken(claims, issuer, subject, expiration, notValidBefore, issuedAt, signKey, "refresh");
    }

    private static String generateJwtToken(Map<String, Object> claims, String issuer, String subject, Date expiration, Date notValidBefore, Date issuedAt, Key signKey, String tokenType){
        return Jwts.builder()
                .setClaims(claims)
                .claim("token", tokenType)
                .setIssuer(issuer)
                .setSubject(subject)
                .setExpiration(expiration)
                .setNotBefore(notValidBefore)
                .setIssuedAt(issuedAt)
                .setId(UUID.randomUUID().toString())
                .signWith(signKey)
                .setHeaderParam("typ", Header.JWT_TYPE)
                .compact();
    }

}
