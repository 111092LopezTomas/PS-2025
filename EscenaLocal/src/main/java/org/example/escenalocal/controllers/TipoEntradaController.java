package org.example.escenalocal.controllers;

import lombok.RequiredArgsConstructor;
import org.example.escenalocal.dtos.get.GetTipoEntradaDto;
import org.example.escenalocal.services.ProductorService;
import org.example.escenalocal.services.TipoEntradaService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/entradas")
@RequiredArgsConstructor
public class TipoEntradaController {

  private final TipoEntradaService tipoEntradaService;

  @GetMapping("/all")
  public ResponseEntity<List<GetTipoEntradaDto>> getAllTipoEntrada() {

    List<GetTipoEntradaDto> tipos = tipoEntradaService.getTipoEntrada();

    return ResponseEntity.ok(tipos);
  }

  @GetMapping("/{id}")
  public ResponseEntity<GetTipoEntradaDto> getTipoEntrada(@PathVariable Long id) {
    GetTipoEntradaDto getTipoEntradaDto = tipoEntradaService.getTipoEntradaById(id);

    return ResponseEntity.ok(getTipoEntradaDto);
  }
}
