package org.example.escenalocal.services.impl;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.example.escenalocal.auth.repository.UserRepository;
import org.example.escenalocal.dashboard.EntradaCompradaDto;
import org.example.escenalocal.repositories.VentaEntradaRepository;
import org.example.escenalocal.services.TicketsHistorialService;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Data
public class TicketsHistorialServiceImpl implements TicketsHistorialService {

  private final VentaEntradaRepository ventaRepo;
  private final UserRepository userRepo;

  public List<EntradaCompradaDto> misCompras(Authentication auth) {

    String username = auth.getName();

    Long usuarioId = userRepo.findByUsername(username)
      .orElseThrow(() -> new RuntimeException("Usuario no encontrado"))
      .getId();

    return ventaRepo.historialComprasPorUsuario(usuarioId);
  }
}
