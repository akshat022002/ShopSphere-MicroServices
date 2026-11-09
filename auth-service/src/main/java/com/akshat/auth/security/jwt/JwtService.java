package com.akshat.auth.security.jwt;

import com.akshat.auth.security.userdetails.UserDetailsImplementation;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.function.Function;

@Service
public class JwtService {

    @Value("${jwt.secret}")
    private String secret;

    @Value("${jwt.access-token-expiration}")
    private long accessTokenExpiration;

    @Value("${jwt.refresh-token-expiration}")
    private long refreshTokenExpiration;

    private static final String TYPE_CLAIM = "type";
    private static final String ACCESS = "access";
    private static final String REFRESH = "refresh";


    private SecretKey getSigningKey() {
        return Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
    }

    public String generateAccessToken(UserDetailsImplementation userDetails) {

        return Jwts.builder()
                .subject(userDetails.getUsername())
                .claim("userId", userDetails.getId())
                .claim("role", userDetails.getRole().name())
                .claim(TYPE_CLAIM,ACCESS)
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + accessTokenExpiration))
                .signWith(getSigningKey())
                .compact();

    }

    public String generateRefreshToken(UserDetails userDetails) {

        return Jwts.builder()
                .subject(userDetails.getUsername())
                .claim(TYPE_CLAIM,REFRESH)
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + refreshTokenExpiration))
                .signWith(getSigningKey())
                .compact();
    }

    public <T> T extractClaim(String token, Function<Claims,T> resolver){

        Claims claims = Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();

        return resolver.apply(claims);

    }

    public String extractUsername(String token){
        return extractClaim(token,Claims::getSubject);
    }

    public Date extractExpiration(String token){
        return extractClaim(token,Claims::getExpiration);
    }

    private boolean isTokenExpired(String token){
        return extractExpiration(token).before(new Date());
    }

    public boolean isTokenValid(String token,UserDetails userDetails){

        String username = extractUsername(token);
        return username.equals(userDetails.getUsername()) && !isTokenExpired(token);
    }

    public boolean isAccessToken(String token){
        return extractClaim(token,claims -> claims.get(TYPE_CLAIM,String.class).equals(ACCESS));
    }

    public boolean isRefreshToken(String token){
        return extractClaim(token,claims -> claims.get(TYPE_CLAIM,String.class).equals(REFRESH));
    }
}
