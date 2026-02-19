package org.example.escenalocal.services.impl;

import lombok.RequiredArgsConstructor;
import org.example.escenalocal.dtos.post.PostPaymentInfoDto;
import org.example.escenalocal.entities.EventoTiposEntradaEntity;
import org.example.escenalocal.entities.VentaEntradaEntity;
import org.example.escenalocal.repositories.EventoTiposEntradaRepository;
import org.example.escenalocal.repositories.NotificacionRepository;
import org.example.escenalocal.repositories.VentaEntradaRepository;
import org.example.escenalocal.auth.repository.UserRepository;
import org.example.escenalocal.services.MercadopagoService;
import org.example.escenalocal.services.NotificacionService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PaymentServiceImpl {

  // mejor por interfaz, pero te dejo como lo tenés
  private final MercadopagoService mercadopagoService;
  private final VentaEntradaRepository ventaRepo;
  private final UserRepository usuarioRepo;
  private final EventoTiposEntradaRepository eventoTipoRepo;
  private final NotificacionService notificacionService;

  @Transactional
  public void processPayment(Long paymentId) throws Exception {

    // 0️⃣ Protegernos de paymentId null
    if (paymentId == null) {
      System.out.println("⚠ Webhook sin paymentId, no se puede procesar venta");
      return;
    }

    // 1️⃣ Evitar procesar dos veces el mismo pago de MP
    if (ventaRepo.existsByPaymentId(paymentId)) {
      System.out.println("⚠ Venta ya registrada para paymentId=" + paymentId + ", ignorando webhook");
      return;
    }

    // 2️⃣ Traer info desde Mercado Pago
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
      System.out.println("⚠ Pago no aprobado: " + info.getStatus() + " (paymentId=" + paymentId + ")");
      return;
    }

    // 3️⃣ Validación de metadata
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

    // (Opcional) si igual querés evitar que el mismo usuario
    // compre EXACTAMENTE la misma combinación (evento+tipo) más de una vez:
        /*
        boolean yaExisteMismaComb =
                ventaRepo.existsByUsuario_IdAndTipoEntradaEvento_Id_EventoIdAndTipoEntradaEvento_Id_TiposEntradaId(
                        info.getUsuarioId(),
                        info.getEventoId(),
                        info.getTipoEntradaId()
                );

        if (yaExisteMismaComb) {
            System.out.println("⚠ El usuario ya tiene una compra para ese evento y tipo de entrada. " +
                               "usuarioId=" + info.getUsuarioId() +
                               ", eventoId=" + info.getEventoId() +
                               ", tipoEntradaId=" + info.getTipoEntradaId());
            // si NO querés bloquear eso, simplemente eliminá este bloque
        }
        */

    // 4️⃣ Construir la venta
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

    // 🔥 Campos nuevos de Mercado Pago en la entidad
    venta.setPaymentId(paymentId);                    // o info.getPaymentId() si lo trae
    venta.setEstadoPago(info.getStatus());            // "approved"
    venta.setExternalReference(info.getExternalReference()); // EVT-17, etc. (agregalo al DTO)
    venta.setFechaActualizacion(LocalDateTime.now());
    venta.setQrToken(UUID.randomUUID().toString());
    venta.setStatusDetail(info.getStatusDetail());    // agregalo al DTO si te interesa

    ventaRepo.save(venta);

    System.out.println("✅ Venta registrada correctamente (paymentId=" + paymentId + ")");

    Long compradorId = venta.getUsuario().getId();
    Long productorId = tipoEntrada.getEvento().getProductor().getUsuario().getId();
    String nombreEvento = tipoEntrada.getEvento().getEvento(); // o getNombre()
    Integer cantidad = venta.getCantidad();

// Notificación al comprador
    notificacionService.createCompraEntradaNotificacion(
      compradorId,
      cantidad,
      nombreEvento
    );

// Notificación al productor (evitar duplicado si fuera el mismo user)
    if (!productorId.equals(compradorId)) {
      notificacionService.createVentaEntradaNotificacion(
        productorId,
        cantidad,
        nombreEvento
      );
    }
  }
}
