package org.example.escenalocal.dtos.get;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class GetEntradaDto {
  private Long id;
  private String tipo;
  private BigDecimal precio;
  private Integer disponibilidad;
}
