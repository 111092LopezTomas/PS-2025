package org.example.escenalocal.services;

import org.example.escenalocal.dtos.get.GetEventoDto;
import org.example.escenalocal.dtos.post.PostEventoDto;
import org.example.escenalocal.dtos.put.PutEventoDto;
import org.example.escenalocal.entities.EventoEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Service
public interface EventoService {

    GetEventoDto getEventoById(Long id);
    List<GetEventoDto> getEventos();
    GetEventoDto createEvento(PostEventoDto postEventoDto, MultipartFile file);
    GetEventoDto updateEvento(Long id, PutEventoDto putEventoDto);
    void deleteEvento(Long id);

    void actualizarImagen(Long id, MultipartFile file);

  EventoEntity obtenerEvento(Long id);

  void eliminarImagen(Long id);

  List<GetEventoDto> getEventosByEstablecimientoId(@PathVariable Long id);

  List<GetEventoDto> getEventosByArtistaId(@PathVariable Long id);

  List<GetEventoDto> getEventosByProductorId(@PathVariable Long id);
}
