package br.com.pferreira.financeiroservice.service;

import br.com.pferreira.financeiroservice.model.dto.CategoriaRequest;
import br.com.pferreira.financeiroservice.model.dto.CategoriaResponse;

import java.util.List;
import java.util.UUID;

/**
 * @author Pedro Ferreira
 */

public interface CategoriaService{

  CategoriaResponse criar(CategoriaRequest catRequest, UUID usuarioId);
  List<CategoriaResponse> listarPorUsuario(UUID usuarioID);
}
