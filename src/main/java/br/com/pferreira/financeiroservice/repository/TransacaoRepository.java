package br.com.pferreira.financeiroservice.repository;

import br.com.pferreira.financeiroservice.model.entity.Transacao;
import br.com.pferreira.financeiroservice.model.enums.StatusTransacao;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * @author Pedro Ferreira
 */

public interface TransacaoRepository extends JpaRepository<Transacao, UUID> {

  List<Transacao> findByContaId(UUID contaId);

  Optional<Transacao> findByIdAndConta_Usuario_Id(UUID id, UUID usuarioId);

  List<Transacao> findByConta_Usuario_IdAndStatus(UUID usuarioId, StatusTransacao statusTransacao);

  List<Transacao> findByContaIdAndStatus(UUID contaId, StatusTransacao statusTransacao);
}
