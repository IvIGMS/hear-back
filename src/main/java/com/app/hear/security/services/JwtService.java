package com.app.hear.security.services;

import com.app.hear.security.dao.models.entities.UserEntity;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

@Service
public class JwtService {

  @Value("${application.security.jwt.secret-key}")
  private String secretKey;

  @Value("${application.security.jwt.expiration}")
  private long jwtExpirationMs; // en milisegundos

  private Key getSigningKey() {
    return Keys.hmacShaKeyFor(secretKey.getBytes());
  }

  public String generateToken(UserEntity userEntity) {
    Map<String, Object> extraClaims = new HashMap<>();
    extraClaims.put(
        "firstname", Objects.nonNull(userEntity.getFirstname()) ? userEntity.getFirstname() : null);
    extraClaims.put(
        "lastname", Objects.nonNull(userEntity.getLastname()) ? userEntity.getLastname() : null);
    extraClaims.put(
        "isActive", Objects.nonNull(userEntity.getIsActive()) ? userEntity.getIsActive() : null);
    extraClaims.put(
        "role",
        Objects.nonNull(userEntity.getRole().getValue()) ? userEntity.getRole().getValue() : null);
    extraClaims.put("user_id", Objects.nonNull(userEntity.getId()) ? userEntity.getId() : null);
    return generateToken(extraClaims, userEntity);
  }

  public String generateToken(Map<String, Object> extraClaims, UserEntity userEntity) {
    return Jwts.builder()
        .setClaims(extraClaims)
        .setSubject(userEntity.getEmail())
        .setIssuedAt(new Date(System.currentTimeMillis()))
        .setExpiration(new Date(System.currentTimeMillis() + jwtExpirationMs))
        .signWith(getSigningKey(), SignatureAlgorithm.HS256)
        .compact();
  }

  public String extractUsername(String token) {
    return extractClaim(token, Claims::getSubject);
  }

  public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
    final Claims claims = extractAllClaims(token);
    return claimsResolver.apply(claims);
  }

  public boolean isTokenValid(String token, UserDetails userDetails) {
    final String username = extractUsername(token);
    return (username.equals(userDetails.getUsername())) && !isTokenExpired(token);
  }

  private boolean isTokenExpired(String token) {
    return extractExpiration(token).before(new Date());
  }

  private Date extractExpiration(String token) {
    return extractClaim(token, Claims::getExpiration);
  }

  public Claims extractAllClaims(String token) {
    return Jwts.parserBuilder()
        .setSigningKey(getSigningKey())
        .build()
        .parseClaimsJws(token)
        .getBody();
  }
}
