package org.example.escenalocal.dashboard;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EventoRankingDto {

  private Long eventoId;
  private String eventoNombre;
  private BigDecimal recaudacion;
  private Long entradasVendidas;

  public EventoRankingDto(Long eventoId,
                          String eventoNombre,
                          Long recaudacion,
                          Long entradasVendidas) {
    this.eventoId = eventoId;
    this.eventoNombre = eventoNombre;
    // convertimos el Long a BigDecimal
    this.recaudacion = (recaudacion != null)
      ? BigDecimal.valueOf(recaudacion)
      : BigDecimal.ZERO;
    this.entradasVendidas = entradasVendidas;
  }
}
