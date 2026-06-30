package br.com.pferreira.financeiroservice.service;

import br.com.pferreira.financeiroservice.model.dto.ContaRequest;
import br.com.pferreira.financeiroservice.model.dto.ContaResponse;

import java.util.List;
import java.util.UUID;

/**
 * @author Pedro Ferreira
 */

public interface ContaService {

  ContaResponse criar(ContaRequest request, UUID usuarioId);
  List<ContaResponse> listarPorUsuario(UUID usuarioId);
  ContaResponse buscarPorId(UUID id, UUID usuarioID);
}
