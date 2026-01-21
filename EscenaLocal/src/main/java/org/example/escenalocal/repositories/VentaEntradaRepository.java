package org.example.escenalocal.repositories;

import org.example.escenalocal.entities.VentaEntradaEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface VentaEntradaRepository
  extends JpaRepository<VentaEntradaEntity, Long> {

  boolean existsByUsuario_IdAndTipoEntradaEvento_Id_EventoIdAndTipoEntradaEvento_Id_TiposEntradaId(
    Long usuarioId,
    Long eventoId,
    Long tipoEntradaId
  );
}
