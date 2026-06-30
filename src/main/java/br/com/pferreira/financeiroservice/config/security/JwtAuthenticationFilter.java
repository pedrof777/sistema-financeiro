package br.com.pferreira.financeiroservice.config.security;

import br.com.pferreira.financeiroservice.service.JwtService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * @author Pedro Ferreira
 */

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

  private static final String AUTHORIZATION_HEADER = "Authorization";
  private static final String BEARER_PREFIX = "Bearer ";
  private static final int BEARER_PREFIX_LENGTH = BEARER_PREFIX.length();

  private final JwtService jwtService;
  private final UserDetailsService userDetailsService;


  @Override
  protected void doFilterInternal(
          @NonNull HttpServletRequest request,
          @NonNull HttpServletResponse response,
          @NonNull FilterChain filterChain) throws ServletException, IOException {

    try {
      String jwt = extractJwtFromRequest(request);

      if (jwt == null){
        filterChain.doFilter(request, response);
        return;
      }

      authenticateUserIfValid(jwt, request);

      filterChain.doFilter(request,response);
    }catch (io.jsonwebtoken.JwtException | IllegalArgumentException e){
      log.warn("Token inválido: {}", e.getMessage());
      filterChain.doFilter(request,response);
    }
  }

  private String extractJwtFromRequest(HttpServletRequest request){
    String authHeader = request.getHeader(AUTHORIZATION_HEADER);
    if (authHeader != null && authHeader.startsWith(BEARER_PREFIX)){
      return authHeader.substring(BEARER_PREFIX_LENGTH);
    }
    return null;
  }

  private void authenticateUserIfValid(String jwt, HttpServletRequest request){
    String userEmail = jwtService.extractUsername(jwt);

    if(userEmail != null && SecurityContextHolder.getContext().getAuthentication() == null){
      UserDetails userDetails = userDetailsService.loadUserByUsername(userEmail);

      if (jwtService.isTokenValid(jwt, userDetails)){
        setAuthentication(userDetails, request);
        log.debug("Usuário autenticado: {}", userEmail);
      }else{
        log.debug("Token inválido para o usuário: {}", userEmail);
      }
    }
  }

  private void setAuthentication(UserDetails userDetails, HttpServletRequest request){
    UsernamePasswordAuthenticationToken authToken =
            new UsernamePasswordAuthenticationToken(
                    userDetails,
                    null,
                    userDetails.getAuthorities()
            );
    authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
    SecurityContextHolder.getContext().setAuthentication(authToken);
  }
}
