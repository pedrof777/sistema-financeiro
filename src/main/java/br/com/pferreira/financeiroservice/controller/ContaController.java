package br.com.pferreira.financeiroservice.controller;

import br.com.pferreira.financeiroservice.model.dto.ContaRequest;
import br.com.pferreira.financeiroservice.model.dto.ContaResponse;
import br.com.pferreira.financeiroservice.model.entity.User;
import br.com.pferreira.financeiroservice.service.ContaService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.apache.coyote.Response;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

/**
 * @author Pedro Ferreira
 */

@RestController
@RequestMapping("/contas")
@RequiredArgsConstructor
public class ContaController {

  private final ContaService contaService;

  @PostMapping
  public ResponseEntity<ContaResponse> criar(
          @RequestBody @Valid ContaRequest contaRequest,
          @AuthenticationPrincipal User usuario){

    ContaResponse contaResponse = contaService.criar(contaRequest, usuario.getId());
    return ResponseEntity.status(HttpStatus.CREATED).body(contaResponse);
  }

  @GetMapping
  public ResponseEntity<List<ContaResponse>> listar(@AuthenticationPrincipal User usuario){
    return ResponseEntity.ok(contaService.listarPorUsuario(usuario.getId()));
  }

  @GetMapping("/{id}")
  public ResponseEntity<ContaResponse> buscarPorId(
          @PathVariable UUID id,
          @AuthenticationPrincipal User usuario
          ){

    return ResponseEntity.ok(contaService.buscarPorId(id, usuario.getId()));
  }
}
