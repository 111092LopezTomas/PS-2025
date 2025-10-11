package org.example.escenalocal.controllers;

import lombok.RequiredArgsConstructor;
import org.example.escenalocal.dtos.get.GetClasificacionDto;
import org.example.escenalocal.services.ClasificacionService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/clasificaciones")
@RequiredArgsConstructor
public class ClasificacionController {

  private final ClasificacionService clasificacionService;

  @GetMapping("/all")
  public ResponseEntity<List<GetClasificacionDto>> getAllClasificacion(){

    List<GetClasificacionDto> clasificaciones = clasificacionService.getClasificaciones();
    return ResponseEntity.ok(clasificaciones);
  }

  @GetMapping("/{id}")
  public ResponseEntity<GetClasificacionDto> getClasificacionById(@PathVariable Long id){
    GetClasificacionDto getClasificacionDto = clasificacionService.getClasificacionById(id);
    return ResponseEntity.ok(getClasificacionDto);
  }
}
