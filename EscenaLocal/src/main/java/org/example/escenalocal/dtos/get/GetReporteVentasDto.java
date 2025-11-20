package org.example.escenalocal.dtos.get;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class GetReporteVentasDto {
  private Long eventoId;
  private String evento;
  private int totalEntradas;
  private double totalRecaudado;
  private Map<String, Integer> entradasPorTipo;
  private List<GetSerieVentasFechaDto> ventasPorFecha;
}
