package org.example.escenalocal.controllers;

import lombok.RequiredArgsConstructor;
import org.example.escenalocal.dtos.NotificacionPreferenciaDto;
import org.example.escenalocal.services.NotificacionPreferenciaService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("notification-preferences")
@RequiredArgsConstructor
public class NotificacionPreferenciaController {

  private final NotificacionPreferenciaService service;

  @GetMapping("/me")
  public ResponseEntity<NotificacionPreferenciaDto> getMine() {
    return ResponseEntity.ok(service.getMine());
  }

  @PutMapping("/me")
  public ResponseEntity<NotificacionPreferenciaDto> updateMine(@RequestBody NotificacionPreferenciaDto dto) {
    return ResponseEntity.ok(service.updateMine(dto));
  }
}
