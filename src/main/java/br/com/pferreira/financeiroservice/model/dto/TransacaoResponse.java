package br.com.pferreira.financeiroservice.model.dto;

import br.com.pferreira.financeiroservice.model.enums.StatusTransacao;
import br.com.pferreira.financeiroservice.model.enums.TipoTransacao;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

/**
 * @author Pedro Ferreira
 */

public record TransacaoResponse(
        UUID id,
        String descricao,
        BigDecimal valor,
        TipoTransacao tipoTransacao,
        StatusTransacao statusTransacao,
        LocalDate dataVencimento,
        LocalDate dataPagamento,
        String nomeConta,
        String nomeCategoria
) {}
