package br.com.pferreira.financeiroservice.repository;

import br.com.pferreira.financeiroservice.model.entity.Categoria;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * @author Pedro Ferreira
 */

public interface CategoriaRepository extends JpaRepository<Categoria, UUID> {

  List<Categoria> findByUsuarioId(UUID usuarioId);

  Optional<Categoria> findByIdAndUsuarioId(UUID id, UUID usuarioId);

}
