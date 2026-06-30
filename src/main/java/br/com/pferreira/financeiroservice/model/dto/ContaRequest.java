package br.com.pferreira.financeiroservice.model.dto;

import br.com.pferreira.financeiroservice.model.enums.TipoConta;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

/**
 * @author Pedro Ferreira
 */

public record ContaRequest(
        @NotBlank(message = "Nome da conta é obrigatório")
        String nome,

        @NotNull(message = "Tipo da conta é obrigatório")
        TipoConta tipoConta,

        @NotNull(message = "Saldo inicial é obrigatório")
        BigDecimal saldoInicial
) {}
