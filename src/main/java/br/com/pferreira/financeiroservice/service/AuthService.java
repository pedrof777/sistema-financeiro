package br.com.pferreira.financeiroservice.service;

import br.com.pferreira.financeiroservice.model.dto.AuthRequest;
import br.com.pferreira.financeiroservice.model.dto.AuthResponse;
import br.com.pferreira.financeiroservice.model.dto.RegisterRequest;

/**
 * @author Pedro Ferreira
 */

public interface AuthService {
  AuthResponse register(RegisterRequest registerRequest);
  AuthResponse login(AuthRequest authRequest);
}
