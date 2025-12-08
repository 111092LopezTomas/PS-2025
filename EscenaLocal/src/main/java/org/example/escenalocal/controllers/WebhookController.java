package org.example.escenalocal.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.example.escenalocal.dtos.post.PostPaymentInfoDto;
import org.example.escenalocal.entities.VentaEntradaEntity;
import org.example.escenalocal.entities.EventoTiposEntradaEntity;
import org.example.escenalocal.repositories.VentaEntradaRepository;
import org.example.escenalocal.repositories.EventoTiposEntradaRepository;
import org.example.escenalocal.auth.repository.UserRepository;
import org.example.escenalocal.services.MercadopagoService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/payments")
@RequiredArgsConstructor
public class WebhookController {

  private final MercadopagoService mercadopagoService;
  private final VentaEntradaRepository ventaRepo;
  private final UserRepository usuarioRepo;
  private final EventoTiposEntradaRepository eventoTipoRepo;
  private final ObjectMapper objectMapper;

  @PostMapping("/webhook")
  public ResponseEntity<String> handleWebhook(
    @RequestParam(required = false) Map<String, String> query,
    @RequestBody(required = false) String bodyRaw
  ) {
    System.out.println("🔥 WEBHOOK RECIBIDO");
    System.out.println("Query params = " + query);
    System.out.println("Body raw     = " + bodyRaw);

    try {
      // 1️⃣ OBTENER payment_id (puede venir por query o body)
      Long paymentId = null;

      if (query != null) {
        if (query.containsKey("id")) {
          paymentId = Long.valueOf(query.get("id"));
        } else if (query.containsKey("data.id")) {
          paymentId = Long.valueOf(query.get("data.id"));
        }
      }

      if (paymentId == null && bodyRaw != null) {
        Map body = objectMapper.readValue(bodyRaw, Map.class);
        if (body.containsKey("id")) {
          paymentId = Long.valueOf(body.get("id").toString());
        }
      }

      if (paymentId == null) {
        System.out.println("⚠️ Webhook sin payment_id, devolviendo 200");
        return ResponseEntity.ok("No payment id");
      }

      System.out.println("➡️ Payment ID = " + paymentId);

      // 2️⃣ Obtener la info REAL del pago desde Mercado Pago
      PostPaymentInfoDto info = mercadopagoService.getPaymentInfo(paymentId);

      // 👉 IMPORTANTE: puede ser null si MP respondió 404 (simulación con id inventado)
      if (info == null) {
        System.out.println("⚠️ No se obtuvo info de pago para id=" + paymentId +
          " (probablemente Payment not found / simulación).");
        // Igual devolvemos 200 para que MP no marque fallo ni reintente
        return ResponseEntity.ok("Payment not found or error getting info");
      }

      System.out.println("➡️ Estado del pago = " + info.getStatus());

      if (!"approved".equalsIgnoreCase(info.getStatus())) {
        System.out.println("⚠️ Pago no aprobado, status=" + info.getStatus());
        return ResponseEntity.ok("Pago no aprobado");
      }

      // 3️⃣ Registrar venta en tu base de datos

      VentaEntradaEntity venta = new VentaEntradaEntity();

      venta.setUsuario(
        usuarioRepo.findById(info.getUsuarioId())
          .orElseThrow(() -> new RuntimeException("Usuario no encontrado"))
      );

      // Buscar tipo de entrada por evento + tipoEntradaId
      EventoTiposEntradaEntity tipoEntrada = eventoTipoRepo.findAll()
        .stream()
        .filter(e ->
          e.getId().getEventoId().equals(info.getEventoId()) &&
            e.getId().getTiposEntradaId().equals(info.getTipoEntradaId())
        )
        .findFirst()
        .orElseThrow(() -> new RuntimeException("Tipo de entrada no encontrado"));

      venta.setTipoEntradaEvento(tipoEntrada);
      venta.setCantidad(info.getCantidad());
      venta.setPrecioUnitario(info.getPrecio());

      ventaRepo.save(venta);

      System.out.println("💾 Venta registrada OK");

      return ResponseEntity.ok("Venta registrada");

    } catch (Exception e) {
      e.printStackTrace();

      System.out.println("⚠️ Error interno al procesar webhook: " + e.getMessage());
      return ResponseEntity.ok("Error interno procesando webhook");
    }
  }
}
