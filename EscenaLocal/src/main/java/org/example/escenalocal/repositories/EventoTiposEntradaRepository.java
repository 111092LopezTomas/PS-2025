package org.example.escenalocal.repositories;

import org.example.escenalocal.entities.EventoTiposEntradaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EventoTiposEntradaRepository extends JpaRepository<EventoTiposEntradaEntity, Long> {
}
