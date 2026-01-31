package org.example.escenalocal.dashboard;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ArtistaKpisDto {

  private Long totalEntradas;
  private Long totalEventos;
  private Double promedioEntradasPorEvento;
  private String mejorEventoNombre;
  private Long mejorEventoEntradas;
}
