package org.example.escenalocal.dashboard;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
public class PuntoVentaDiaDto {

  private LocalDate fecha;
  private BigDecimal totalDia;

  public PuntoVentaDiaDto(LocalDateTime fechaVenta, BigDecimal totalDia) {
    this.fecha = (fechaVenta != null) ? fechaVenta.toLocalDate() : null;
    this.totalDia = (totalDia != null) ? totalDia : BigDecimal.ZERO;
  }
}
