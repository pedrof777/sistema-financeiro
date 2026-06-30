package br.com.pferreira.financeiroservice.model.entity;

import br.com.pferreira.financeiroservice.model.enums.TipoConta;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.UUID;

/**
 * @author Pedro Ferreira
 */

@Entity
@Table(name = "contas")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Conta {

  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  private UUID id;

  @Column(nullable = false)
  private String nome;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  private TipoConta tipo;

  @Column(nullable = false, precision = 15, scale = 2)
  private BigDecimal saldoInicial;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "usuario_id", nullable = false)
  private User usuario;
}
