package br.com.pferreira.financeiroservice;

import br.com.pferreira.financeiroservice.exception.RecursoNaoEncontradoException;
import br.com.pferreira.financeiroservice.model.dto.TransacaoRequest;
import br.com.pferreira.financeiroservice.model.dto.TransacaoResponse;
import br.com.pferreira.financeiroservice.model.entity.Categoria;
import br.com.pferreira.financeiroservice.model.entity.Conta;
import br.com.pferreira.financeiroservice.model.entity.Transacao;
import br.com.pferreira.financeiroservice.model.entity.User;
import br.com.pferreira.financeiroservice.model.enums.Role;
import br.com.pferreira.financeiroservice.model.enums.StatusTransacao;
import br.com.pferreira.financeiroservice.model.enums.TipoConta;
import br.com.pferreira.financeiroservice.model.enums.TipoTransacao;
import br.com.pferreira.financeiroservice.repository.CategoriaRepository;
import br.com.pferreira.financeiroservice.repository.ContaRepository;
import br.com.pferreira.financeiroservice.repository.TransacaoRepository;
import br.com.pferreira.financeiroservice.service.impl.TransacaoServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * @author Pedro Ferreira
 */

@ExtendWith(MockitoExtension.class)
class TransacaoServiceImplTest {

  @Mock private TransacaoRepository transacaoRepository;
  @Mock private CategoriaRepository categoriaRepository;
  @Mock private ContaRepository contaRepository;


  @InjectMocks
  private TransacaoServiceImpl transacaoService;

  private User usuario;
  private UUID usuarioId;
  private Conta conta;
  private Categoria categoria;
  private UUID contaId;
  private UUID categoriaId;


  @BeforeEach
  void setUp(){
    usuarioId = UUID.randomUUID();
    contaId = UUID.randomUUID();
    categoriaId = UUID.randomUUID();

    usuario = User.builder()
            .id(usuarioId)
            .name("Joao")
            .email("j@email.com")
            .password("senha")
            .role(Role.USER)
            .build();

    conta = Conta.builder()
            .id(contaId)
            .nome("Conta corrente")
            .tipo(TipoConta.CORRENTE)
            .saldoInicial(BigDecimal.valueOf(1000))
            .usuario(usuario)
            .build();

    categoria = Categoria.builder()
            .id(categoriaId)
            .nome("Alimentação")
            .tipo(TipoTransacao.DESPESA)
            .usuario(usuario)
            .build();
  }

  @Test
  void criar_deveSalvarTransacaoComStatusPendente(){
    TransacaoRequest transacaoRequest = new TransacaoRequest(
            "Mercado",BigDecimal.valueOf(150),TipoTransacao.DESPESA,
            LocalDate.now().plusDays(5), contaId, categoriaId);

    Transacao transacaoSalva = Transacao.builder()
            .id(UUID.randomUUID())
            .tipoTransacao(transacaoRequest.tipoTransacao())
            .categoria(categoria)
            .descricao(transacaoRequest.descricao())
            .valor(transacaoRequest.valor())
            .status(StatusTransacao.PENDENTE)
            .conta(conta)
            .dataVencimento(transacaoRequest.dataVencimento())
            .build();

    when(contaRepository.findByIdAndUsuarioId(contaId, usuarioId)).thenReturn(Optional.of(conta));
    when(categoriaRepository.findByIdAndUsuarioId(categoriaId, usuarioId)).thenReturn(Optional.of(categoria));
    when(transacaoRepository.save(any(Transacao.class))).thenReturn(transacaoSalva);

    TransacaoResponse transacaoResponse = transacaoService.criar(transacaoRequest, usuarioId);

    assertEquals("Mercado", transacaoResponse.descricao());
    assertEquals(StatusTransacao.PENDENTE, transacaoResponse.statusTransacao());
    assertEquals("Conta corrente", transacaoResponse.nomeConta());
    assertEquals("Alimentação", transacaoResponse.nomeCategoria());
    verify(transacaoRepository).save(any(Transacao.class));
  }

