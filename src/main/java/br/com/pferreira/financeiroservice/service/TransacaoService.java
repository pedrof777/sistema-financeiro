package br.com.pferreira.financeiroservice.service;

import br.com.pferreira.financeiroservice.model.dto.TransacaoRequest;
import br.com.pferreira.financeiroservice.model.dto.TransacaoResponse;

import java.util.List;
import java.util.UUID;

/**
 * @author Pedro Ferreira
 */

public interface TransacaoService {
  TransacaoResponse criar(TransacaoRequest transacaoRequest, UUID usuarioId);
  List<TransacaoResponse> listarPorConta(UUID contaId, UUID usuarioId);
  TransacaoResponse marcarComoPaga(UUID id, UUID usuarioId);
}
