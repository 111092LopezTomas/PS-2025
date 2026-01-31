package org.example.escenalocal.services.impl;

import lombok.RequiredArgsConstructor;
import org.example.escenalocal.dashboard.*;
import org.example.escenalocal.entities.EventoEntity;
import org.example.escenalocal.entities.EventoTiposEntradaEntity;
import org.example.escenalocal.entities.VentaEntradaEntity;
import org.example.escenalocal.repositories.EventoRepository;
import org.example.escenalocal.repositories.VentaEntradaRepository;
import org.example.escenalocal.services.DashboardService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DashboardServiceImpl implements DashboardService {

  private final VentaEntradaRepository ventaRepo;
  private final EventoRepository eventoRepo;

  @Override
  @Transactional(readOnly = true)
  public ProductorDashboardDto getDashboardProductor(Long productorId, LocalDate from, LocalDate to) {

    LocalDateTime fromDateTime = from.atStartOfDay();
    LocalDateTime toDateTime   = to.atTime(LocalTime.MAX);

    ProductorDashboardDto dto = new ProductorDashboardDto();

    // 1) KPIs básicos
    ProductorKpiDto kpis = buildKpis(productorId, from, to, fromDateTime, toDateTime);
    dto.setKpis(kpis);

    // 2) Gráfico: ventas por día
    dto.setVentasPorDia(
      ventaRepo.ventasPorDia(productorId, fromDateTime, toDateTime)
    );

    // 3) Gráfico: entradas por tipo
    dto.setEntradasPorTipo(
      ventaRepo.entradasPorTipo(productorId, fromDateTime, toDateTime)
    );

    // 4) Ranking de eventos
    dto.setTopEventos(
      ventaRepo.rankingEventos(productorId, fromDateTime, toDateTime)
    );

    // 5) Ventas en el período (las usamos para tabla + agregados por evento)
    List<VentaEntradaEntity> ventasPeriodo =
      ventaRepo.ventasPorProductorEnPeriodo(productorId, fromDateTime, toDateTime);

    dto.setVentasRecientes(
      ventasPeriodo.stream()
        .limit(20)
        .map(this::mapVentaToVentaDashboardDto)
        .collect(Collectors.toList())
    );

    // ✅ 5.b) Agregados por evento (solo pagos aprobados)
    Map<Long, Long> entradasPorEvento = new HashMap<>();
    Map<Long, BigDecimal> recaudacionPorEvento = new HashMap<>();

    for (VentaEntradaEntity v : ventasPeriodo) {
      // ignorar pagos no aprobados
      if (v.getEstadoPago() == null ||
        !"approved".equalsIgnoreCase(v.getEstadoPago())) {
        continue;
      }

      if (v.getTipoEntradaEvento() == null ||
        v.getTipoEntradaEvento().getEvento() == null) {
        continue;
      }

      Long eventoId = v.getTipoEntradaEvento().getEvento().getId();
      if (eventoId == null) continue;

      // sumar entradas
      entradasPorEvento.merge(
        eventoId,
        (long) v.getCantidad(),
        Long::sum
      );

      // sumar recaudación
      BigDecimal monto = v.getMontoTotal(); // precioUnitario * cantidad
      if (monto == null) {
        monto = BigDecimal.ZERO;
      }
      recaudacionPorEvento.merge(
        eventoId,
        monto,
        BigDecimal::add
      );
    }

    // 6) Resumen de eventos usando los agregados
    List<EventoEntity> eventos = eventoRepo.eventosDelProductorEnPeriodo(productorId, from, to);

    Map<Long, EventoEntity> eventosPorId = eventos.stream()
      .filter(e -> e.getId() != null)
      .collect(Collectors.toMap(EventoEntity::getId, e -> e));

    dto.setEventosResumen(
      eventos.stream()
        .map(e -> {
          Long entradas = entradasPorEvento.getOrDefault(e.getId(), 0L);
          BigDecimal recaudacion = recaudacionPorEvento.getOrDefault(e.getId(), BigDecimal.ZERO);
          return mapEventoToEventoDashboardDto(e, entradas, recaudacion);
        })
        .collect(Collectors.toList())
    );

    // === 🔥 NUEVO: usar los mismos agregados para KPIs ===

    // 1) Ocupación promedio
    long capacidadTotalEventos = 0L;
    long entradasTotalesEventos = 0L;

    for (EventoEntity e : eventos) {
      Integer capacidad = null;
      if (e.getEstablecimiento() != null) {
        capacidad = e.getEstablecimiento().getCapacidad(); // ajusta el nombre del campo
      }
      if (capacidad != null && capacidad > 0) {
        capacidadTotalEventos += capacidad;
        Long entradas = entradasPorEvento.getOrDefault(e.getId(), 0L);
        entradasTotalesEventos += entradas;
      }
    }

    double ocupacionPromedio = 0.0;
    if (capacidadTotalEventos > 0) {
      ocupacionPromedio = (double) entradasTotalesEventos / capacidadTotalEventos;
    }
    kpis.setOcupacionPromedio(ocupacionPromedio); // 0–1, el front multiplica por 100

    // 2) Mejor evento (por recaudación)
    String mejorEventoNombre = null;
    BigDecimal mejorEventoRecaudacion = BigDecimal.ZERO;

    for (Map.Entry<Long, BigDecimal> entry : recaudacionPorEvento.entrySet()) {
      Long eventoId = entry.getKey();
      BigDecimal rec = entry.getValue() != null ? entry.getValue() : BigDecimal.ZERO;

      if (rec.compareTo(mejorEventoRecaudacion) > 0) {
        mejorEventoRecaudacion = rec;
        EventoEntity ev = eventosPorId.get(eventoId);
        if (ev != null) {
          mejorEventoNombre = ev.getEvento(); // o ev.getNombre(), según tu campo
        }
      }
    }

    kpis.setMejorEventoNombre(mejorEventoNombre);
    kpis.setMejorEventoRecaudacion(mejorEventoRecaudacion);

    // guardar KPIs actualizados en el DTO final
    dto.setKpis(kpis);

    return dto;
  }

  /**
   * Construye los KPIs usando solo repositorios (sin stream raros).
   */
  private ProductorKpiDto buildKpis(Long productorId,
                                    LocalDate from,
                                    LocalDate to,
                                    LocalDateTime fromDateTime,
                                    LocalDateTime toDateTime) {

    ProductorKpiDto kpis = new ProductorKpiDto();

    // Total recaudado en el período
    BigDecimal totalRecaudado =
      Optional.ofNullable(
        ventaRepo.totalRecaudadoPorProductor(productorId, fromDateTime, toDateTime)
      ).orElse(BigDecimal.ZERO);

    // Total entradas vendidas en el período
    Long entradasVendidas =
      Optional.ofNullable(
        ventaRepo.totalEntradasVendidasPorProductor(productorId, fromDateTime, toDateTime)
      ).orElse(0L);

    // Cantidad de eventos activos en el período
    int eventosActivos =
      Optional.ofNullable(
        eventoRepo.countEventosDelProductorEnPeriodo(productorId, from, to)
      ).orElse(0);

    kpis.setTotalRecaudado(totalRecaudado);
    kpis.setEntradasVendidas(entradasVendidas);
    kpis.setEventosActivos(eventosActivos);

    // v1: dejamos ocupaciónPromedio y mejorEvento "vacíos" (0 / null)
    // para no depender de colecciones dentro de EventoEntity.
    kpis.setOcupacionPromedio(0.0);
    kpis.setMejorEventoNombre(null);
    kpis.setMejorEventoRecaudacion(BigDecimal.ZERO);

    return kpis;
  }

  /**
   * Mapea una venta a DTO para mostrar en la tabla de "ventas recientes".
   */
  private VentaDashboardDto mapVentaToVentaDashboardDto(VentaEntradaEntity v) {
    VentaDashboardDto dto = new VentaDashboardDto();

    dto.setFechaCompra(v.getFechaVenta());

    if (v.getUsuario() != null) {
      dto.setUsuarioNombre(v.getUsuario().getUsername());   // ajustá si tu campo se llama distinto
      dto.setUsuarioEmail(v.getUsuario().getEmail());     // idem
    }

    EventoTiposEntradaEntity ete = v.getTipoEntradaEvento();
    if (ete != null) {
      if (ete.getEvento() != null) {
        dto.setEventoId(ete.getEvento().getId());
        dto.setEventoNombre(ete.getEvento().getEvento());  // o getNombre(), según tu EventoEntity
      }
      if (ete.getTiposEntrada() != null) {
        dto.setTipoEntradaNombre(ete.getTiposEntrada().getEntrada());
      }
    }

    dto.setCantidad(v.getCantidad());
    dto.setTotal(v.getMontoTotal());
    dto.setEstadoPago(v.getEstadoPago());
    dto.setPaymentId(v.getPaymentId());

    return dto;
  }

  /**
   * Mapea EventoEntity a un resumen para tabla de eventos.
   * OJO: acá hay que ajustar nombres de campos según tu EventoEntity real.
   */
  private EventoDashboardDto mapEventoToEventoDashboardDto(
    EventoEntity e,
    Long entradasVendidas,
    BigDecimal recaudacion
  ) {
    EventoDashboardDto dto = new EventoDashboardDto();

    dto.setEventoId(e.getId());
    dto.setNombre(e.getEvento());          // ajustá si tu campo se llama distinto
    dto.setFecha(e.getFecha());      // idem

    if (e.getEstablecimiento() != null) {
      dto.setEstablecimientoNombre(e.getEstablecimiento().getEstablecimiento());
    }

    // 👇 Capacidad tomada del establecimiento
    Integer capacidad = null;
    if (e.getEstablecimiento() != null) {
      capacidad = e.getEstablecimiento().getCapacidad(); // ajustá nombre del campo
    }

    dto.setCapacidadTotal(capacidad);

    // ventas y recaudación calculadas
    dto.setEntradasVendidas(entradasVendidas != null ? entradasVendidas : 0L);
    dto.setRecaudacion(recaudacion != null ? recaudacion : BigDecimal.ZERO);

    // ocupación = vendidas / capacidad
    double ocupacion = 0.0;
    if (capacidad != null && capacidad > 0 && entradasVendidas != null) {
      ocupacion = (double) entradasVendidas / capacidad; // 0–1
    }
    dto.setPorcentajeOcupacion(ocupacion);

    return dto;
  }

  public ArtistaDashboardDto dashboardArtista(Long artistaId, LocalDateTime from, LocalDateTime to) {

    Long totalEntradas = ventaRepo.totalEntradasPorArtista(artistaId, from, to);
    List<PuntoCantidadDiaDto> porDia = ventaRepo.entradasPorDiaArtista(artistaId, from, to);
    List<EventoAsistenciaDto> ranking = ventaRepo.rankingEventosPorArtista(artistaId, from, to);
    List<EntradasPorTipoDto> porTipo = ventaRepo.entradasPorTipoArtista(artistaId, from, to);

    long totalEventos = ranking.size();
    double promedio = totalEventos > 0 ? (double) totalEntradas / totalEventos : 0.0;

    String mejorNombre = null;
    Long mejorEntradas = 0L;
    if (!ranking.isEmpty()) {
      mejorNombre = ranking.get(0).getEventoNombre();
      mejorEntradas = ranking.get(0).getEntradasVendidas();
    }

    ArtistaKpisDto kpis = new ArtistaKpisDto(
      totalEntradas,
      totalEventos,
      promedio,
      mejorNombre,
      mejorEntradas
    );

    return new ArtistaDashboardDto(kpis, porDia, ranking, porTipo);
  }
}
