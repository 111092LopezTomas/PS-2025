package org.example.escenalocal.services;

import org.example.escenalocal.dtos.NotificacionPreferenciaDto;

public interface NotificacionPreferenciaService {
  NotificacionPreferenciaDto getMine();
  NotificacionPreferenciaDto updateMine(NotificacionPreferenciaDto dto);

}
