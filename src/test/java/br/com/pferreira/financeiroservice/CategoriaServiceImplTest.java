package br.com.pferreira.financeiroservice;

import br.com.pferreira.financeiroservice.exception.RecursoNaoEncontradoException;
import br.com.pferreira.financeiroservice.model.dto.CategoriaRequest;
import br.com.pferreira.financeiroservice.model.dto.CategoriaResponse;
import br.com.pferreira.financeiroservice.model.entity.Categoria;
import br.com.pferreira.financeiroservice.model.entity.User;
import br.com.pferreira.financeiroservice.model.enums.Role;
import br.com.pferreira.financeiroservice.model.enums.TipoTransacao;
import br.com.pferreira.financeiroservice.repository.CategoriaRepository;
import br.com.pferreira.financeiroservice.repository.UserRepository;
import br.com.pferreira.financeiroservice.service.impl.CategoriaServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

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
public class CategoriaServiceImplTest {

  @Mock private CategoriaRepository categoriaRepository;
  @Mock private UserRepository userRepository;

  @InjectMocks
  private CategoriaServiceImpl categoriaServiceImpl;

  private User usuario;
  private UUID usuarioId;

  @BeforeEach
  void setUp(){
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
  void criar_deveSalvarCategoriaComSucesso(){
    CategoriaRequest categoriaRequest = new CategoriaRequest("Alimentação", TipoTransacao.DESPESA );
    Categoria categoriaSalva = Categoria.builder()
            .id(UUID.randomUUID())
            .nome(categoriaRequest.nome())
            .tipo(categoriaRequest.tipoTransacao())
            .usuario(usuario)
            .build();

    when(userRepository.findById(usuarioId)).thenReturn(Optional.of(usuario));
    when(categoriaRepository.save(any(Categoria.class))).thenReturn(categoriaSalva);

    CategoriaResponse categoriaResponse = categoriaServiceImpl.criar(categoriaRequest, usuarioId);

    assertEquals("Alimentação", categoriaResponse.nome());
    assertEquals(TipoTransacao.DESPESA, categoriaResponse.tipoTransacao());
    verify(categoriaRepository).save(any(Categoria.class));
  }

  @Test
  void criar_deveLancarExcecao_quandoUsuarioNaoExiste(){
    CategoriaRequest categoriaRequest = new CategoriaRequest("Salário", TipoTransacao.RECEITA);
    when(userRepository.findById(usuarioId)).thenReturn(Optional.empty());

    assertThrows(RecursoNaoEncontradoException.class,
            () -> categoriaServiceImpl.criar(categoriaRequest, usuarioId));

    verify(categoriaRepository, never()).save(any());

  }

  @Test
  void listarPorUsuario_deveRetornarListaDeCategorias(){
    Categoria categoria1 = Categoria.builder()
            .id(UUID.randomUUID())
            .nome("Transporte")
            .tipo(TipoTransacao.DESPESA)
            .usuario(usuario)
            .build();

    Categoria categoria2 = Categoria.builder()
            .id(UUID.randomUUID())
            .nome("Freelancer")
            .tipo(TipoTransacao.RECEITA)
            .usuario(usuario)
            .build();

    when(categoriaRepository.findByUsuarioId(usuarioId)).thenReturn(List.of(categoria1, categoria2));

    List<CategoriaResponse> resultado = categoriaServiceImpl.listarPorUsuario(usuarioId);

    assertEquals(2, resultado.size());
    assertEquals("Transporte", resultado.get(0).nome());
    assertEquals("Freelancer", resultado.get(1).nome());

  }

  @Test
  void ListarPorUsuario_deveRetornarListaVazia_quandoUsuarioNaoTemCategorias(){
    when(categoriaRepository.findByUsuarioId(usuarioId)).thenReturn(List.of());

    List<CategoriaResponse> categoriaResponses = categoriaServiceImpl.listarPorUsuario(usuarioId);

    assertTrue(categoriaResponses.isEmpty());
  }

}
