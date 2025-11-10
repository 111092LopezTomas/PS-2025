package org.example.escenalocal.controllers;

import org.example.escenalocal.entities.Notificacion;
import org.example.escenalocal.services.NotificacionService;
import org.example.escenalocal.services.impl.NotificacionServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

  @RestController
  @RequestMapping("/api/notificaciones")
  public class NotificacionController {

    @Autowired
    private NotificacionServiceImpl notificacionService;

    @GetMapping("/{userId}")
    public List<Notificacion> getUserNotificaciones(@PathVariable Long userId) {
      return notificacionService.getUserNotificaciones(userId);
    }

    @PatchMapping("/{id}/leido")
    public void marcarComoLeido(@PathVariable Long id) {
      notificacionService.marcarComoLeido(id);
    }
}
