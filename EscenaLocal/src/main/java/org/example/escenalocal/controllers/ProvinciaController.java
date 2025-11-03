package org.example.escenalocal.controllers;

import lombok.RequiredArgsConstructor;
import org.example.escenalocal.dtos.get.GetProvinciaDto;
import org.example.escenalocal.services.ProvinciaService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("provincias")
@RequiredArgsConstructor
public class ProvinciaController {

  private final ProvinciaService provinciaService;

  @GetMapping("all")
  public ResponseEntity<List<GetProvinciaDto>> getAll(){
    List<GetProvinciaDto> lista = provinciaService.getProvincias();
    return ResponseEntity.ok(lista);
  }

  @GetMapping("/{id}")
  public ResponseEntity<GetProvinciaDto> getById(@PathVariable Long id){
    GetProvinciaDto provincia = provinciaService.getProvincia(id);
    return ResponseEntity.ok(provincia);
  }
}
