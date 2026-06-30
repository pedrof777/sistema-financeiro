package br.com.pferreira.financeiroservice.service.impl;

import br.com.pferreira.financeiroservice.model.dto.AuthRequest;
import br.com.pferreira.financeiroservice.model.dto.AuthResponse;
import br.com.pferreira.financeiroservice.model.dto.RegisterRequest;
import br.com.pferreira.financeiroservice.model.entity.User;
import br.com.pferreira.financeiroservice.model.enums.Role;
import br.com.pferreira.financeiroservice.repository.UserRepository;
import br.com.pferreira.financeiroservice.service.AuthService;
import br.com.pferreira.financeiroservice.service.JwtService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

/**
 * @author Pedro Ferreira
 */

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

  private final UserRepository userRepository;
  private final PasswordEncoder passwordEncoder;
  private final JwtService jwtService;
  private final AuthenticationManager authenticationManager;

  @Override
  public AuthResponse register(RegisterRequest registerRequest) {
    if (userRepository.existsByEmail(registerRequest.email())){
      throw new RuntimeException("Email já cadastrado: " + registerRequest.email());
    }

    User user = User.builder()
            .name(registerRequest.name())
            .password(passwordEncoder.encode(registerRequest.password()))
            .email(registerRequest.email())
            .role(Role.USER)
            .build();

    userRepository.save(user);
    log.info("Usuário registrado com sucesso: {}", user.getEmail());

    String token = jwtService.generateToken(user);
    return new AuthResponse(token, null, user.getEmail(), user.getRole().name());
  }

  @Override
  public AuthResponse login(AuthRequest authRequest) {

    try {
      authenticationManager.authenticate(
              new UsernamePasswordAuthenticationToken(
                      authRequest.email(),
                      authRequest.password())
      );
    }catch (BadCredentialsException e){
      throw new IllegalArgumentException("E-mail ou senha inválidos");
    }

    User user = userRepository.findByEmail(authRequest.email())
            .orElseThrow(() -> new RuntimeException("Usuário não encontrado!"));

    log.info("Login realizado com sucesso: {}", user.getEmail());

    String token = jwtService.generateToken(user);
    return new AuthResponse(token, null, user.getEmail(), user.getRole().name());
  }
}
