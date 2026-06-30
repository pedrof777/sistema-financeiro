package br.com.pferreira.financeiroservice.controller;

import br.com.pferreira.financeiroservice.model.dto.AuthRequest;
import br.com.pferreira.financeiroservice.model.dto.AuthResponse;
import br.com.pferreira.financeiroservice.model.dto.RegisterRequest;
import br.com.pferreira.financeiroservice.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author Pedro Ferreira
 */

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

  private final AuthService authService;

  @PostMapping("/register")
  public ResponseEntity<AuthResponse> criar(@RequestBody @Valid RegisterRequest registerRequest){
    AuthResponse authResponse = authService.register(registerRequest);
    return ResponseEntity.status(HttpStatus.CREATED).body(authResponse);
  }

  @PostMapping("/login")
  public ResponseEntity<AuthResponse> login(@RequestBody @Valid AuthRequest authRequest){
    AuthResponse authResponse = authService.login(authRequest);
    return ResponseEntity.ok(authResponse);
  }
}
