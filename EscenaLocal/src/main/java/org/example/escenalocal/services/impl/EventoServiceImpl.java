package org.example.escenalocal.services.impl;

import io.micrometer.common.lang.Nullable;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.example.escenalocal.dtos.get.GetEntradaDto;
import org.example.escenalocal.dtos.get.GetEventoDto;
import org.example.escenalocal.dtos.post.PostEntradaDetalleDto;
import org.example.escenalocal.dtos.post.PostEventoDto;
import org.example.escenalocal.dtos.put.PutEventoDto;
import org.example.escenalocal.entities.*;
import org.example.escenalocal.repositories.*;
import org.example.escenalocal.services.EventoService;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalTime;
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
    private final ProductorRepository productorRepository;
    private final EventoTiposEntradaRepository eventoTiposEntradaRepository;

    public GetEventoDto getEventoById(Long id) {
      var e = eventoRepository.findByIdForDto(id)
        .orElseThrow(() -> new jakarta.persistence.EntityNotFoundException("Evento no encontrado: " + id));

      var artistas = e.getArtistasEvento().stream()
        .map(ae -> ae.getArtista() != null ? ae.getArtista().getNombre() : null)
        .filter(n -> n != null && !n.isBlank())
        .distinct()
        .sorted()
        .toList();

      var entradasDetalle = e.getEventoTiposEntrada().stream()
        .map(ete -> {
          var tipo = ete.getTiposEntrada() != null ? ete.getTiposEntrada().getEntrada() : null;
          var precio = ete.getPrecio();
          var disponibilidad = ete.getDisponibilidad();
          return new GetEntradaDto(tipo, precio, disponibilidad);
        })
        .filter(dto -> dto.getTipo() != null && !dto.getTipo().isBlank())
        .sorted(Comparator.comparing(GetEntradaDto::getTipo))
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
        entradasDetalle,
        e.getClasificacion().getClasificacion(),
        e.getProductor().getNombre(),
        est.getEstablecimiento(),
        est.getDireccion(),
        ciudad.getCiudad(),
        provincia.getProvincia()
      );
    }

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

    var entradasDetalle = e.getEventoTiposEntrada().stream()
      .map(ete -> new GetEntradaDto(
        ete.getTiposEntrada().getEntrada(),
        ete.getPrecio(),
        ete.getDisponibilidad()
      ))
      .filter(dto -> dto.getTipo() != null && !dto.getTipo().isBlank())
      .sorted(Comparator.comparing(GetEntradaDto::getTipo))
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
      entradasDetalle,
      e.getClasificacion().getClasificacion(),
      e.getProductor().getNombre(),
      est.getEstablecimiento(),
      est.getDireccion(),
      ciudad.getCiudad(),
      provincia.getProvincia()
    );
  }


    @Transactional
    public GetEventoDto createEvento(PostEventoDto dto, @Nullable MultipartFile file) {

      var evento = new EventoEntity();
      evento.setEvento(dto.getEvento());
      evento.setDescripcion(dto.getDescripcion());

      if (dto.getFecha() != null && !dto.getFecha().isBlank()) {
        evento.setFecha(LocalDate.parse(dto.getFecha()));
      }
      if (dto.getHora() != null && !dto.getHora().isBlank()) {
        evento.setHora(LocalTime.parse(dto.getHora()));
      }
      evento.setActivo(Boolean.TRUE.equals(dto.getActivo()));

      // ---------- Relaciones obligatorias ----------
      var est = establecimientoRepository.findById(dto.getEstablecimientoId())
        .orElseThrow(() -> new EntityNotFoundException("Establecimiento no encontrado: " + dto.getEstablecimientoId()));

      var clas = clasificacionRepository.findById(dto.getClasificacionId())
        .orElseThrow(() -> new EntityNotFoundException("Clasificación no encontrada: " + dto.getClasificacionId()));

      evento.setEstablecimiento(est);
      evento.setClasificacion(clas);

      if (dto.getProductorId() != null) {
        var prod = productorRepository.findById(dto.getProductorId())
          .orElseThrow(() -> new EntityNotFoundException("Productor no encontrado: " + dto.getProductorId()));
        evento.setProductor(prod);
      }

      // ---------- Imagen opcional ----------
      if (file != null && !file.isEmpty()) {
        String ct = file.getContentType() != null ? file.getContentType() : "application/octet-stream";
        if (!ct.startsWith("image/")) {
          throw new IllegalArgumentException("El archivo debe ser una imagen. Content-Type recibido: " + ct);
        }
        long maxBytes = 10L * 1024 * 1024;
        if (file.getSize() > maxBytes) {
          throw new IllegalArgumentException("La imagen excede el tamaño máximo de 10MB");
        }
        try {
          evento.setImagenNombre(file.getOriginalFilename() != null ? file.getOriginalFilename() : "archivo");
          evento.setImagenContentType(ct);
          evento.setImagenDatos(file.getBytes());
          evento.setImagenTamano(file.getSize());
        } catch (IOException e) {
          throw new RuntimeException("No se pudo leer la imagen: " + e.getMessage(), e);
        }
      }

      // ---------- Artistas ----------
      if (dto.getArtistaId() != null && !dto.getArtistaId().isEmpty()) {
        var artistas = new HashSet<>(artistaRepository.findAllById(dto.getArtistaId()));
        var okA = artistas.stream().map(ArtistaEntity::getId).collect(Collectors.toSet());
        var faltA = dto.getArtistaId().stream().filter(id -> !okA.contains(id)).toList();
        if (!faltA.isEmpty()) {
          throw new EntityNotFoundException("Artistas no encontrados: " + faltA);
        }

        for (ArtistaEntity a : artistas) {
          var join = new ArtistaEventoEntity();
          join.setEvento(evento);
          join.setArtista(a);
          join.setId(new ArtistaEventoId(null, a.getId()));
          evento.getArtistasEvento().add(join);
          a.getArtistaEventos().add(join);
        }
      }

      // ---------- Tipos de entrada + precio/disponibilidad ----------
      if (dto.getEntradasDetalle() != null && !dto.getEntradasDetalle().isEmpty()) {

        var idsTipos = dto.getEntradasDetalle().stream()
          .map(PostEntradaDetalleDto::getTipo)
          .filter(Objects::nonNull)
          .collect(Collectors.toSet());

        var tiposExistentes = new HashMap<Long, TiposEntradaEntity>();
        tiposEntradaRepository.findAllById(idsTipos).forEach(t -> tiposExistentes.put(t.getId(), t));

        var faltT = idsTipos.stream().filter(id -> !tiposExistentes.containsKey(id)).toList();
        if (!faltT.isEmpty()) {
          throw new EntityNotFoundException("Tipos de entrada no encontrados: " + faltT);
        }

        for (PostEntradaDetalleDto det : dto.getEntradasDetalle()) {
          var tipo = tiposExistentes.get(det.getTipo());
          var join = new EventoTiposEntradaEntity();
          join.setEvento(evento);
          join.setTiposEntrada(tipo);
          join.setId(new EventoTiposEntradaId(null, tipo.getId()));

          join.setPrecio(det.getPrecio());
          join.setDisponibilidad(det.getDisponibilidad()); // número entero

          evento.getEventoTiposEntrada().add(join);
          tipo.getEventoTipos().add(join);
        }
      }

      var saved = eventoRepository.save(evento);
      return modelMapper.map(saved, GetEventoDto.class);
    }


  @Override
    public GetEventoDto updateEvento(Long id, PutEventoDto putEventoDto) {
        return null;
    }

    @Override
    public void deleteEvento(Long id) {

    }

  @Transactional
  public void actualizarImagen(Long idEvento, MultipartFile file) {
    EventoEntity ev = eventoRepository.findById(idEvento)
      .orElseThrow(() -> new RuntimeException("Evento no encontrado: " + idEvento));

    try {
      ev.setImagenNombre(file.getOriginalFilename() != null ? file.getOriginalFilename() : "archivo");
      ev.setImagenContentType(file.getContentType() != null ? file.getContentType() : "application/octet-stream");
      ev.setImagenDatos(file.getBytes());                 // <-- byte[]
      ev.setImagenTamano(file.getSize());                 // <-- opcional
      // al estar dentro de una transacción, el save puede ser implícito
      eventoRepository.save(ev);
    } catch (IOException e) {
      throw new RuntimeException("No se pudo leer el archivo: " + e.getMessage(), e);
    }
  }

  @Transactional
  public EventoEntity obtenerEvento(Long id) {
    return eventoRepository.findById(id)
      .orElseThrow(() -> new RuntimeException("Evento no encontrado: " + id));
  }

  @Transactional
  public void eliminarImagen(Long idEvento) {
    EventoEntity ev = eventoRepository.findById(idEvento)
      .orElseThrow(() -> new RuntimeException("Evento no encontrado: " + idEvento));
    ev.setImagenNombre(null);
    ev.setImagenContentType(null);
    ev.setImagenDatos(null);
    ev.setImagenTamano(null);
    eventoRepository.save(ev);
  }
}
