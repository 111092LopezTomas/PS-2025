package org.example.escenalocal.services;

import org.example.escenalocal.entities.Notificacion;

import java.util.List;

public interface NotificacionService {
    void createBinvenidaNotificacion(Long userId);
    List<Notificacion> getUserNotificaciones(Long userId);
    void marcarComoLeido(Long id);
  void createCambioContrasenaNotificacion(Long userId);

  void createCompraEntradaNotificacion(Long compradorId, Integer cantidad, String nombreEvento);

  void createVentaEntradaNotificacion(Long productorId, Integer cantidad, String nombreEvento);

  void createEventoCreadoNotificacion(Long productorUserId, String nombreEvento);

  void createArtistaIncluidoEnEventoNotificacion(Long artistaUserId, String nombreEvento);
}
