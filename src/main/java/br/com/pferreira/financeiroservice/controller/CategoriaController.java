package br.com.pferreira.financeiroservice.controller;

import br.com.pferreira.financeiroservice.model.dto.CategoriaRequest;
import br.com.pferreira.financeiroservice.model.dto.CategoriaResponse;
import br.com.pferreira.financeiroservice.model.entity.User;
import br.com.pferreira.financeiroservice.service.CategoriaService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author Pedro Ferreira
 */

@RestController
@RequestMapping("/categorias")
@RequiredArgsConstructor
public class CategoriaController {

  private final CategoriaService categoriaService;

  @PostMapping
  public ResponseEntity<CategoriaResponse> criar(
          @RequestBody @Valid CategoriaRequest categoriaRequest,
          @AuthenticationPrincipal User usuario){

    CategoriaResponse categoriaResponse = categoriaService.criar(categoriaRequest, usuario.getId());
    return ResponseEntity.status(HttpStatus.CREATED).body(categoriaResponse);
  }

  @GetMapping
  public ResponseEntity<List<CategoriaResponse>> listar(@AuthenticationPrincipal User usuario){
    return ResponseEntity.ok(categoriaService.listarPorUsuario(usuario.getId()));
  }
}
