package org.example.escenalocal.services.impl;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.example.escenalocal.auth.service.AuthContextService;
import org.example.escenalocal.dtos.NotificacionPreferenciaDto;
import org.example.escenalocal.entities.NotificacionPreferenciaEntity;
import org.example.escenalocal.repositories.NotificacionPreferenciaRepository;
import org.example.escenalocal.services.NotificacionPreferenciaService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Data
public class NotificacionPreferenciaServiceImpl implements NotificacionPreferenciaService {
  private final NotificacionPreferenciaRepository repo;
  private final AuthContextService auth;

  @Transactional
  public NotificacionPreferenciaDto getMine() {
    Long userId = auth.currentUserId();

    NotificacionPreferenciaEntity e = repo.findByUserId(userId)
      .orElseGet(() -> repo.save(NotificacionPreferenciaEntity.builder()
        .userId(userId)
        .build()));

    return toDto(e);
  }

  @Transactional
  public NotificacionPreferenciaDto updateMine(NotificacionPreferenciaDto dto) {
    Long userId = auth.currentUserId();

    NotificacionPreferenciaEntity e = repo.findByUserId(userId)
      .orElseGet(() -> NotificacionPreferenciaEntity.builder()
        .userId(userId)
        .build());

    apply(e, dto);
    return toDto(repo.save(e));
  }

  private static NotificacionPreferenciaDto toDto(NotificacionPreferenciaEntity e) {
    return NotificacionPreferenciaDto.builder()
      .email(e.isEmail())
      .push(e.isPush())
      .marketingNovedades(e.isMarketingNovedades())
      .recordatoriosEventos(e.isRecordatoriosEventos())
      .mensajesDirectos(e.isMensajesDirectos())
      .invitacionesAEventos(e.isInvitacionesAEventos())
      .nuevosSeguidores(e.isNuevosSeguidores())
      .ventasEntradas(e.isVentasEntradas())
      .pagosMercadopago(e.isPagosMercadopago())
      .build();
  }

  private static void apply(NotificacionPreferenciaEntity e, NotificacionPreferenciaDto d) {
    e.setEmail(d.email);
    e.setPush(d.push);

    e.setMarketingNovedades(d.marketingNovedades);
    e.setRecordatoriosEventos(d.recordatoriosEventos);
    e.setMensajesDirectos(d.mensajesDirectos);

    e.setInvitacionesAEventos(d.invitacionesAEventos);
    e.setNuevosSeguidores(d.nuevosSeguidores);

    e.setVentasEntradas(d.ventasEntradas);
    e.setPagosMercadopago(d.pagosMercadopago);
  }
}
