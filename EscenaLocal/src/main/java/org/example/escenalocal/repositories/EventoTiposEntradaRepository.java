package org.example.escenalocal.repositories;

import org.example.escenalocal.entities.EventoTiposEntradaEntity;
import org.example.escenalocal.entities.EventoTiposEntradaId;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface EventoTiposEntradaRepository
  extends JpaRepository<EventoTiposEntradaEntity, EventoTiposEntradaId> {

  Optional<EventoTiposEntradaEntity>
  findById_EventoIdAndId_TiposEntradaId(Long eventoId, Long tiposEntradaId);
}
