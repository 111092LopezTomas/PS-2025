package org.example.escenalocal.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.example.escenalocal.services.impl.PaymentServiceImpl;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/payments")
@RequiredArgsConstructor
public class WebhookController {

  private final PaymentServiceImpl paymentService;
  private final ObjectMapper objectMapper;

  @PostMapping("/webhook")
  public ResponseEntity<String> handleWebhook(
    @RequestParam(required = false) Map<String, String> query,
    @RequestBody(required = false) String bodyRaw
  ) throws Exception {

    System.out.println("📩 WEBHOOK RECIBIDO");
    System.out.println("Query: " + query);
    System.out.println("Body: " + bodyRaw);

    Long paymentId = extractPaymentId(query, bodyRaw);

    if (paymentId == null) {
      return ResponseEntity.ok("No payment id");
    }

    paymentService.processPayment(paymentId);
    return ResponseEntity.ok("OK");
  }

  private Long extractPaymentId(Map<String, String> query, String bodyRaw) {
    try {
      // Query string (data.id)
      if (query != null && query.containsKey("data.id")) {
        return Long.valueOf(query.get("data.id"));
      }

      // Body JSON
      if (bodyRaw != null && !bodyRaw.isBlank()) {
        Map<String, Object> json = objectMapper.readValue(bodyRaw, Map.class);
        Map<String, Object> data = (Map<String, Object>) json.get("data");

        if (data != null && data.get("id") != null) {
          return Long.valueOf(data.get("id").toString());
        }
      }
    } catch (Exception e) {
      e.printStackTrace();
    }
    return null;
  }
}


