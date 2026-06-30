package br.com.pferreira.financeiroservice.service.impl;

import br.com.pferreira.financeiroservice.exception.RecursoNaoEncontradoException;
import br.com.pferreira.financeiroservice.model.dto.TransacaoRequest;
import br.com.pferreira.financeiroservice.model.dto.TransacaoResponse;
import br.com.pferreira.financeiroservice.model.entity.Categoria;
import br.com.pferreira.financeiroservice.model.entity.Conta;
import br.com.pferreira.financeiroservice.model.entity.Transacao;
import br.com.pferreira.financeiroservice.model.enums.StatusTransacao;
import br.com.pferreira.financeiroservice.repository.CategoriaRepository;
import br.com.pferreira.financeiroservice.repository.ContaRepository;
import br.com.pferreira.financeiroservice.repository.TransacaoRepository;
import br.com.pferreira.financeiroservice.service.TransacaoService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

/**
 * @author Pedro Ferreira
 */

@Slf4j
@Service
@AllArgsConstructor
public class TransacaoServiceImpl implements TransacaoService {

  private final TransacaoRepository transacaoRepository;
  private final CategoriaRepository categoriaRepository;
  private final ContaRepository contaRepository;

  @Override
  public TransacaoResponse criar(TransacaoRequest transacaoRequest, UUID usuarioId) {
    Conta conta = contaRepository.findByIdAndUsuarioId(transacaoRequest.contaId(), usuarioId)
            .orElseThrow(() -> new RecursoNaoEncontradoException("Conta não encontrada"));

    Categoria categoria = categoriaRepository.findByIdAndUsuarioId(transacaoRequest.categoriaId(), usuarioId)
            .orElseThrow(() -> new RecursoNaoEncontradoException("Categoria não encontrada"));

    Transacao transacao = Transacao.builder()
            .descricao(transacaoRequest.descricao())
            .valor(transacaoRequest.valor())
            .tipoTransacao(transacaoRequest.tipoTransacao())
            .status(StatusTransacao.PENDENTE)
            .dataVencimento(transacaoRequest.dataVencimento())
            .conta(conta)
            .categoria(categoria)
            .build();

    Transacao transacaoSalva = transacaoRepository.save(transacao);
    log.info("Transação criada com sucesso: id={}", transacaoSalva.getId());

    return toResponse(transacaoSalva);
  }

  @Override
  @Transactional(readOnly = true)
  public List<TransacaoResponse> listarPorConta(UUID contaId, UUID usuarioId) {
    contaRepository.findByIdAndUsuarioId(contaId, usuarioId)
            .orElseThrow(() -> new RecursoNaoEncontradoException("Conta não encontrada"));

    log.debug("Listando transações da conta {}", contaId);
    return transacaoRepository.findByContaId(contaId).stream()
            .map(this::toResponse)
            .toList();
  }

  @Override
  @Transactional
  public TransacaoResponse marcarComoPaga(UUID id, UUID usuarioId) {
      Transacao transacao = transacaoRepository.findByIdAndConta_Usuario_Id(id, usuarioId)
              .orElseThrow(() -> new RecursoNaoEncontradoException("Transação não encontrada"));

      transacao.setStatus(StatusTransacao.PAGO);
      transacao.setDataPagamento(LocalDate.now());

      log.info("Transação {} marcada como PAGA", id);

      return toResponse(transacaoRepository.save(transacao));
  }

  //Metodos auxiliar
  private TransacaoResponse toResponse(Transacao transacao){
    return new TransacaoResponse(
            transacao.getId(),
            transacao.getDescricao(),
            transacao.getValor(),
            transacao.getTipoTransacao(),
            transacao.getStatus(),
            transacao.getDataVencimento(),
            transacao.getDataPagamento(),
            transacao.getConta().getNome(),
            transacao.getCategoria().getNome()
    );
  }
}
