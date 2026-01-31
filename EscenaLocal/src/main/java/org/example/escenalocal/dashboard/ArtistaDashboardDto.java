package org.example.escenalocal.dashboard;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ArtistaDashboardDto {

  private ArtistaKpisDto kpis;

  private List<PuntoCantidadDiaDto> entradasPorDia;
  private List<EventoAsistenciaDto> rankingEventos;
  private List<EntradasPorTipoDto> entradasPorTipo;
}
