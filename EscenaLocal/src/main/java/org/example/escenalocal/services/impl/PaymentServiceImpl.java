package org.example.escenalocal.services.impl;

import lombok.RequiredArgsConstructor;
import org.example.escenalocal.dtos.post.PostPaymentInfoDto;
import org.example.escenalocal.entities.EventoTiposEntradaEntity;
import org.example.escenalocal.entities.VentaEntradaEntity;
import org.example.escenalocal.repositories.EventoTiposEntradaRepository;
import org.example.escenalocal.repositories.VentaEntradaRepository;
import org.example.escenalocal.auth.repository.UserRepository;
import org.example.escenalocal.services.MercadopagoService;
import org.example.escenalocal.services.impl.MercadopagoServiceImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class PaymentServiceImpl {

  private final MercadopagoServiceImpl mercadopagoService;
  private final VentaEntradaRepository ventaRepo;
  private final UserRepository usuarioRepo;
  private final EventoTiposEntradaRepository eventoTipoRepo;

  @Transactional
  public void processPayment(Long paymentId) {

    PostPaymentInfoDto info = mercadopagoService.getPaymentInfo(paymentId);

    // 🔁 Retry por timing de MP
    int intentos = 0;
    while ((info == null || info.getStatus() == null) && intentos < 3) {
      try {
        Thread.sleep(2000); // 2 segundos
      } catch (InterruptedException ignored) {}

      info = mercadopagoService.getPaymentInfo(paymentId);
      intentos++;
    }

    if (info == null || info.getStatus() == null) {
      System.out.println("⚠ Pago sin estado todavía: " + paymentId);
      return;
    }

    if (!"approved".equalsIgnoreCase(info.getStatus())) {
      System.out.println("⚠ Pago no aprobado: " + info.getStatus());
      return;
    }

    // 🔒 Validación CRÍTICA
    if (
      info.getUsuarioId() == null ||
        info.getEventoId() == null ||
        info.getTipoEntradaId() == null ||
        info.getCantidad() == null ||
        info.getPrecio() == null
    ) {
      System.out.println("⚠ Pago aprobado pero metadata incompleta. paymentId=" + paymentId);
      System.out.println("metadata=" + info);
      return;
    }

    boolean yaExiste =
      ventaRepo.existsByUsuario_IdAndTipoEntradaEvento_Id_EventoIdAndTipoEntradaEvento_Id_TiposEntradaId(
        info.getUsuarioId(),
        info.getEventoId(),
        info.getTipoEntradaId()
      );

    if (yaExiste) {
      System.out.println("⚠ Venta ya registrada, ignorando webhook");
      return;
    }

    VentaEntradaEntity venta = new VentaEntradaEntity();

    venta.setUsuario(
      usuarioRepo.findById(info.getUsuarioId())
        .orElseThrow(() -> new RuntimeException("Usuario no encontrado"))
    );

    EventoTiposEntradaEntity tipoEntrada =
      eventoTipoRepo.findById_EventoIdAndId_TiposEntradaId(
        info.getEventoId(),
        info.getTipoEntradaId()
      ).orElseThrow(() -> new RuntimeException("Tipo de entrada no encontrado"));

    venta.setTipoEntradaEvento(tipoEntrada);
    venta.setCantidad(info.getCantidad());
    venta.setPrecioUnitario(info.getPrecio());

    ventaRepo.save(venta);

    System.out.println("✅ Venta registrada correctamente (paymentId=" + paymentId + ")");
  }
}
