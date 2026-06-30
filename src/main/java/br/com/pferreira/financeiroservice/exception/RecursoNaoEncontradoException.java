package br.com.pferreira.financeiroservice.exception;

/**
 * @author Pedro Ferreira
 */

public class RecursoNaoEncontradoException extends RuntimeException {
  public RecursoNaoEncontradoException(String message) {
    super(message);
  }
}
