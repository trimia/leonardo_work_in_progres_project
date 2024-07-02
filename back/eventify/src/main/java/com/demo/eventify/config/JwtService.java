package com.demo.eventify.config;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.function.Function;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

@Service
public class JwtService {

    //  @Value("${application.security.jwt.secret-key}")
    private static final String secretKey = "74e0567dccf63391e5efbbbd7e46c364a233f6bad3ca8c6e642bb36479787d6f";
    //  @Value("${application.security.jwt.expiration}")
    private final static long jwtExpiration = 1000 * 60 * 5;
    private final static long jwtResetExpiration = 1000 * 60 * 5;
    //  @Autowired
//  @Component
//  @Value("${application.security.jwt.refresh-token.expiration}")
    private static final long refreshExpiration = 1000 * 60 * 10;

    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }
    public <T> T extractResetClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractResetAllClaims(token);
        return claimsResolver.apply(claims);
    }

    public String generateToken(Map<String, Object> extraClaims, UserDetails userDetails) {
        return buildToken(extraClaims, userDetails, jwtExpiration);
    }

    public String generateToken(UserDetails userDetails) {
        return generateToken(new HashMap<>(), userDetails);
    }

    public String generateRefreshToken(UserDetails userDetails) {
        return buildToken(new HashMap<>(), userDetails, refreshExpiration);
    }

    public String generateFirstResetToken(UserDetails userDetails) {
        return buildFirstResetToken(new HashMap<>(), userDetails, jwtResetExpiration);
    }
    public String generateResetToken(UserDetails userDetails) {
        return buildResetToken(new HashMap<>(), userDetails, jwtResetExpiration);
    }

    private String buildToken(Map<String, Object> extraClaims, UserDetails userDetails, long expiration) {
        return Jwts.builder().setClaims(extraClaims).setSubject(userDetails.getUsername()).setIssuedAt(new Date(System.currentTimeMillis())).setExpiration(new Date(System.currentTimeMillis() + expiration)) ///////////////////// Set another expiration date cause it is not safe
                .setId(UUID.randomUUID().toString()) /////////////////////////// check if it's workin to elude jwt replace
                .signWith(getSignInKey(), SignatureAlgorithm.HS256) //////////////////// after brainstorming decided they will upgrade the encryption GG
                .compact();
    }

    private String buildFirstResetToken(Map<String, Object> extraClaims, UserDetails userDetails, long expiration) {
        return Jwts.builder()
                .setClaims(extraClaims)
                .setSubject("non te lo dico pappappero!!")
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + expiration)) ///////////////////// Set another expiration date cause it is not safe
                .setId(UUID.randomUUID().toString()) /////////////////////////// check if it's workin to elude jwt replace
                .signWith(getResetKey(), SignatureAlgorithm.HS256) //////////////////// after brainstorming decided they will upgrade the encryption GG
                .compact();
    }
    private String buildResetToken(Map<String, Object> extraClaims, UserDetails userDetails, long expiration) {
        return Jwts.builder()
                .setClaims(extraClaims)
                .setSubject(userDetails.getUsername())
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + expiration)) ///////////////////// Set another expiration date cause it is not safe
                .setId(UUID.randomUUID().toString()) /////////////////////////// check if it's workin to elude jwt replace
                .signWith(getResetKey(), SignatureAlgorithm.HS256) //////////////////// after brainstorming decided they will upgrade the encryption GG
                .compact();
    }

    public boolean isTokenValid(String token, UserDetails userDetails) {
        final String username = extractUsername(token);
        return (username.equals(userDetails.getUsername())) && !isTokenExpired(token);
    }

//    public boolean killtoken(){
//
//    }

    public boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }
    public boolean isResetTokenExpired(String token) {
        return extractResetExpiration(token).before(new Date());
    }

    private Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }
    private Date extractResetExpiration(String token) {
        return extractResetClaim(token, Claims::getExpiration);
    }

    private Claims extractAllClaims(String token) {
        return Jwts.parserBuilder().setSigningKey(getSignInKey()).build().parseClaimsJws(token).getBody();
    }
    private Claims extractResetAllClaims(String token) {
        return Jwts.parserBuilder().setSigningKey(getResetKey()).build().parseClaimsJws(token).getBody();
    }

    private Key getSignInKey() {
        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    private Key getResetKey() {
        byte[] keyBytes = Decoders.BASE64.decode("nontelodicopapapapapapapapapapapapapapapaapapapapapapapappappero");
        return Keys.hmacShaKeyFor(keyBytes);
    }
}
