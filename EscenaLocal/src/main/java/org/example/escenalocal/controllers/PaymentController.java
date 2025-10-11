package org.example.escenalocal.controllers;

import com.mercadopago.exceptions.MPApiException;
import com.mercadopago.exceptions.MPException;
import lombok.RequiredArgsConstructor;
import org.example.escenalocal.payments.CreatePrefCommand;
import org.example.escenalocal.payments.PaymentGateway;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/payments")
@RequiredArgsConstructor
public class PaymentController {


  private final PaymentGateway gateway;


  @PostMapping("/create-preference")
  public Map<String, Object> create(@RequestBody CreatePrefCommand cmd) throws MPException, MPApiException {
    var r = gateway.createPreference(cmd);
    return Map.of("preferenceId", r.preferenceId(), "initPoint", r.initPoint());
  }


  @GetMapping("/status/{id}")
  public Map<String, Object> status(@PathVariable String id) {
    return Map.of("status", gateway.getStatus(id).name());
  }
}
