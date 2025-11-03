package org.example.escenalocal.controllers;

import lombok.RequiredArgsConstructor;
import org.example.escenalocal.dtos.get.GetEstablecimientoDto;
import org.example.escenalocal.dtos.get.GetEventoDto;
import org.example.escenalocal.services.EstablecimientoService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/establecimientos")
@RequiredArgsConstructor
public class EstablecimientoController {

  private final EstablecimientoService establecimientoService;

  @GetMapping("/all")
  public ResponseEntity<List<GetEstablecimientoDto>> getAllEstablecimientos(){
    List<GetEstablecimientoDto> establecimientoDtos = establecimientoService.getEstablecimientos();

    return ResponseEntity.ok(establecimientoDtos);
  }

  @GetMapping("/{id}")
  public ResponseEntity<GetEstablecimientoDto> getEstablecimientoById(@PathVariable Long id){
    GetEstablecimientoDto getEstablecimientoDto =  establecimientoService.getEstablecimientoById(id);
    return ResponseEntity.ok(getEstablecimientoDto);
  }
}
