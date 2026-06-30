package br.com.pferreira.financeiroservice.controller;

import br.com.pferreira.financeiroservice.model.dto.TransacaoRequest;
import br.com.pferreira.financeiroservice.model.dto.TransacaoResponse;
import br.com.pferreira.financeiroservice.model.entity.User;
import br.com.pferreira.financeiroservice.service.TransacaoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
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
@RequestMapping("/transacoes")
@RequiredArgsConstructor
public class TransacaoController {

  private final TransacaoService transacaoService;

  @PostMapping
  public ResponseEntity<TransacaoResponse> criar(
          @RequestBody @Valid TransacaoRequest transacaoRequest,
          @AuthenticationPrincipal User usuario){

    TransacaoResponse transacaoResponse = transacaoService.criar(transacaoRequest, usuario.getId());
    return ResponseEntity.status(HttpStatus.CREATED).body(transacaoResponse);
  }

  @GetMapping
  public ResponseEntity<List<TransacaoResponse>> listarPorConta(
          @RequestParam UUID contaId,
          @AuthenticationPrincipal User usuario){

    return ResponseEntity.ok(transacaoService.listarPorConta(contaId, usuario.getId()));
  }

  @PatchMapping("/{id}/pagar")
  public ResponseEntity<TransacaoResponse> marcarComoPaga(
          @PathVariable UUID id,
          @AuthenticationPrincipal User usuario){

    return ResponseEntity.ok(transacaoService.marcarComoPaga(id,usuario.getId()));
  }
}
