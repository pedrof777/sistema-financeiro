package br.com.pferreira.financeiroservice.model.dto;

/**
 * @author Pedro Ferreira
 */

public record AuthResponse(
        String accessToken,
        String refreshToken,
        String email,
        String role
) {
}
