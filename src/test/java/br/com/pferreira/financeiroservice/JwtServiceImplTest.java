package br.com.pferreira.financeiroservice;

import br.com.pferreira.financeiroservice.model.entity.User;
import br.com.pferreira.financeiroservice.model.enums.Role;
import br.com.pferreira.financeiroservice.service.impl.JwtServiceImpl;
import io.jsonwebtoken.ExpiredJwtException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * @author Pedro Ferreira
 */

class JwtServiceImplTest {

  private JwtServiceImpl jwtService;
  private User user;

  @BeforeEach
  void setUp(){
    jwtService = new JwtServiceImpl();

    ReflectionTestUtils.setField(jwtService, "secretKey", "3mAfay9GpsvKkua5mhIpS8235ncnudr/hid/K/rQpMNzRKngr2Wc9y9+3Ec8HOcN7QFBO5f5a9dazfDiHbNtiQ==");
    ReflectionTestUtils.setField(jwtService, "jwtExpiration", 3600000L);

    user = User.builder()
            .id(UUID.randomUUID())
            .name("paulo")
            .email("paulo@email.com")
            .password("123")
            .role(Role.USER)
            .build();
  }

  @Test
  void generateToken_shouldReturnNonNotBlankToken(){
    String token = jwtService.generateToken(user);

    assertNotNull(token);
    assertFalse(token.isBlank());
  }

  @Test
  void extractUsername_shouldReturnEmailUsedAsSubject(){
    String token = jwtService.generateToken(user);

    assertEquals(user.getEmail(), jwtService.extractUsername(token));
  }

  @Test
  void extractUsername_shouldThrowException_whenTokenIsExpired(){
    ReflectionTestUtils.setField(jwtService, "jwtExpiration", -1000L);
    String token = jwtService.generateToken(user);

    assertThrows(ExpiredJwtException.class, () -> jwtService.extractUsername(token));
  }

  @Test
  void isTokenValid_shouldReturnTrue_whenTokenMatchesUser(){
    String token = jwtService.generateToken(user);

    assertTrue(jwtService.isTokenValid(token, user));
  }

  @Test
  void isTokenValid_shouldReturnFalse_whenUserIsDifferent(){
    String token = jwtService.generateToken(user);

    User outroUsuario = User.builder().email("ciclando@email.com").role(Role.USER).build();

    assertFalse(jwtService.isTokenValid(token, outroUsuario));
  }
}
