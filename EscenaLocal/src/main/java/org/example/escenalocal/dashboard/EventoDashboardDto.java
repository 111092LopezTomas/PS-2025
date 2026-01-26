package org.example.escenalocal.dashboard;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EventoDashboardDto {

  private Long eventoId;
  private String nombre;
  private LocalDate fecha;
  private String establecimientoNombre;
  private Integer capacidadTotal;
  private Long entradasVendidas;
  private Double porcentajeOcupacion;
  private BigDecimal recaudacion;
}
