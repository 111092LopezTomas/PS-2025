package org.example.escenalocal.services.impl;

import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.example.escenalocal.dtos.get.EventoDetalleProj;
import org.example.escenalocal.dtos.get.GetEventoDto;
import org.example.escenalocal.dtos.post.PostEventoDto;
import org.example.escenalocal.dtos.put.PutEventoDto;
import org.example.escenalocal.entities.*;
import org.example.escenalocal.repositories.*;
import org.example.escenalocal.services.EventoService;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
@Data
@RequiredArgsConstructor
public class EventoServiceImpl implements EventoService {

    private final ModelMapper modelMapper =  new ModelMapper();
    private final ArtistaRepository artistaRepository;
    private final EventoRepository eventoRepository;
    private final ClasificacionRepository clasificacionRepository;
    private final EstablecimientoRepository establecimientoRepository;
    private final TiposEntradaRepository tiposEntradaRepository;

//    @Override
//    public GetEventoDto getEvento(Long id) {

//        GetEventoDto getEventoDto = eventoRepository.findEventoById(id);
//
//        //GetEventoDto getEventoDto = modelMapper.map(eventoEntity, GetEventoDto.class);
//        return getEventoDto;

    public GetEventoDto getEventoById(Long id) {
        var e = eventoRepository.findByIdForDto(id)
                .orElseThrow(() -> new jakarta.persistence.EntityNotFoundException("Evento no encontrado: " + id));

        var artistas = e.getArtistasEvento().stream()
                .map(ae -> ae.getArtista() != null ? ae.getArtista().getNombre() : null)
                .filter(n -> n != null && !n.isBlank())
                .distinct()
                .sorted()
                .toList();

        var entradas = e.getEventoTiposEntrada().stream()
                .map(ete -> ete.getTiposEntrada() != null ? ete.getTiposEntrada().getEntrada() : null)
                .filter(t -> t != null && !t.isBlank())
                .distinct()
                .sorted()
                .toList();

        var est = e.getEstablecimiento();
        var barrio = est.getBarrio();
        var ciudad = barrio.getCiudad();
        var provincia = ciudad.getProvincia();

        return new GetEventoDto(
                e.getId(),
                e.getActivo(),
                e.getDescripcion(),
                e.getEvento(),
                e.getFecha(),
                e.getHora(),
                artistas,
                entradas,
                e.getClasificacion().getClasificacion(),
                est.getEstablecimiento(),
                est.getDireccion(),
                ciudad.getCiudad(),
                provincia.getProvincia()
        );
    }


//    @Override
//    public List<GetEventoDto> getEventos() {
//        List<GetEventoDto> list = eventoRepository.findAllEventos();
////        List<GetEventoDto> list = new ArrayList<>();
////        for (EventoEntity eventoEntity : eventoEntities) {
////            GetEventoDto getEventoDto = modelMapper.map(eventoEntity, GetEventoDto.class);
////            list.add(getEventoDto);
////        }
//
//
//        return list;
        @Transactional
        public List<GetEventoDto> getEventos() {
            var eventos = eventoRepository.findAllForDto();
            return eventos.stream()
                    .map(this::toDto)
                    .toList();
        }

        private GetEventoDto toDto(EventoEntity e) {
            var artistas = e.getArtistasEvento().stream()
                    .map(ae -> ae.getArtista().getNombre())
                    .filter(n -> n != null && !n.isBlank())
                    .distinct()
                    .sorted(Comparator.naturalOrder())
                    .toList();

            var entradas = e.getEventoTiposEntrada().stream()
                    .map(ete -> ete.getTiposEntrada().getEntrada())
                    .filter(t -> t != null && !t.isBlank())
                    .distinct()
                    .sorted(Comparator.naturalOrder())
                    .toList();

            var est = e.getEstablecimiento();
            var barrio = est.getBarrio();
            var ciudad = barrio.getCiudad();
            var provincia = ciudad.getProvincia();

            return new GetEventoDto(
                    e.getId(),
                    e.getActivo(),
                    e.getDescripcion(),
                    e.getEvento(),
                    e.getFecha(),
                    e.getHora(),
                    artistas,
                    entradas,
                    e.getClasificacion().getClasificacion(),
                    est.getEstablecimiento(),
                    est.getDireccion(),
                    ciudad.getCiudad(),
                    provincia.getProvincia()
            );
        }


    @Transactional
    public GetEventoDto createEvento(PostEventoDto dto) {

        var evento = new EventoEntity();
        evento.setEvento(dto.getEvento());
        evento.setDescripcion(dto.getDescripcion());
        evento.setFecha(dto.getFecha());
        evento.setHora(dto.getHora());
        evento.setActivo(dto.getActivo());

        var est = establecimientoRepository.findById(dto.getEstablecimiento())
                .orElseThrow(() -> new EntityNotFoundException("Establecimiento no encontrado: " + dto.getEstablecimiento()));
        var clas = clasificacionRepository.findById(dto.getClasificacion())
                .orElseThrow(() -> new EntityNotFoundException("Clasificación no encontrada: " + dto.getClasificacion()));

        evento.setEstablecimiento(est);
        evento.setClasificacion(clas);

        // --- Cargar maestros EXISTENTES
        var artistas = new HashSet<>(artistaRepository.findAllById(dto.getArtistas()));
        var tipos    = new HashSet<>(tiposEntradaRepository.findAllById(dto.getEntradas()));

        // --- Validar faltantes (evita 500 por FK)
        var okA = artistas.stream().map(ArtistaEntity::getId).collect(Collectors.toSet());
        var faltA = dto.getArtistas().stream().filter(id -> !okA.contains(id)).toList();
        if (!faltA.isEmpty()) throw new EntityNotFoundException("Artistas no encontrados: " + faltA);

        var okT = tipos.stream().map(TiposEntradaEntity::getId).collect(Collectors.toSet());
        var faltT = dto.getEntradas().stream().filter(id -> !okT.contains(id)).toList();
        if (!faltT.isEmpty()) throw new EntityNotFoundException("Tipos de entrada no encontrados: " + faltT);

        // --- Construir join-entities (sin placeholders, sin cascade en maestros)
        for (ArtistaEntity a : artistas) {
            var join = new ArtistaEventoEntity();
            join.setEvento(evento);
            join.setArtista(a);
            join.setId(new ArtistaEventoId(null, a.getId())); // eventoId se completa al persistir
            evento.getArtistasEvento().add(join);
            a.getArtistaEventos().add(join); // opcional, para mantener la bidireccionalidad en memoria
        }

        for (TiposEntradaEntity t : tipos) {
            var join = new EventoTiposEntradaEntity();
            join.setEvento(evento);
            join.setTiposEntrada(t);
            join.setId(new EventoTiposEntradaId(null, t.getId())); // eventoId se completa al persistir
            evento.getEventoTiposEntrada().add(join);
            t.getEventoTipos().add(join); // opcional
        }

        // --- Persistir (cascade = ALL desde Evento → inserta en tablas puente)
        var saved = eventoRepository.save(evento);

        // map a tu DTO de salida
        return modelMapper.map(saved, GetEventoDto.class);


    }

    @Override
    public GetEventoDto updateEvento(Long id, PutEventoDto putEventoDto) {
        return null;
    }

    @Override
    public void deleteEvento(Long id) {

    }
}
