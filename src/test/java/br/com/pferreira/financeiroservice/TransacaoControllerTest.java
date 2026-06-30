package br.com.pferreira.financeiroservice;

import br.com.pferreira.financeiroservice.controller.TransacaoController;
import br.com.pferreira.financeiroservice.model.dto.TransacaoRequest;
import br.com.pferreira.financeiroservice.model.dto.TransacaoResponse;
import br.com.pferreira.financeiroservice.model.entity.User;
import br.com.pferreira.financeiroservice.model.enums.Role;
import br.com.pferreira.financeiroservice.model.enums.StatusTransacao;
import br.com.pferreira.financeiroservice.model.enums.TipoTransacao;
import br.com.pferreira.financeiroservice.service.JwtService;
import br.com.pferreira.financeiroservice.service.TransacaoService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.RequestPostProcessor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * @author Pedro Ferreira
 */

@WebMvcTest(TransacaoController.class)
//@AutoConfigureMockMvc
@AutoConfigureMockMvc(addFilters = false)
class TransacaoControllerTest {

  @Autowired private MockMvc mockMvc;

  @MockitoBean private TransacaoService transacaoService;
  @MockitoBean private JwtService jwtService;
  @MockitoBean private AuthenticationProvider authenticationProvider;
  @MockitoBean private UserDetailsService userDetailsService;

  private ObjectMapper objectMapper;
  private User usuario;
  private UUID usuarioId;

  @BeforeEach
  void setUp(){
    objectMapper = new ObjectMapper();
    objectMapper.registerModule(new JavaTimeModule());

    usuarioId = UUID.randomUUID();
    usuario = User.builder()
            .id(usuarioId)
            .name("Joao")
            .email("j@email.com")
            .password("senha")
            .role(Role.USER)
            .build();
  }


  @Test
  void criar_deveRetonar201_quandoRequesteValido() throws Exception{
    var request = new TransacaoRequest(
            "Mercado", BigDecimal.valueOf(150), TipoTransacao.DESPESA,
            LocalDate.now().plusDays(5), UUID.randomUUID(), UUID.randomUUID());

    var response = new TransacaoResponse(
            UUID.randomUUID(), "Mercado", BigDecimal.valueOf(150),TipoTransacao.DESPESA,
            StatusTransacao.PENDENTE,  request.dataVencimento(), null,
            "Conta corrente", "Alimentação");

    when(transacaoService.criar(any(), eq(usuarioId))).thenReturn(response);

    mockMvc.perform(post("/transacoes")
              .with(comUsuarioAutenticado())
              .contentType(MediaType.APPLICATION_JSON)
              .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.descricao").value("Mercado"))
            .andExpect(jsonPath("$.statusTransacao").value("PENDENTE"));
  }

  @Test
  void criar_deveRetornar400_quandoValorNegativo() throws Exception{
    var request = new TransacaoRequest(
            "Mercado", BigDecimal.valueOf(-50),TipoTransacao.DESPESA,
            LocalDate.now(), UUID.randomUUID(), UUID.randomUUID());

    mockMvc.perform(post("/transacoes")
            .with(comUsuarioAutenticado())
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isBadRequest());

  }

  @Test
  void criar_deveRetornar400_quandoDescricaoVazia() throws Exception{
    var request = new TransacaoRequest(
            "", BigDecimal.valueOf(150), TipoTransacao.DESPESA,
            LocalDate.now(), UUID.randomUUID(), UUID.randomUUID());

    mockMvc.perform(post("/transacoes")
            .with(comUsuarioAutenticado())
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isBadRequest());

  }

  @Test
  void ListarPorConta_deveRetornar200ComLista() throws Exception{
    UUID contaId = UUID.randomUUID();
    var response = new TransacaoResponse(
            UUID.randomUUID(), "Supermercado", BigDecimal.valueOf(200), TipoTransacao.DESPESA,
            StatusTransacao.PENDENTE, LocalDate.now(), null, "conta corrente", "Alimentação");

    when(transacaoService.listarPorConta(eq(contaId), eq(usuarioId))).thenReturn(List.of(response));

    mockMvc.perform(get("/transacoes")
            .with(comUsuarioAutenticado())
            .param("contaId", contaId.toString()))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$[0].descricao").value("Supermercado"));


  }

  @Test
  void marcarComoPaga_deveRetornar200ComStatusAtualizado()throws Exception{
    UUID transacaoId = UUID.randomUUID();
    var response = new TransacaoResponse(
            transacaoId, "Aluguel", BigDecimal.valueOf(800), TipoTransacao.DESPESA,
            StatusTransacao.PAGO, LocalDate.now(), LocalDate.now(), "Conta corrente", "Moradia");

    when(transacaoService.marcarComoPaga(eq(transacaoId), eq(usuarioId))).thenReturn(response);

    mockMvc.perform(patch("/transacoes/{id}/pagar", transacaoId)
            .with(comUsuarioAutenticado()))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.statusTransacao").value("PAGO"));
  }


  //Metodos auxiliares
  private RequestPostProcessor comUsuarioAutenticado() {
    return request -> {
      SecurityContext context = SecurityContextHolder.createEmptyContext();
      context.setAuthentication(
              new org.springframework.security.authentication.UsernamePasswordAuthenticationToken(
                      usuario, null, usuario.getAuthorities()));
      SecurityContextHolder.setContext(context);
      return request;
    };
  }

}
