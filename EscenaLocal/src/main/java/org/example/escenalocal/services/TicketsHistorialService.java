package org.example.escenalocal.services;

import org.example.escenalocal.dashboard.EntradaCompradaDto;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface TicketsHistorialService {
  List<EntradaCompradaDto> misCompras(Authentication auth);
}
