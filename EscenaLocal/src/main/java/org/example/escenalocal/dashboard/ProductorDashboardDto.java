package org.example.escenalocal.dashboard;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProductorDashboardDto {

  private ProductorKpiDto kpis;
  private List<PuntoVentaDiaDto> ventasPorDia;
  private List<EntradasPorTipoDto> entradasPorTipo;
  private List<EventoDashboardDto> eventosResumen;
  private List<VentaDashboardDto> ventasRecientes;
  private List<EventoRankingDto> topEventos;
}
