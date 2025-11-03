package org.example.escenalocal.repositories;

import org.example.escenalocal.entities.EstablecimientoEntity;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface EstablecimientoRepository extends JpaRepository<EstablecimientoEntity,Long> {

  @EntityGraph(attributePaths = {
    "barrio",
    "barrio.ciudad",
    "barrio.ciudad.provincia"
  })
  @Query("""
        select es
        from EstablecimientoEntity es
    """)
  List<EstablecimientoEntity> finAllEstablecimientos();


  @EntityGraph(attributePaths = {
    "barrio",
    "barrio.ciudad",
    "barrio.ciudad.provincia"
  })
  @Query("""
        select es
        from EstablecimientoEntity es
        where es.id = :id
    """)
  Optional<EstablecimientoEntity> findEstablecimientoById(@Param("id") Long id);
}
