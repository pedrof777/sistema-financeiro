package br.com.pferreira.financeiroservice;

import br.com.pferreira.financeiroservice.exception.RecursoNaoEncontradoException;
import br.com.pferreira.financeiroservice.model.dto.ContaRequest;
import br.com.pferreira.financeiroservice.model.dto.ContaResponse;
import br.com.pferreira.financeiroservice.model.entity.Conta;
import br.com.pferreira.financeiroservice.model.entity.Transacao;
import br.com.pferreira.financeiroservice.model.entity.User;
import br.com.pferreira.financeiroservice.model.enums.Role;
import br.com.pferreira.financeiroservice.model.enums.StatusTransacao;
import br.com.pferreira.financeiroservice.model.enums.TipoConta;
import br.com.pferreira.financeiroservice.model.enums.TipoTransacao;
import br.com.pferreira.financeiroservice.repository.ContaRepository;
import br.com.pferreira.financeiroservice.repository.TransacaoRepository;
import br.com.pferreira.financeiroservice.repository.UserRepository;
import br.com.pferreira.financeiroservice.service.impl.ContaServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

/**
 * @author Pedro Ferreira
 */

@ExtendWith(MockitoExtension.class)
class ContaServiceImplTest {

  @Mock private ContaRepository contaRepository;
  @Mock private TransacaoRepository transacaoRepository;
  @Mock private UserRepository userRepository;

  @InjectMocks
  private ContaServiceImpl contaService;

  private User usuario;
  private UUID usuarioId;

  @BeforeEach
  void setUp(){
    usuarioId = UUID.randomUUID();
    usuario = User.builder()
            .id(usuarioId)
            .name("Joao")
            .email("j@email.com")
            .password("hash")
            .role(Role.USER)
            .build();
  }


  @Test
  void criar_deveSalvarContaComSucesso(){
    ContaRequest contaRequest = new ContaRequest("Conta corrente", TipoConta.CORRENTE, BigDecimal.valueOf(1000));
    Conta contaSalva = Conta.builder()
            .id(UUID.randomUUID())
            .nome(contaRequest.nome())
            .tipo(contaRequest.tipoConta())
            .saldoInicial(contaRequest.saldoInicial())
            .usuario(usuario)
            .build();

    when(userRepository.findById(usuarioId)).thenReturn(Optional.of(usuario));
    when(contaRepository.save(any(Conta.class))).thenReturn(contaSalva);
    when(transacaoRepository.findByContaIdAndStatus(any(), eq(StatusTransacao.PAGO))).thenReturn(List.of());

    ContaResponse contaResponse = contaService.criar(contaRequest, usuarioId);

    assertEquals("Conta corrente", contaResponse.nome());
    assertEquals(BigDecimal.valueOf(1000), contaResponse.saldoAtual());
    verify(contaRepository).save(any(Conta.class));
  }

  @Test
  void criar_deveLancarExcecao_quandoUsuarioNaoExiste(){
    ContaRequest contaRequest = new ContaRequest("Conta", TipoConta.CORRENTE, BigDecimal.TEN);
    when(userRepository.findById(usuarioId)).thenReturn(Optional.empty());

    assertThrows(RecursoNaoEncontradoException.class,
            () -> contaService.criar(contaRequest, usuarioId));

    verify(contaRepository, never()).save(any());
  }

  @Test
  void buscarPorId_deveRetornarConta_quandoPertenceAoUsuario(){
    UUID contaId = UUID.randomUUID();
    Conta conta = Conta.builder()
            .id(contaId)
            .nome("Carteira")
            .tipo(TipoConta.CARTEIRA)
            .saldoInicial(BigDecimal.valueOf(500))
            .usuario(usuario)
            .build();

    when(contaRepository.findByIdAndUsuarioId(contaId, usuarioId)).thenReturn(Optional.of(conta));
    when((transacaoRepository.findByContaIdAndStatus(contaId, StatusTransacao.PAGO))).thenReturn(List.of());

    ContaResponse contaResponse = contaService.buscarPorId(contaId, usuarioId);

    assertEquals("Carteira", contaResponse.nome());
  }

  @Test
  void buscarPorId_deveLancarExcecao_quandoContaNaoPertenceAoUsuario(){
    UUID contaId = UUID.randomUUID();
    when(contaRepository.findByIdAndUsuarioId(contaId, usuarioId)).thenReturn(Optional.empty());

    assertThrows(RecursoNaoEncontradoException.class,
            () -> contaService.buscarPorId(contaId, usuarioId));
  }

  @Test
  void calcularSaldoAtual_deveSomarReceitasESubtrairDespesasPagas(){
    UUID contaId = UUID.randomUUID();
    Conta conta = Conta.builder()
            .id(contaId)
            .nome("Conta Teste")
            .tipo(TipoConta.CORRENTE)
            .saldoInicial(BigDecimal.valueOf(100))
            .usuario(usuario)
            .build();

    Transacao receita = Transacao.builder()
            .tipoTransacao(TipoTransacao.RECEITA)
            .valor(BigDecimal.valueOf(200))
            .status(StatusTransacao.PAGO)
            .build();

    Transacao despesa = Transacao.builder()
            .tipoTransacao(TipoTransacao.DESPESA)
            .valor(BigDecimal.valueOf(50))
            .status(StatusTransacao.PAGO)
            .build();

    when(contaRepository.findByIdAndUsuarioId(contaId, usuarioId)).thenReturn(Optional.of(conta));
    when(transacaoRepository.findByContaIdAndStatus(contaId, StatusTransacao.PAGO)).thenReturn(List.of(receita, despesa));

    ContaResponse contaResponse = contaService.buscarPorId(contaId, usuarioId);

    assertEquals(BigDecimal.valueOf(250), contaResponse.saldoAtual());
  }

  @Test
  void listarPorUsuario_deveRetornarListaDeContas(){
    Conta conta = Conta.builder()
            .id(UUID.randomUUID())
            .nome("Poupança")
            .tipo(TipoConta.POUPANCA)
            .saldoInicial(BigDecimal.valueOf(300))
            .usuario(usuario)
            .build();
    when(contaRepository.findByUsuarioId(usuarioId)).thenReturn(List.of(conta));
    when(transacaoRepository.findByContaIdAndStatus(any(), eq(StatusTransacao.PAGO))).thenReturn(List.of());

    List<ContaResponse> resultado = contaService.listarPorUsuario(usuarioId);

    assertEquals(1, resultado.size());
    assertEquals("Poupança", resultado.getFirst().nome());
  }
}
