package org.example.escenalocal.controllers;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.example.escenalocal.auth.repository.UserRepository;
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
import org.springframework.security.core.context.SecurityContextHolder;


@RestController
@RequestMapping("/payments")
@RequiredArgsConstructor
public class PaymentController {

  private final PaymentGateway gateway;
  private final UserRepository usuarioRepository;

  @PostMapping("/create-preference")
  public Map<String, Object> create(
    @RequestBody CreatePrefCommand cmd,
    HttpServletRequest request
  ) throws Exception {

    String base = ServletUriComponentsBuilder
      .fromRequest(request)
      .replacePath(null)
      .replaceQuery(null)
      .build()
      .toUriString();

    var r = gateway.createPreferenceWithBase(cmd, base);
    return Map.of(
      "preferenceId", r.preferenceId(),
      "initPoint", r.initPoint()
    );
  }

  @GetMapping("/status/{id}")
  public Map<String, Object> status(@PathVariable String id) {
    return Map.of("status", gateway.getStatus(id).name());
  }

   @PostMapping("/create-preference/event/{eventId}")
  public Map<String, Object> createForEvent(

  @PathVariable Long eventId,
    @RequestParam Long tipoEntradaId,
    @RequestParam(defaultValue = "1") int qty,
    @RequestParam BigDecimal precio,
    HttpServletRequest request
  ) throws Exception {

    if (eventId == null || eventId <= 0) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "eventoId inválido");
    }

    qty = Math.max(qty, 1);

    if (precio == null || precio.compareTo(BigDecimal.ZERO) <= 0) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "precio inválido");
    }

    precio = precio.setScale(2, RoundingMode.HALF_UP);

    String base = ServletUriComponentsBuilder
      .fromRequest(request)
      .replacePath(null)
      .replaceQuery(null)
      .build()
      .toUriString();

    String username = SecurityContextHolder
      .getContext()
      .getAuthentication()
      .getName();

    Long usuarioId = usuarioRepository
      .findByUsername(username)
      .orElseThrow(() -> new RuntimeException("Usuario no encontrado"))
      .getId();


    var cmd = new CreatePrefCommand(
      "EVT-" + eventId,
      usuarioId,        // ✅ YA NO ES NULL
      eventId,
      tipoEntradaId,
      List.of(
        new CreatePrefCommand.Item(
          String.valueOf(tipoEntradaId),
          "Entrada",
          "Show",
          qty,
          precio
        )
      )
    );


    var r = gateway.createPreferenceWithBase(cmd, base);
    return Map.of(
      "preferenceId", r.preferenceId(),
      "initPoint", r.initPoint()
    );
  }
}



