package org.example.escenalocal.controllers;

import lombok.RequiredArgsConstructor;
import org.example.escenalocal.dtos.get.GetArtistaDto;
import org.example.escenalocal.services.ArtistaService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/artistas")
@RequiredArgsConstructor
public class ArtistaController {

    private final ArtistaService artistaService;

    @GetMapping("/{id}")
    public ResponseEntity<GetArtistaDto> getArtista(@PathVariable Long id) {
        GetArtistaDto getArtistaDto = artistaService.getArtistaById(id);
        if (getArtistaDto == null) {
            return ResponseEntity.notFound().build();
        }

        return  ResponseEntity.ok(getArtistaDto);
    }

    @GetMapping("/all")
    public ResponseEntity<List<GetArtistaDto>> getAllArtistas() {
        List<GetArtistaDto> list = artistaService.getArtistas();
        if (list.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(list);
    }
}
