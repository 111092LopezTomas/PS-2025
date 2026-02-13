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
import org.example.escenalocal.services.NotificacionService;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PathVariable;
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

  private final ModelMapper modelMapper = new ModelMapper();
  private final ArtistaRepository artistaRepository;
  private final EventoRepository eventoRepository;
  private final ClasificacionRepository clasificacionRepository;
  private final EstablecimientoRepository establecimientoRepository;
  private final TiposEntradaRepository tiposEntradaRepository;
  private final ProductorRepository productorRepository;
  private final EventoTiposEntradaRepository eventoTiposEntradaRepository;
  private final NotificacionService notificacionService;

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
        var idTipoEntrada = ete.getId().getTiposEntradaId();
        var tipo = ete.getTiposEntrada() != null ? ete.getTiposEntrada().getEntrada() : null;
        var precio = ete.getPrecio();
        var disponibilidad = ete.getDisponibilidad();
        return new GetEntradaDto(idTipoEntrada, tipo, precio, disponibilidad);
      })
      .filter(dto -> dto.getTipo() != null && !dto.getTipo().isBlank())
      .sorted(Comparator.comparing(GetEntradaDto::getTipo))
      .toList();

    var est = e.getEstablecimiento();
    var barrio = est.getBarrio();
    var ciudad = barrio.getCiudad();
    var provincia = ciudad.getProvincia();

    var genero = e.getArtistasEvento().stream()
      .map(ae -> ae.getArtista())
      .filter(Objects::nonNull)
      .map(a -> {
        if (a.getGenero() != null) {
          return a.getGenero().getGenero();
        }
        return null;
      })
      .filter(Objects::nonNull)
      .findFirst()
      .orElse(null);

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
      e.getProductor().getId(),
      e.getProductor().getNombre(),
      est.getId(),
      est.getEstablecimiento(),
      est.getCapacidad(),
      est.getDireccion(),
      est.getBarrio().getBarrio(),
      ciudad.getCiudad(),
      provincia.getProvincia(),
      genero

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
        ete.getTiposEntrada().getId(),
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
    var genero = e.getArtistasEvento().stream()
      .map(ae -> ae.getArtista())
      .filter(Objects::nonNull)
      .map(a -> {
        if (a.getGenero() != null) {
          return a.getGenero().getGenero();
        }
        return null;
      })
      .filter(Objects::nonNull)
      .findFirst()
      .orElse(null);

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
      e.getProductor().getId(),
      e.getProductor().getNombre(),
      est.getId(),
      est.getEstablecimiento(),
      est.getCapacidad(),
      est.getDireccion(),
      est.getBarrio().getBarrio(),
      ciudad.getCiudad(),
      provincia.getProvincia(),
      genero
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

    String nombreEvento = saved.getEvento(); // o getNombre()

    // 1) productor: resolver userId dueño del productor
    Long productorUserId = evento.getProductor().getUsuario().getId();
    notificacionService.createEventoCreadoNotificacion(productorUserId, nombreEvento);

    // 2) artistas: a cada artista (usuario dueño del artista)
    List<ArtistaEventoEntity> artistas = evento.getArtistasEvento().stream().toList();
    for (ArtistaEventoEntity a : artistas) {
      Long artistaUserId = a.getArtista().getUsuario().getId();

      // (opcional) evitar duplicado si justo coincide con productorUserId
      if (!artistaUserId.equals(productorUserId)) {
        notificacionService.createArtistaIncluidoEnEventoNotificacion(artistaUserId, nombreEvento);
      }
    }

    return modelMapper.map(saved, GetEventoDto.class);
  }


  @Override
  @Transactional
  public GetEventoDto updateEvento(Long id, PutEventoDto dto) {
    // 1) Traer evento con sus colecciones (lazy ok dentro de @Transactional)
    var ev = eventoRepository.findById(id)
      .orElseThrow(() -> new EntityNotFoundException("Evento no encontrado: " + id));

    // 2) Campos simples (solo si el dto trae valor)
    if (dto.getEvento() != null) {
      ev.setEvento(dto.getEvento());
    }
    if (dto.getDescripcion() != null) {
      ev.setDescripcion(dto.getDescripcion());
    }
    if (dto.getActivo() != null) {
      ev.setActivo(Boolean.TRUE.equals(dto.getActivo()));
    }

    // fecha: null -> no tocar; "" -> limpiar; "yyyy-MM-dd" -> setear
    if (dto.getFecha() != null) {
      if (dto.getFecha().isBlank()) {
        ev.setFecha(null);
      } else {
        ev.setFecha(LocalDate.parse(dto.getFecha()));
      }
    }

    // hora: null -> no tocar; "" -> limpiar; "HH:mm[:ss]" -> setear
    if (dto.getHora() != null) {
      if (dto.getHora().isBlank()) {
        ev.setHora(null);
      } else {
        ev.setHora(LocalTime.parse(dto.getHora()));
      }
    }

    // 3) Relaciones "single-valued" si vienen en el DTO
    if (dto.getEstablecimientoId() != null) {
      var est = establecimientoRepository.findById(dto.getEstablecimientoId())
        .orElseThrow(() -> new EntityNotFoundException("Establecimiento no encontrado: " + dto.getEstablecimientoId()));
      ev.setEstablecimiento(est);
    }

    if (dto.getClasificacionId() != null) {
      var clas = clasificacionRepository.findById(dto.getClasificacionId())
        .orElseThrow(() -> new EntityNotFoundException("Clasificación no encontrada: " + dto.getClasificacionId()));
      ev.setClasificacion(clas);
    }

    if (dto.getProductorId() != null) {
      var prod = productorRepository.findById(dto.getProductorId())
        .orElseThrow(() -> new EntityNotFoundException("Productor no encontrado: " + dto.getProductorId()));
      ev.setProductor(prod);
    }

    // 4) Relaciones "collection-valued" (reconciliar si vienen en el DTO)
    if (dto.getArtistaId() != null) {
      reconciliarArtistas(ev, new HashSet<>(dto.getArtistaId()));
    }

    if (dto.getEntradasDetalle() != null) {
      reconciliarEntradas(ev, new HashSet<>(dto.getEntradasDetalle()));
    }

    // 5) Persistir y devolver DTO consistente
    var saved = eventoRepository.save(ev);
    return toDto(saved); // usa tu método que arma GetEventoDto completo
  }

  /**
   * Reemplaza los artistas del evento por exactamente los que se pasen en artistaIds.
   * Elimina los que sobran y crea los que faltan.
   */
  private void reconciliarArtistas(EventoEntity ev, Set<Long> artistaIds) {
    // IDs actuales en el evento
    var actuales = ev.getArtistasEvento().stream()
      .map(ae -> ae.getArtista().getId())
      .collect(Collectors.toSet());

    // --- Eliminar los que ya no vienen ---
    var iterator = ev.getArtistasEvento().iterator();
    while (iterator.hasNext()) {
      var join = iterator.next();
      Long aid = join.getArtista().getId();
      if (!artistaIds.contains(aid)) {
        // quitar de ambos lados
        join.getArtista().getArtistaEventos().remove(join);
        iterator.remove();
      }
    }

    // --- Agregar los nuevos que faltan ---
    var nuevosIds = artistaIds.stream()
      .filter(id -> !actuales.contains(id))
      .collect(Collectors.toSet());

    if (!nuevosIds.isEmpty()) {
      var nuevosArtistas = new HashSet<>(artistaRepository.findAllById(nuevosIds));
      var encontrados = nuevosArtistas.stream().map(ArtistaEntity::getId).collect(Collectors.toSet());
      var faltantes = nuevosIds.stream().filter(id -> !encontrados.contains(id)).toList();
      if (!faltantes.isEmpty()) {
        throw new EntityNotFoundException("Artistas no encontrados: " + faltantes);
      }

      for (var artista : nuevosArtistas) {
        var join = new ArtistaEventoEntity();
        join.setEvento(ev);
        join.setArtista(artista);
        join.setId(new ArtistaEventoId(ev.getId(), artista.getId())); // si ev.getId() es null, JPA lo completará al persistir
        ev.getArtistasEvento().add(join);
        artista.getArtistaEventos().add(join);
      }
    }
  }

  /**
   * Reemplaza los tipos de entrada del evento por exactamente los que lleguen en 'detalles'.
   * Hace upsert de precio/disponibilidad y elimina los tipos que ya no estén.
   */
  private void reconciliarEntradas(EventoEntity ev, Set<PostEntradaDetalleDto> detalles) {
    // Map de tipoId -> detalle entrante
    Map<Long, PostEntradaDetalleDto> entrantes = detalles.stream()
      .filter(d -> d.getTipo() != null)
      .collect(Collectors.toMap(PostEntradaDetalleDto::getTipo, d -> d, (a, b) -> b));

    // Validar tipos existentes en DB
    var idsTipos = entrantes.keySet();
    var tiposExistentes = new HashMap<Long, TiposEntradaEntity>();
    tiposEntradaRepository.findAllById(idsTipos).forEach(t -> tiposExistentes.put(t.getId(), t));

    var faltantes = idsTipos.stream().filter(id -> !tiposExistentes.containsKey(id)).toList();
    if (!faltantes.isEmpty()) {
      throw new EntityNotFoundException("Tipos de entrada no encontrados: " + faltantes);
    }

    // Map de tipoId -> join actual
    Map<Long, EventoTiposEntradaEntity> actuales = ev.getEventoTiposEntrada().stream()
      .collect(Collectors.toMap(eje -> eje.getTiposEntrada().getId(), eje -> eje));

    // --- Eliminar los que ya no vienen ---
    var it = ev.getEventoTiposEntrada().iterator();
    while (it.hasNext()) {
      var join = it.next();
      Long tipoId = join.getTiposEntrada().getId();
      if (!entrantes.containsKey(tipoId)) {
        // quitar de ambos lados
        join.getTiposEntrada().getEventoTipos().remove(join);
        it.remove();
        // Si manejás repositorio para join y no hay orphanRemoval, podrías hacer:
        // eventoTiposEntradaRepository.delete(join);
      }
    }

    // --- Upsert de los que vienen ---
    for (var entry : entrantes.entrySet()) {
      Long tipoId = entry.getKey();
      var det = entry.getValue();

      if (actuales.containsKey(tipoId)) {
        // update
        var join = actuales.get(tipoId);
        join.setPrecio(det.getPrecio());
        join.setDisponibilidad(det.getDisponibilidad());
      } else {
        // insert
        var tipo = tiposExistentes.get(tipoId);
        var join = new EventoTiposEntradaEntity();
        join.setEvento(ev);
        join.setTiposEntrada(tipo);
        join.setId(new EventoTiposEntradaId(ev.getId(), tipo.getId()));
        join.setPrecio(det.getPrecio());
        join.setDisponibilidad(det.getDisponibilidad());

        ev.getEventoTiposEntrada().add(join);
        tipo.getEventoTipos().add(join);
      }
    }
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

  @Override
  @Transactional
  public List<GetEventoDto> getEventosByEstablecimientoId(Long id) {

    var eventos = eventoRepository.findActivosByEstablecimientoId(id);
    return eventos.stream()
      .map(this::toDto)
      .toList();
  }

  @Override
  @Transactional
  public List<GetEventoDto> getEventosByArtistaId(Long id) {

    var eventos = eventoRepository.findActivosByArtistaId(id);
    return eventos.stream()
      .map(this::toDto)
      .toList();
  }

  @Override
  @Transactional
  public List<GetEventoDto> getEventosByProductorId(@PathVariable Long id) {
    var eventos = eventoRepository.findActivosByProductorId(id);
    return eventos.stream()
      .map(this::toDto)
      .toList();
  }
}
