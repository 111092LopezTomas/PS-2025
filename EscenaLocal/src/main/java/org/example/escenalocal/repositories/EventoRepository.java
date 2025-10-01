package org.example.escenalocal.repositories;

import org.example.escenalocal.dtos.get.EventoDetalleProj;
import org.example.escenalocal.dtos.get.GetEventoDto;
import org.example.escenalocal.entities.EventoEntity;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.ListCrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
//public interface EventoRepository extends JpaRepository<EventoEntity, Long> {
//
//@Query(value = """
//
//        SELECT\s
//  e.id, e.activo, e.descripcion, e.evento, e.fecha, e.hora,
//  STRING_AGG(DISTINCT a.nombre, ', ')     AS artistas,
//  STRING_AGG(DISTINCT te.entrada, ', ')   AS entradas,
//  c.clasificacion, e2.establecimiento, e2.direccion, c2.ciudad, p.provincia
//FROM eventos e
//LEFT JOIN artista_evento ae       ON ae.id_evento = e.id
//LEFT JOIN artistas a              ON a.id = ae.id_artista
//LEFT JOIN evento_entrada ee       ON ee.id_evento = e.id
//LEFT JOIN tipos_entrada te        ON te.id = ee.id_tipos_entrada
//JOIN clasificaciones c            ON c.id = e.id_clasificacion
//JOIN establecimientos e2          ON e2.id = e.id_establecimiento
//JOIN barrios b                    ON b.id = e2.id_barrio
//JOIN ciudades c2                  ON c2.id = b.id_ciudad
//JOIN provincias p                 ON p.id = c2.id_provincia
//GROUP BY\s
//  e.id, e.activo, e.descripcion, e.evento, e.fecha, e.hora,
//  c.clasificacion, e2.establecimiento, e2.direccion, c2.ciudad, p.provincia;
//""", nativeQuery = true)
//    List<GetEventoDto> findAllEventos();
//
//@Query(value = """
//
//        SELECT\s
//  e.id, e.activo, e.descripcion, e.evento, e.fecha, e.hora,
//  STRING_AGG(DISTINCT a.nombre, ', ')     AS artistas,
//  STRING_AGG(DISTINCT te.entrada, ', ')   AS entradas,
//  c.clasificacion, e2.establecimiento, e2.direccion, c2.ciudad, p.provincia
//FROM eventos e
//LEFT JOIN artista_evento ae       ON ae.id_evento = e.id
//LEFT JOIN artistas a              ON a.id = ae.id_artista
//LEFT JOIN evento_entrada ee       ON ee.id_evento = e.id
//LEFT JOIN tipos_entrada te        ON te.id = ee.id_tipos_entrada
//JOIN clasificaciones c            ON c.id = e.id_clasificacion
//JOIN establecimientos e2          ON e2.id = e.id_establecimiento
//JOIN barrios b                    ON b.id = e2.id_barrio
//JOIN ciudades c2                  ON c2.id = b.id_ciudad
//JOIN provincias p                 ON p.id = c2.id_provincia
//where e.id = :id
//GROUP BY\s
//  e.id, e.activo, e.descripcion, e.evento, e.fecha, e.hora,
//  c.clasificacion, e2.establecimiento, e2.direccion, c2.ciudad, p.provincia;
//
//""", nativeQuery = true)
//    GetEventoDto findEventoById(@Param("id")  Long id);
//
//}
public interface EventoRepository extends ListCrudRepository<EventoEntity, Long> {

    @EntityGraph(attributePaths = {
            "clasificacion",
            "establecimiento",
            "establecimiento.barrio",
            "establecimiento.barrio.ciudad",
            "establecimiento.barrio.ciudad.provincia",
            "artistasEvento.artista",
            "eventoTiposEntrada.tiposEntrada"
    })
    @Query("select e from EventoEntity e")
    List<EventoEntity> findAllForDto();

    @EntityGraph(attributePaths = {
            "clasificacion",
            "establecimiento",
            "establecimiento.barrio",
            "establecimiento.barrio.ciudad",
            "establecimiento.barrio.ciudad.provincia",
            "artistasEvento.artista",
            "eventoTiposEntrada.tiposEntrada"
    })
    @Query("select e from EventoEntity e where e.id = :id")
    Optional<EventoEntity> findByIdForDto(@Param("id") Long id);
}
