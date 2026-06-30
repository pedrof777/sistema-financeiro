package br.com.pferreira.financeiroservice.model.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

/**
 * @author Pedro Ferreira
 */

public record RegisterRequest(
        @NotBlank String name,
        @NotBlank @Email String email,
        @NotBlank String password
) {}
