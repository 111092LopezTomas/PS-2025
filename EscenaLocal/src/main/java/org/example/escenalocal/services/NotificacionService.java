package org.example.escenalocal.services;

import org.example.escenalocal.entities.Notificacion;

import java.util.List;

public interface NotificacionService {
    void createBinvenidaNotificacion(Long userId);
    List<Notificacion> getUserNotificaciones(Long userId);
    void marcarComoLeido(Long id);
}
