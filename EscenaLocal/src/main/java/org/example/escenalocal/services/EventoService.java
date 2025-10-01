package org.example.escenalocal.services;

import org.example.escenalocal.dtos.get.EventoDetalleProj;
import org.example.escenalocal.dtos.get.GetEventoDto;
import org.example.escenalocal.dtos.post.PostEventoDto;
import org.example.escenalocal.dtos.put.PutEventoDto;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface EventoService {

    GetEventoDto getEventoById(Long id);
    List<GetEventoDto> getEventos();
    GetEventoDto createEvento(PostEventoDto postEventoDto);
    GetEventoDto updateEvento(Long id, PutEventoDto putEventoDto);
    void deleteEvento(Long id);
}
