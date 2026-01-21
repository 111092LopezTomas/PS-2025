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

    // 1️⃣ Consultar pago real en MercadoPago
    PostPaymentInfoDto info = mercadopagoService.getPaymentInfo(paymentId);

    if (info == null || info.getStatus() == null) {
      System.out.println("⚠ Pago sin estado todavía: " + paymentId);
      return;
    }

    // 2️⃣ Validar estado del pago
    if (!"approved".equalsIgnoreCase(info.getStatus())) {
      System.out.println("⚠ Pago no aprobado: " + info.getStatus());
      return;
    }

    // 3️⃣ Evitar duplicados (misma compra procesada más de una vez)
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

    // 4️⃣ ACÁ RECIÉN CONFIRMÁS LA COMPRA ✅
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

    // 5️⃣ Persistir
    ventaRepo.save(venta);

    System.out.println("✅ Venta registrada correctamente (paymentId=" + paymentId + ")");
  }

}

