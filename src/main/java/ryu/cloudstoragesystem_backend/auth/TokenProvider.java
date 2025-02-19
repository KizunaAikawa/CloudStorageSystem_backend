package ryu.cloudstoragesystem_backend.auth;

import io.jsonwebtoken.Jwts;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import ryu.cloudstoragesystem_backend.user.User;

import javax.crypto.spec.SecretKeySpec;
import java.security.Key;
import java.util.Date;

@Component
public class TokenProvider {
    private static final String secretKey = "msvECEvTpGqMoZtGisuUar64pW6AGs1kzjwyJKaRMGw=";

    @Value("${token.key-algorithm}")
    public String secretKeyAlgorithm;

    @Getter
    @Value("${token.expiration}")
    private Long tokenExpiration;

    public String generateToken(User user) {
        return Jwts.builder()
                .setSubject(user.getUsername())
                .setIssuedAt(new Date())
                .setNotBefore(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + tokenExpiration))
                .signWith(getSecretKey())
                .compact();
    }

    public String generateToken(String username) {
        return Jwts.builder()
                .setSubject(username)
                .setIssuedAt(new Date())
                .setNotBefore(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + tokenExpiration))
                .signWith(getSecretKey())
                .compact();
    }

    public String getUsernameByToken(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSecretKey())
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
    }

    private Key getSecretKey() {
        return new SecretKeySpec(secretKey.getBytes(), secretKeyAlgorithm);
    }

}
