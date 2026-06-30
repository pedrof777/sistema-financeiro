package br.com.pferreira.financeiroservice.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Pedro Ferreira
 */

@ControllerAdvice
public class GlobalExceptionHandler{

  @ExceptionHandler(RecursoNaoEncontradoException.class)
  public ResponseEntity<Map<String, Object>> handleNotFound(RecursoNaoEncontradoException ex){
    return buildErroResponse(HttpStatus.NOT_FOUND, ex.getMessage());
  }

  @ExceptionHandler(MethodArgumentNotValidException.class)
  public ResponseEntity<Map<String, Object>> handleValidation(MethodArgumentNotValidException ex){
    Map<String, String> errors = new HashMap<>();
    ex.getBindingResult().getFieldErrors().forEach(error ->
            errors.put(error.getField(), error.getDefaultMessage())
    );
    Map<String, Object> response = new HashMap<>();
    response.put("timestamp", LocalDateTime.now());
    response.put("status", HttpStatus.BAD_REQUEST.value());
    response.put("errors", errors);
    return ResponseEntity.badRequest().body(response);
  }

  @ExceptionHandler(IllegalArgumentException.class)
  public ResponseEntity<Map<String, Object>> handleIllegalArgument(IllegalArgumentException ex){
    return buildErroResponse(HttpStatus.BAD_REQUEST, ex.getMessage());
  }


  //Metodos auxiliar

  private ResponseEntity<Map<String, Object>> buildErroResponse(HttpStatus status, String message){
    Map<String , Object> response = new HashMap<>();
    response.put("timestamp", LocalDateTime.now());
    response.put("status", status.value());
    response.put("message", message);
    return ResponseEntity.status(status).body(response);
  }

}
