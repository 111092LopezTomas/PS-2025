package org.example.escenalocal.auth.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import java.util.*;
import java.security.Key;
import java.util.Date;

@Component
public class JwtUtil {

  @Value("${jwt.secret}")
  private String secret;

  @Value("${jwt.expiration}")
  private long expirationMs;

  private Key getKey() {
    return Keys.hmacShaKeyFor(secret.getBytes());
  }

//  public String generateToken(String username) {
//    Date now = new Date();
//    Date expiry = new Date(now.getTime() + expirationMs);
//    return Jwts.builder()
//      .setSubject(username)
//      .setIssuedAt(now)
//      .setExpiration(expiry)
//      .signWith(getKey(), SignatureAlgorithm.HS256)
//      .compact();
//  }

  public String generateToken(String username, String role) {
    Date now = new Date();
    Date expiry = new Date(now.getTime() + expirationMs);

    return Jwts.builder()
      .setSubject(username)
      .claim("role", role) // 👈 acá agregás el rol directamente
      .setIssuedAt(now)
      .setExpiration(expiry)
      .signWith(getKey(), SignatureAlgorithm.HS256)
      .compact();
  }


  public String extractUsername(String token) {
    return Jwts.parserBuilder().setSigningKey(getKey()).build()
      .parseClaimsJws(token).getBody().getSubject();
  }

  public boolean validateToken(String token) {
    try {
      Jwts.parserBuilder().setSigningKey(getKey()).build().parseClaimsJws(token);
      return true;
    } catch (JwtException | IllegalArgumentException e) {
      return false;
    }
  }
}
