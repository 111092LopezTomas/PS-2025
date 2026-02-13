package org.example.escenalocal.controllers;

import org.example.escenalocal.dtos.NotificacionBadgeDto;
import org.example.escenalocal.dtos.NotificacionItemDto;
import org.example.escenalocal.entities.Notificacion;
import org.example.escenalocal.services.NotificacionService;
import org.example.escenalocal.services.impl.NotificacionServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
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

    //--//
    @GetMapping("/me/badge")
    public ResponseEntity<NotificacionBadgeDto> badge() {
      return ResponseEntity.ok(notificacionService.badge());
    }

    @GetMapping("/me")
    public ResponseEntity<Page<NotificacionItemDto>> list(
      @RequestParam(defaultValue = "0") int page,
      @RequestParam(defaultValue = "10") int size
    ) {
      return ResponseEntity.ok(notificacionService.list(page, size));
    }

    @PatchMapping("/{id}/read")
    public ResponseEntity<Void> markRead(@PathVariable Long id) {
      notificacionService.markRead(id);
      return ResponseEntity.noContent().build();
    }

    @PatchMapping("/me/read-all")
    public ResponseEntity<Void> readAll() {
      notificacionService.markAllRead();
      return ResponseEntity.noContent().build();
    }
}
