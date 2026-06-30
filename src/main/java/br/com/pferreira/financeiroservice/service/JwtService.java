package br.com.pferreira.financeiroservice.service;

import io.jsonwebtoken.Claims;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Map;
import java.util.function.Function;

/**
 * @author Pedro Ferreira
 */

public interface JwtService {

  String generateToken(UserDetails userDetails);

  String generateToken(Map<String, Object> extraClaims, UserDetails userDetails);

  String extractUsername(String token);

  boolean isTokenValid(String token, UserDetails userDetails);

  <T> T extractClaim(String token, Function<Claims, T> claimsResolver);
}
