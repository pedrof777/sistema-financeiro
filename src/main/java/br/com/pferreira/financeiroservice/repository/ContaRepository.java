package br.com.pferreira.financeiroservice.repository;

import br.com.pferreira.financeiroservice.model.entity.Conta;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * @author Pedro Ferreira
 */

public interface ContaRepository extends JpaRepository<Conta, UUID> {

  List<Conta> findByUsuarioId(UUID usuarioID);

  Optional<Conta> findByIdAndUsuarioId(UUID id, UUID usuarioId);
}
