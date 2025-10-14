package org.example.escenalocal.controllers;

import com.mercadopago.exceptions.MPApiException;
import com.mercadopago.exceptions.MPException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.example.escenalocal.payments.CreatePrefCommand;
import org.example.escenalocal.payments.PaymentGateway;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/payments")
@RequiredArgsConstructor
public class PaymentController {


  private final PaymentGateway gateway;


  @PostMapping("/create-preference")
  public Map<String, Object> create(@RequestBody CreatePrefCommand cmd,
                                    HttpServletRequest request) throws Exception {

    String base = ServletUriComponentsBuilder.fromRequest(request)
      .replacePath(null)
      .build()
      .toUriString();

    var r = gateway.createPreferenceWithBase(cmd, base);
    return Map.of("preferenceId", r.preferenceId(), "initPoint", r.initPoint());
  }


  @GetMapping("/status/{id}")
  public Map<String, Object> status(@PathVariable String id) {
    return Map.of("status", gateway.getStatus(id).name());
  }

  @RequestMapping(
    path = "/create-preference/event/{eventId}",
    method = { RequestMethod.POST, RequestMethod.GET }
  )
  public Map<String,Object> createForEvent(
    @PathVariable Long eventId,
    @RequestParam(defaultValue = "1") int qty,
    @RequestParam BigDecimal precio,
    HttpServletRequest request
  ) throws Exception {

    if (eventId == null || eventId <= 0) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "eventoId inválido");
    }
    if (qty < 1) qty = 1;
    if (precio == null || precio.compareTo(BigDecimal.ZERO) <= 0) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "precio inválido");
    }
    precio = precio.setScale(2, RoundingMode.HALF_UP);

    String base = ServletUriComponentsBuilder.fromRequest(request)
      .replacePath(null).replaceQuery(null)
      .build().toUriString();

    var cmd = new CreatePrefCommand(
      "EVT-" + eventId,
      List.of(new CreatePrefCommand.Item(
        String.valueOf(eventId),
        "Entrada", "Show",
        qty,
        precio
      ))
    );

    var r = gateway.createPreferenceWithBase(cmd, base);
    return Map.of("preferenceId", r.preferenceId(), "initPoint", r.initPoint());
  }
}

