package org.example.escenalocal.services.impl;

import lombok.RequiredArgsConstructor;
import org.example.escenalocal.auth.service.AuthContextService;
import org.example.escenalocal.dtos.NotificacionBadgeDto;
import org.example.escenalocal.dtos.NotificacionItemDto;
import org.example.escenalocal.entities.Notificacion;
import org.example.escenalocal.repositories.NotificacionRepository;
import org.example.escenalocal.services.NotificacionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class NotificacionServiceImpl implements NotificacionService {

  private final NotificacionRepository notificacionRepository;
  private final AuthContextService auth;

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

  //---//

  @Transactional(readOnly = true)
  public NotificacionBadgeDto badge() {
    Long userId = auth.currentUserId();
    return NotificacionBadgeDto.builder()
      .unreadCount(notificacionRepository.countByUserIdAndLeidoFalse(userId))
      .build();
  }

  @Transactional(readOnly = true)
  public Page<NotificacionItemDto> list(int page, int size) {
    Long userId = auth.currentUserId();
    Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "creado"));

    return notificacionRepository.findByUserIdOrderByCreadoDesc(userId, pageable)
      .map(this::toDto);
  }

  @Transactional
  public void markRead(Long id) {
    Long userId = auth.currentUserId();

    Notificacion n = notificacionRepository.findById(id)
      .orElseThrow(() -> new IllegalArgumentException("Notificación no encontrada"));

    if (!n.getUserId().equals(userId)) throw new IllegalStateException("No autorizado");

    if (!n.isLeido()) {
      n.setLeido(true);
      notificacionRepository.save(n);
    }
  }

  @Transactional
  public int markAllRead() {
    Long userId = auth.currentUserId();
    return notificacionRepository.markAllAsRead(userId);
  }

  private NotificacionItemDto toDto(Notificacion n) {
    return NotificacionItemDto.builder()
      .id(n.getId())
      .mensaje(n.getMensaje())
      .leido(n.isLeido())
      .creado(n.getCreado())
      .build();
  }

  //--//
  public void createCambioContrasenaNotificacion(Long userId) {
    Notificacion n = new Notificacion();
    n.setUserId(userId);
    n.setMensaje("La contraseña ha sido cambiada.");
    notificacionRepository.save(n);
  }

  public void createCompraEntradaNotificacion(
    Long userId,
    Integer cantidad,
    String nombreEvento
  ) {
    Notificacion n = new Notificacion();
    n.setUserId(userId);
    n.setMensaje(
      "🎟️ Compraste " + cantidad + " entrada(s) para el evento \"" + nombreEvento + "\"."
    );
    notificacionRepository.save(n);
  }

  public void createVentaEntradaNotificacion(
    Long productorId,
    Integer cantidad,
    String nombreEvento
  ) {
    Notificacion n = new Notificacion();
    n.setUserId(productorId);
    n.setMensaje(
      "💰 Vendiste " + cantidad + " entrada(s) para el evento \"" + nombreEvento + "\"."
    );
    notificacionRepository.save(n);
  }

  public void createEventoCreadoNotificacion(Long productorUserId, String nombreEvento) {
    Notificacion n = new Notificacion();
    n.setUserId(productorUserId);
    n.setMensaje("✅ Creaste el evento \"" + nombreEvento + "\".");
    notificacionRepository.save(n);
  }

  public void createArtistaIncluidoEnEventoNotificacion(Long artistaUserId, String nombreEvento) {
    Notificacion n = new Notificacion();
    n.setUserId(artistaUserId);
    n.setMensaje("🎤 Fuiste incluido como artista en el evento \"" + nombreEvento + "\".");
    notificacionRepository.save(n);
  }

}
