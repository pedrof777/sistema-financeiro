package br.com.pferreira.financeiroservice.model.entity;

import br.com.pferreira.financeiroservice.model.enums.StatusTransacao;
import br.com.pferreira.financeiroservice.model.enums.TipoTransacao;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

/**
 * @author Pedro Ferreira
 */

@Entity
@Table(name = "transacoes")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Transacao {

  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  private UUID id;

  @Column(nullable = false)
  private String descricao;

  @Column(nullable = false, precision = 15, scale = 2)
  private BigDecimal valor;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  private TipoTransacao tipoTransacao;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  private StatusTransacao status;

  @Column(nullable = false)
  private LocalDate dataVencimento;

  private LocalDate dataPagamento;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "conta_id", nullable = false)
  private Conta conta;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "categoria_id", nullable = false)
  private Categoria categoria;
}
