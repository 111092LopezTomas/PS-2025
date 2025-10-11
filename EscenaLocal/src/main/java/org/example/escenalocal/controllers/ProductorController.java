package org.example.escenalocal.controllers;

import lombok.RequiredArgsConstructor;
import org.example.escenalocal.dtos.get.GetProductorDto;
import org.example.escenalocal.services.ProductorService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/productores")
@RequiredArgsConstructor
public class ProductorController {

  private final ProductorService productorService;

  @GetMapping("/all")
  public ResponseEntity<List<GetProductorDto>> getAll() {

    List<GetProductorDto> list = productorService.getProductores();
    return ResponseEntity.ok(list);
  }

  @GetMapping("/{id}")
  public ResponseEntity<GetProductorDto> get(@PathVariable Long id) {
    GetProductorDto productor = productorService.getProductorById(id);

    return ResponseEntity.ok(productor);
  }
}
