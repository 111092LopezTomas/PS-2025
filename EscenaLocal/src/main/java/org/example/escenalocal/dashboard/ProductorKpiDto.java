package org.example.escenalocal.dashboard;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProductorKpiDto {

  private BigDecimal totalRecaudado;
  private Long entradasVendidas;
  private Integer eventosActivos;
  private Double ocupacionPromedio;
  private String mejorEventoNombre;
  private BigDecimal mejorEventoRecaudacion;
}
