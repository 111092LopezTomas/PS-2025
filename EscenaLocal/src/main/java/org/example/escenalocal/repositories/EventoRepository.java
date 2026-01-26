package org.example.escenalocal.repositories;

import jakarta.persistence.QueryHint;
import org.example.escenalocal.entities.EventoEntity;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.QueryHints;
import org.springframework.data.repository.ListCrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface EventoRepository extends ListCrudRepository<EventoEntity, Long> {
@EntityGraph(attributePaths = {
  "clasificacion",
  "establecimiento",
  "productor",
  "imagenDatos",
  "establecimiento.id",
  "establecimiento.capacidad",
  "establecimiento.barrio",
  "establecimiento.barrio.ciudad",
  "establecimiento.barrio.ciudad.provincia",
  "artistasEvento.artista",
  "eventoTiposEntrada.tiposEntrada"
})
@Query("select distinct e from EventoEntity e where e.activo = true order by e.fecha desc")
@QueryHints(@QueryHint(name = "org.hibernate.jpa.HibernateHints.HINT_PASS_DISTINCT_THROUGH", value = "false"))
    List<EventoEntity> findAllForDto();

    @EntityGraph(attributePaths = {
            "clasificacion",
            "establecimiento",
            "productor",
            "imagenDatos",
            "establecimiento.id",
            "establecimiento.capacidad",
            "establecimiento.barrio",
            "establecimiento.barrio.ciudad",
            "establecimiento.barrio.ciudad.provincia",
            "artistasEvento.artista",
            "eventoTiposEntrada.tiposEntrada",
            "eventoTiposEntrada.precio",
            "eventoTiposEntrada.disponibilidad"
    })
    @Query("select e from EventoEntity e where e.activo and e.id = :id")
    Optional<EventoEntity> findByIdForDto(@Param("id") Long id);

  @EntityGraph(attributePaths = {
    "clasificacion",
    "establecimiento",
    "productor",
    "imagenDatos",
    "establecimiento.capacidad",
    "establecimiento.barrio",
    "establecimiento.barrio.ciudad",
    "establecimiento.barrio.ciudad.provincia",
    "artistasEvento.artista",
    "eventoTiposEntrada.tiposEntrada"
  })
  @Query("""
    select distinct e
    from EventoEntity e
    where e.activo = true
      and e.establecimiento.id = :establecimientoId
      order by e.fecha desc
""")
  List<EventoEntity> findActivosByEstablecimientoId(@Param("establecimientoId") Long establecimientoId);

  @EntityGraph(attributePaths = {
    "clasificacion",
    "establecimiento",
    "productor",
    "imagenDatos",
    "establecimiento.capacidad",
    "establecimiento.barrio",
    "establecimiento.barrio.ciudad",
    "establecimiento.barrio.ciudad.provincia",
    "artistasEvento.artista",
    "artistasEvento.artista.id",
    "eventoTiposEntrada.tiposEntrada"
  })
  @Query("""
  select distinct e
  from EventoEntity e
  join e.artistasEvento ae
  where e.activo = true
    and ae.artista.id = :artistaId
  order by e.fecha desc
""")
  List<EventoEntity> findActivosByArtistaId(@Param("artistaId") Long artistaId);

  @EntityGraph(attributePaths = {
    "clasificacion",
    "establecimiento",
    "productor",
    "productor.id",
    "imagenDatos",
    "establecimiento.id",
    "establecimiento.capacidad",
    "establecimiento.barrio",
    "establecimiento.barrio.ciudad",
    "establecimiento.barrio.ciudad.provincia",
    "artistasEvento.artista",
    "eventoTiposEntrada.tiposEntrada"
  })
  @Query("""
  select distinct e
  from EventoEntity e
  where e.activo = true
    and e.productor.id = :productorId
  order by e.fecha desc
""")
  List<EventoEntity> findActivosByProductorId(@Param("productorId") Long productorId);

  @Query("""
        SELECT e
        FROM EventoEntity e
        WHERE e.productor.id = :productorId
          AND e.fecha BETWEEN :from AND :to
    """)
  List<EventoEntity> eventosDelProductorEnPeriodo(Long productorId,
                                                  LocalDate from,
                                                  LocalDate to);

  @Query("""
        SELECT COUNT(e)
        FROM EventoEntity e
        WHERE e.productor.id = :productorId
          AND e.fecha BETWEEN :from AND :to
    """)
  Integer countEventosDelProductorEnPeriodo(Long productorId,
                                            LocalDate from,
                                            LocalDate to);

}
