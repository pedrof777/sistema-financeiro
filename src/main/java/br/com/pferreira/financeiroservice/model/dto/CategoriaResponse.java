package br.com.pferreira.financeiroservice.model.dto;

import br.com.pferreira.financeiroservice.model.enums.TipoTransacao;

import java.util.UUID;

/**
 * @author Pedro Ferreira
 */

public record CategoriaResponse(
        UUID id,
        String nome,
        TipoTransacao tipoTransacao
) {
}
