package br.com.pferreira.financeiroservice.service.impl;

import br.com.pferreira.financeiroservice.exception.RecursoNaoEncontradoException;
import br.com.pferreira.financeiroservice.model.dto.CategoriaRequest;
import br.com.pferreira.financeiroservice.model.dto.CategoriaResponse;
import br.com.pferreira.financeiroservice.model.entity.Categoria;
import br.com.pferreira.financeiroservice.model.entity.User;
import br.com.pferreira.financeiroservice.repository.CategoriaRepository;
import br.com.pferreira.financeiroservice.repository.UserRepository;
import br.com.pferreira.financeiroservice.service.CategoriaService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

/**
 * @author Pedro Ferreira
 */

@Slf4j
@Service
@AllArgsConstructor
public class CategoriaServiceImpl implements CategoriaService {

  private final CategoriaRepository categoriaRepository;
  private final UserRepository userRepository;

  @Override
  @Transactional
  public CategoriaResponse criar(CategoriaRequest catRequest, UUID usuarioId) {
    User usuario = userRepository.findById(usuarioId)
            .orElseThrow(() -> new RecursoNaoEncontradoException("Usuário não encontrado"));

    Categoria categoria = Categoria.builder()
            .nome(catRequest.nome())
            .tipo(catRequest.tipoTransacao())
            .usuario(usuario)
            .build();
    Categoria categoriaSalva = categoriaRepository.save(categoria);
    log.info("Categoria criada com sucesso: id={} ", categoriaSalva.getId());

    return  toResponse(categoriaSalva);
  }

  @Override
  @Transactional(readOnly = true)
  public List<CategoriaResponse> listarPorUsuario(UUID usuarioID) {
    log.debug("Listando categorias do usuário {}", usuarioID);
    return categoriaRepository.findByUsuarioId(usuarioID).stream()
            .map(this::toResponse)
            .toList();
  }

  //Metodos auxiliares
  private CategoriaResponse toResponse(Categoria categoria){
    return new CategoriaResponse(
            categoria.getId(),
            categoria.getNome(),
            categoria.getTipo()
    );
  }
}
