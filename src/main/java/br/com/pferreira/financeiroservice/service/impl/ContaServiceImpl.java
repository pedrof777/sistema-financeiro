package br.com.pferreira.financeiroservice.service.impl;

import br.com.pferreira.financeiroservice.exception.RecursoNaoEncontradoException;
import br.com.pferreira.financeiroservice.model.dto.ContaRequest;
import br.com.pferreira.financeiroservice.model.dto.ContaResponse;
import br.com.pferreira.financeiroservice.model.entity.Conta;
import br.com.pferreira.financeiroservice.model.entity.Transacao;
import br.com.pferreira.financeiroservice.model.entity.User;
import br.com.pferreira.financeiroservice.model.enums.StatusTransacao;
import br.com.pferreira.financeiroservice.model.enums.TipoTransacao;
import br.com.pferreira.financeiroservice.repository.CategoriaRepository;
import br.com.pferreira.financeiroservice.repository.ContaRepository;
import br.com.pferreira.financeiroservice.repository.TransacaoRepository;
import br.com.pferreira.financeiroservice.repository.UserRepository;
import br.com.pferreira.financeiroservice.service.ContaService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

/**
 * @author Pedro Ferreira
 */

@Slf4j
@Service
@RequiredArgsConstructor
public class ContaServiceImpl implements ContaService {

  private final ContaRepository contaRepository;
  private final TransacaoRepository transacaoRepository;
  private final UserRepository userRepository;

  @Override
  @Transactional
  public ContaResponse criar(ContaRequest request, UUID usuarioId) {
    User usuario = userRepository.findById(usuarioId)
            .orElseThrow(() -> new RecursoNaoEncontradoException("Usuário não encontrado"));

    Conta conta = Conta.builder()
            .nome(request.nome())
            .tipo(request.tipoConta())
            .saldoInicial(request.saldoInicial())
            .usuario(usuario)
            .build();

    Conta contaSalva = contaRepository.save(conta);
    log.info("Conta criada com sucesso: id={}, usuario={}", contaSalva.getId(), usuarioId);

    return toResponse(contaSalva);
  }

  @Override
  @Transactional(readOnly = true)
  public List<ContaResponse> listarPorUsuario(UUID usuarioId) {
    log.debug("Listando contas do usuário {}", usuarioId);
    return contaRepository.findByUsuarioId(usuarioId).stream()
            .map(this::toResponse)
            .toList();
  }

  @Override
  @Transactional(readOnly = true)
  public ContaResponse buscarPorId(UUID id, UUID usuarioID) {
    Conta conta = contaRepository.findByIdAndUsuarioId(id, usuarioID)
            .orElseThrow(() -> new RecursoNaoEncontradoException("Conta não encontrada"));
    return toResponse(conta);
  }


  //Metodos auxiliares

  private ContaResponse toResponse(Conta conta){
    return new ContaResponse(
            conta.getId(),
            conta.getNome(),
            conta.getTipo(),
            conta.getSaldoInicial(),
            calcularSaldoAtual(conta)
    );
  }

  private BigDecimal calcularSaldoAtual(Conta conta){
    List<Transacao> pagas = transacaoRepository.findByContaIdAndStatus(conta.getId(), StatusTransacao.PAGO);

    BigDecimal saldo = conta.getSaldoInicial();
    for (Transacao transacao : pagas){
      saldo = transacao.getTipoTransacao() == TipoTransacao.RECEITA
              ? saldo.add(transacao.getValor())
              : saldo.subtract(transacao.getValor());
    }
    return saldo;
  }
}
