export interface ProductorDashboardDto {
  kpis: ProductorKpiDto;
  ventasPorDia: PuntoVentaDiaDto[];
  entradasPorTipo: EntradasPorTipoDto[];
  eventosResumen: EventoDashboardDto[];
  ventasRecientes: VentaDashboardDto[];
  topEventos: EventoRankingDto[];
}

export interface ProductorKpiDto {
  totalRecaudado: number;
  entradasVendidas: number;
  eventosActivos: number;
  ocupacionPromedio: number;
  mejorEventoNombre: string | null;
  mejorEventoRecaudacion: number;
}

export interface PuntoVentaDiaDto {
  fecha: string;   // LocalDate -> string
  totalDia: number;
}

export interface EntradasPorTipoDto {
  tipoEntradaNombre: string;
  cantidadVendida: number;
}

export interface EventoDashboardDto {
  eventoId: number;
  nombre: string;
  fecha: string;
  establecimientoNombre: string | null;
  capacidadTotal: number | null;
  entradasVendidas: number;
  porcentajeOcupacion: number;
  recaudacion: number;
}

export interface VentaDashboardDto {
  fechaCompra: string;
  usuarioNombre: string | null;
  usuarioEmail: string | null;
  eventoId: number;
  eventoNombre: string;
  tipoEntradaNombre: string;
  cantidad: number;
  total: number;
  estadoPago: string;
  paymentId: number;
}

export interface EventoRankingDto {
  eventoId: number;
  eventoNombre: string;
  recaudacion: number;
  entradasVendidas: number;
}
