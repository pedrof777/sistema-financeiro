package br.com.pferreira.financeiroservice.model.entity;

import br.com.pferreira.financeiroservice.model.enums.TipoTransacao;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

/**
 * @author Pedro Ferreira
 */

@Entity
@Table(name = "categorias")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Categoria {

  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  private UUID id;

  @Column(nullable = false)
  private String nome;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  private TipoTransacao tipo;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "usuario_id", nullable = false)
  private User usuario;


}
