package org.example.escenalocal.repositories;

import org.example.escenalocal.dashboard.*;
import org.example.escenalocal.entities.VentaEntradaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public interface VentaEntradaRepository
  extends JpaRepository<VentaEntradaEntity, Long> {

  boolean existsByUsuario_IdAndTipoEntradaEvento_Id_EventoIdAndTipoEntradaEvento_Id_TiposEntradaId(
    Long usuarioId,
    Long eventoId,
    Long tipoEntradaId
  );

  boolean existsByPaymentId(Long paymentId);

  @Query("""
    SELECT COALESCE(SUM(v.cantidad * v.precioUnitario), 0)
    FROM VentaEntradaEntity v
    JOIN v.tipoEntradaEvento ete
    JOIN ete.evento e
    WHERE e.productor.id = :productorId
      AND v.estadoPago = 'approved'
      AND v.fechaVenta BETWEEN :from AND :to
    """)
  BigDecimal totalRecaudadoPorProductor(Long productorId,
                                        LocalDateTime from,
                                        LocalDateTime to);

  @Query("""
    SELECT COALESCE(SUM(v.cantidad), 0L)
    FROM VentaEntradaEntity v
    JOIN v.tipoEntradaEvento ete
    JOIN ete.evento e
    WHERE e.productor.id = :productorId
      AND v.estadoPago = 'approved'
      AND v.fechaVenta BETWEEN :from AND :to
    """)
  Long totalEntradasVendidasPorProductor(Long productorId,
                                         LocalDateTime from,
                                         LocalDateTime to);

  @Query("""
    SELECT new org.example.escenalocal.dashboard.PuntoVentaDiaDto(
        v.fechaVenta,
        SUM(v.cantidad * v.precioUnitario)
    )
    FROM VentaEntradaEntity v
    JOIN v.tipoEntradaEvento ete
    JOIN ete.evento e
    WHERE e.productor.id = :productorId
      AND v.estadoPago = 'approved'
      AND v.fechaVenta BETWEEN :from AND :to
    GROUP BY v.fechaVenta
    ORDER BY v.fechaVenta
    """)
  List<PuntoVentaDiaDto> ventasPorDia(Long productorId,
                                      LocalDateTime from,
                                      LocalDateTime to);

  @Query("""
    SELECT new org.example.escenalocal.dashboard.EntradasPorTipoDto(
        te.entrada,
        SUM(v.cantidad)
    )
    FROM VentaEntradaEntity v
    JOIN v.tipoEntradaEvento ete
    JOIN ete.tiposEntrada te
    JOIN ete.evento e
    WHERE e.productor.id = :productorId
      AND v.estadoPago = 'approved'
      AND v.fechaVenta BETWEEN :from AND :to
    GROUP BY te.entrada
    """)
  List<EntradasPorTipoDto> entradasPorTipo(Long productorId,
                                           LocalDateTime from,
                                           LocalDateTime to);

  @Query("""
    SELECT new org.example.escenalocal.dashboard.EventoRankingDto(
        e.id,
        e.evento,
        SUM(v.cantidad * v.precioUnitario),
        SUM(v.cantidad)
    )
    FROM VentaEntradaEntity v
    JOIN v.tipoEntradaEvento ete
    JOIN ete.evento e
    WHERE e.productor.id = :productorId
      AND v.estadoPago = 'approved'
      AND v.fechaVenta BETWEEN :from AND :to
    GROUP BY e.id, e.evento
    ORDER BY SUM(v.cantidad * v.precioUnitario) DESC
    """)
  List<EventoRankingDto> rankingEventos(Long productorId,
                                        LocalDateTime from,
                                        LocalDateTime to);

  @Query("""
    SELECT v
    FROM VentaEntradaEntity v
    JOIN v.tipoEntradaEvento ete
    JOIN ete.evento e
    WHERE e.productor.id = :productorId
      AND v.fechaVenta BETWEEN :from AND :to
    ORDER BY v.fechaVenta DESC
    """)
  List<VentaEntradaEntity> ventasPorProductorEnPeriodo(Long productorId,
                                                       LocalDateTime from,
                                                       LocalDateTime to);

  @Query("""
  SELECT new org.example.escenalocal.dashboard.EntradaCompradaDto(
      v.id,
      e.id,
      e.evento,
      e.fecha,
      est.establecimiento,
      te.entrada,
      v.cantidad,
      v.precioUnitario,
      v.estadoPago,
      v.paymentId,
      v.externalReference,
      v.fechaVenta
  )
  FROM VentaEntradaEntity v
  JOIN v.tipoEntradaEvento ete
  JOIN ete.evento e
  LEFT JOIN e.establecimiento est
  JOIN ete.tiposEntrada te
  WHERE v.usuario.id = :usuarioId
  ORDER BY v.fechaVenta DESC
""")
  List<EntradaCompradaDto> historialComprasPorUsuario(@Param("usuarioId") Long usuarioId);

  @Query("""
  SELECT COALESCE(SUM(v.cantidad), 0)
  FROM VentaEntradaEntity v
  JOIN v.tipoEntradaEvento ete
  JOIN ete.evento e
  JOIN e.artistasEvento ae
  WHERE ae.artista.id = :artistaId
    AND v.estadoPago = 'approved'
    AND v.fechaVenta BETWEEN :from AND :to
""")
  Long totalEntradasPorArtista(Long artistaId, LocalDateTime from, LocalDateTime to);

  @Query("""
  SELECT new org.example.escenalocal.dashboard.PuntoCantidadDiaDto(
    DATE(v.fechaVenta),
    COALESCE(SUM(v.cantidad), 0L)
  )
  FROM VentaEntradaEntity v
  JOIN v.tipoEntradaEvento ete
  JOIN ete.evento e
  JOIN e.artistasEvento ae
  WHERE ae.artista.id = :artistaId
    AND v.estadoPago = 'approved'
    AND v.fechaVenta BETWEEN :from AND :to
  GROUP BY DATE(v.fechaVenta)
  ORDER BY DATE(v.fechaVenta)
""")
  List<PuntoCantidadDiaDto> entradasPorDiaArtista(Long artistaId, LocalDateTime from, LocalDateTime to);

  @Query("""
  SELECT new org.example.escenalocal.dashboard.EventoAsistenciaDto(
    e.id,
    e.evento,
    SUM(v.cantidad)
  )
  FROM VentaEntradaEntity v
  JOIN v.tipoEntradaEvento ete
  JOIN ete.evento e
  JOIN e.artistasEvento ae
  WHERE ae.artista.id = :artistaId
    AND v.estadoPago = 'approved'
    AND v.fechaVenta BETWEEN :from AND :to
  GROUP BY e.id, e.evento
  ORDER BY COALESCE(SUM(v.cantidad), 0) DESC
""")
  List<EventoAsistenciaDto> rankingEventosPorArtista(Long artistaId, LocalDateTime from, LocalDateTime to);

  @Query("""
  SELECT new org.example.escenalocal.dashboard.EntradasPorTipoDto(
    te.entrada,
    SUM(v.cantidad)
  )
  FROM VentaEntradaEntity v
  JOIN v.tipoEntradaEvento ete
  JOIN ete.tiposEntrada te
  JOIN ete.evento e
  JOIN e.artistasEvento ae
  WHERE ae.artista.id = :artistaId
    AND v.estadoPago = 'approved'
    AND v.fechaVenta BETWEEN :from AND :to
  GROUP BY te.entrada
""")
  List<EntradasPorTipoDto> entradasPorTipoArtista(Long artistaId, LocalDateTime from, LocalDateTime to);




}
