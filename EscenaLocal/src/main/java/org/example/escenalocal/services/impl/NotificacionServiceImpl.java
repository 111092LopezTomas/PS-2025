package org.example.escenalocal.services.impl;

import org.example.escenalocal.entities.Notificacion;
import org.example.escenalocal.repositories.NotificacionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class NotificacionServiceImpl {

  @Autowired
  private NotificacionRepository notificacionRepository;

  public void createBinvenidaNotificacion(Long userId) {
    Notificacion n = new Notificacion();
    n.setUserId(userId);
    n.setMensaje("¡Bienvenido! Te has logueado correctamente.");
    notificacionRepository.save(n);
  }

  public List<Notificacion> getUserNotificaciones(Long userId) {
    return notificacionRepository.findByUserIdOrderByCreadoDesc(userId);
  }

  public void marcarComoLeido(Long id) {
    notificacionRepository.findById(id).ifPresent(n -> {
      n.setLeido(true);
      notificacionRepository.save(n);
    });
  }
}
