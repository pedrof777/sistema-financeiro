package br.com.pferreira.financeiroservice.model.dto;

import br.com.pferreira.financeiroservice.model.enums.TipoTransacao;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

/**
 * @author Pedro Ferreira
 */

public record TransacaoRequest(
        @NotBlank(message = "Descrição é obrigatória")
        String descricao,

        @NotNull(message = "Valor é obrigatório")
        @Positive(message = "Valor deve ser maior que zero")
        BigDecimal valor,

        @NotNull(message = "Tipo de transação é obrigatório")
        TipoTransacao tipoTransacao,

        @NotNull(message = "Data de vencimento é obrigatória")
        LocalDate dataVencimento,

        @NotNull(message = "Conta é obrigatória")
        UUID contaId,

        @NotNull(message = "Categoria é obrigatória")
        UUID categoriaId
) {
}