  @Test
  void criar_deveLancarExcecao_quandoContaNaoPertenceAoUsuario(){
    TransacaoRequest transacaoRequest = new TransacaoRequest("Mercado", BigDecimal.valueOf(150),
            TipoTransacao.DESPESA, LocalDate.now(), contaId, categoriaId);

    when(contaRepository.findByIdAndUsuarioId(contaId, usuarioId)).thenReturn(Optional.empty());

    assertThrows(RecursoNaoEncontradoException.class,
            () -> transacaoService.criar(transacaoRequest, usuarioId));

    verify(transacaoRepository, never()).save(any());
  }

  @Test
  void criar_deveLancarExcecao_quandoCategoriaNaoPertenceAoUsuario(){
    TransacaoRequest transacaoRequest = new TransacaoRequest("Mercado", BigDecimal.valueOf(150),
            TipoTransacao.DESPESA, LocalDate.now(), contaId, categoriaId);

    when(contaRepository.findByIdAndUsuarioId(contaId, usuarioId)).thenReturn(Optional.of(conta));
    when(categoriaRepository.findByIdAndUsuarioId(categoriaId, usuarioId)).thenReturn(Optional.empty());

    assertThrows(RecursoNaoEncontradoException.class,
            () -> transacaoService.criar(transacaoRequest, usuarioId));

    verify(transacaoRepository, never()).save(any());
  }

  @Test
  void marcarComoPaga_deveAtualizarStatusEDataDaPagamento(){
    UUID transacaoId = UUID.randomUUID();
    Transacao transacaoPendente = Transacao.builder()
            .id(transacaoId).descricao("Aluguel")
            .valor(BigDecimal.valueOf(800)).tipoTransacao(TipoTransacao.DESPESA)
            .status(StatusTransacao.PENDENTE)
            .dataVencimento(LocalDate.now())
            .conta(conta).categoria(categoria)
            .build();

    when(transacaoRepository.findByIdAndConta_Usuario_Id(transacaoId, usuarioId))
            .thenReturn(Optional.of(transacaoPendente));
    when(transacaoRepository.save(any(Transacao.class)))
            .thenAnswer(inv -> inv.getArgument(0));

    TransacaoResponse transacaoResponse = transacaoService.marcarComoPaga(transacaoId, usuarioId);

    assertEquals(StatusTransacao.PAGO, transacaoResponse.statusTransacao());
    assertNotNull(transacaoResponse.dataPagamento());
    assertEquals(LocalDate.now(), transacaoResponse.dataPagamento());
  }

  @Test
  void marcarComoPaga_deveLancarExecao_quandoTransacaoNaoEncontrada(){
    UUID transacaoId = UUID.randomUUID();
    when(transacaoRepository.findByIdAndConta_Usuario_Id(transacaoId,usuarioId)).thenReturn(Optional.empty());

    assertThrows(RecursoNaoEncontradoException.class,
            () -> transacaoService.marcarComoPaga(transacaoId, usuarioId));

  }

  @Test
  void listarPorConta_deveRetornarTransacaoDaConta(){
    Transacao transacao = Transacao.builder()
            .id(UUID.randomUUID()).descricao("Supermercado")
            .valor(BigDecimal.valueOf(200)).tipoTransacao(TipoTransacao.DESPESA)
            .status(StatusTransacao.PENDENTE)
            .dataVencimento(LocalDate.now())
            .conta(conta).categoria(categoria)
            .build();

    when(contaRepository.findByIdAndUsuarioId(contaId, usuarioId)).thenReturn(Optional.of(conta));
    when(transacaoRepository.findByContaId(contaId)).thenReturn(List.of(transacao));

    List<TransacaoResponse> transacaoResponses = transacaoService.listarPorConta(contaId, usuarioId);

    assertEquals(1, transacaoResponses.size());
    assertEquals("Supermercado", transacaoResponses.getFirst().descricao());
  }

}
