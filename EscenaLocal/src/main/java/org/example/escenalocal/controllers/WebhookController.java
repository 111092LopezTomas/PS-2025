package org.example.escenalocal.controllers;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/payments")
public class WebhookController {


  @Value("${mercadopago.webhook-secret:}")
  private String webhookSecret;


  @PostMapping("/webhook")
  public ResponseEntity<String> handleWebhook(
    @RequestHeader(value = "X-Signature", required = false) String signature,
    @RequestHeader(value = "X-Request-Id", required = false) String requestId,
    @RequestParam Map<String, String> query,
    @RequestBody(required = false) String bodyRaw
  ) {
// TODO: valida la firma con webhookSecret si lo necesitás
    return ResponseEntity.status(HttpStatus.OK).body("ok");
  }
}
