package org.example.escenalocal.controllers;

import lombok.RequiredArgsConstructor;
import org.example.escenalocal.dtos.get.GetGeneroDto;
import org.example.escenalocal.services.GeneroService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/generos")
public class GeneroController {

  private final GeneroService generoService;

  @GetMapping("/all")
  public ResponseEntity<List<GetGeneroDto>> getGeneros(){

    List<GetGeneroDto> getGeneroDto = generoService.getGeneros();
    return ResponseEntity.ok(getGeneroDto);
  }

  @GetMapping("/{id}")
  public ResponseEntity<GetGeneroDto> getGenero(@PathVariable Long id){
    GetGeneroDto getGeneroDto = generoService.getGenero(id);
    return ResponseEntity.ok(getGeneroDto);
  }
}
