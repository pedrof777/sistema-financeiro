package br.com.pferreira.financeiroservice.model.dto;

import br.com.pferreira.financeiroservice.model.enums.TipoTransacao;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

/**
 * @author Pedro Ferreira
 */

public record CategoriaRequest(
        @NotBlank(message = "Nome da categoria é obrigatório")
        String nome,

        @NotNull(message = "Tipo da categoria é obrigatório")
        TipoTransacao tipoTransacao
) {}
