package org.example.escenalocal.controllers;

import lombok.RequiredArgsConstructor;
import org.example.escenalocal.dashboard.EntradaCompradaDto;
import org.example.escenalocal.services.TicketsHistorialService;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/tickets")
@RequiredArgsConstructor
public class TicketsHistorialController {

  private final TicketsHistorialService ticketsHistorialService;

  @GetMapping("/mis-compras")
  public List<EntradaCompradaDto> misCompras(Authentication auth) {
    return ticketsHistorialService.misCompras(auth);
  }
}
