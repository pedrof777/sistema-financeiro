package br.com.pferreira.financeiroservice.model.dto;

import br.com.pferreira.financeiroservice.model.enums.TipoConta;

import java.math.BigDecimal;
import java.util.UUID;

/**
 * @author Pedro Ferreira
 */

public record ContaResponse(
        UUID id,
        String nome,
        TipoConta tipoConta,
        BigDecimal saldoInicial,
        BigDecimal saldoAtual
) {}
