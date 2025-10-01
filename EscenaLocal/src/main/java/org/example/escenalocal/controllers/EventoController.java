package org.example.escenalocal.controllers;

import lombok.RequiredArgsConstructor;
import org.example.escenalocal.dtos.get.EventoDetalleProj;
import org.example.escenalocal.dtos.get.GetEventoDto;
import org.example.escenalocal.dtos.post.PostEventoDto;
import org.example.escenalocal.services.EventoService;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/eventos")
@RequiredArgsConstructor
public class EventoController {

    private final EventoService eventoService;


    @GetMapping("/{id}")
    public ResponseEntity<GetEventoDto> getEvento(@PathVariable Long id) {

        GetEventoDto getEventoDto = eventoService.getEventoById(id);
        if (getEventoDto == null) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(getEventoDto);
    }

    @GetMapping("/all")
    public ResponseEntity<List<GetEventoDto>> getAllEventos() {
        List<GetEventoDto> list = eventoService.getEventos();
        if (list == null) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(list);
    }

    @PostMapping("/post")
    public ResponseEntity<GetEventoDto> createEvento(@RequestBody PostEventoDto postEventoDto) {
        GetEventoDto getEventoDto = eventoService.createEvento(postEventoDto);
        if (getEventoDto == null) {
            return ResponseEntity.badRequest().build();
        }

        return ResponseEntity.ok(getEventoDto);
    }
}
