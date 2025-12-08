package org.example.escenalocal.controllers;

import com.mercadopago.client.payment.PaymentClient;
import com.mercadopago.client.payment.PaymentCreateRequest;
import com.mercadopago.client.payment.PaymentPayerRequest;
import com.mercadopago.resources.payment.Payment;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.Map;

@RestController
@RequestMapping("/payments")
public class TestPaymentController {

  @PostMapping("/manual-test")
  public ResponseEntity<?> crearPagoDePrueba(
    @RequestParam Long usuarioId,
    @RequestParam Long eventoId,
    @RequestParam Long tipoEntradaId,
    @RequestParam int cantidad,
    @RequestParam BigDecimal precio
  ) {
    try {
      BigDecimal total = precio.multiply(BigDecimal.valueOf(cantidad));

      // ⚠ usuario comprador de PRUEBA
      String emailCompradorTest = "test_user_123456@testuser.com";

      PaymentClient client = new PaymentClient();

      PaymentCreateRequest request = PaymentCreateRequest.builder()
        .transactionAmount(total)
        .description("Compra de entradas EscenaLocal (test)")
        .paymentMethodId("account_money")
        .payer(
          PaymentPayerRequest.builder()
            .email(emailCompradorTest)
            .build()
        )
        .externalReference("EVT-" + eventoId)
        .metadata(Map.of(
          "usuarioId", usuarioId,
          "eventoId", eventoId,
          "tipoEntradaId", tipoEntradaId,
          "cantidad", cantidad,
          "precio", precio
        ))
        .build();

      Payment payment = client.create(request);

      System.out.println("✅ Pago de prueba creado por API");
      System.out.println("   id           = " + payment.getId());
      System.out.println("   status       = " + payment.getStatus());
      System.out.println("   statusDetail = " + payment.getStatusDetail());

      return ResponseEntity.ok(Map.of(
        "id", payment.getId(),
        "status", payment.getStatus(),
        "status_detail", payment.getStatusDetail()
      ));

    } catch (Exception e) {
      e.printStackTrace();
      return ResponseEntity.status(500)
        .body("Error creando pago de prueba: " + e.getMessage());
    }
  }
}
