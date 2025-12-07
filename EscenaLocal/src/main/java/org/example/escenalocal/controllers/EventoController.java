package org.example.escenalocal.controllers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Encoding;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.RequiredArgsConstructor;
import org.example.escenalocal.dtos.CreateEventoMultipart;
import org.example.escenalocal.dtos.get.GetEventoDto;
import org.example.escenalocal.dtos.post.PostEventoDto;
import org.example.escenalocal.entities.EventoEntity;
import org.example.escenalocal.repositories.EventoRepository;
import org.example.escenalocal.services.EventoService;
import org.springframework.http.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/eventos")
@RequiredArgsConstructor
public class EventoController {

  private final EventoService eventoService;
  private final EventoRepository eventoRepository;

  @GetMapping("/{id}")
  public ResponseEntity<GetEventoDto> getEvento(@PathVariable Long id) {
    GetEventoDto getEventoDto = eventoService.getEventoById(id);
    if (getEventoDto == null) return ResponseEntity.notFound().build();
    return ResponseEntity.ok(getEventoDto);
  }

  @GetMapping("/all")
  public ResponseEntity<List<GetEventoDto>> getAllEventos() {
    List<GetEventoDto> list = eventoService.getEventos();
    if (list == null) return ResponseEntity.notFound().build();
    return ResponseEntity.ok(list);
  }

  @Operation(summary = "Crear evento con imagen (multipart)")
  @io.swagger.v3.oas.annotations.parameters.RequestBody(
    required = true,
    content = @Content(
      mediaType = MediaType.MULTIPART_FORM_DATA_VALUE,
      schema = @Schema(implementation = CreateEventoMultipart.class),
      encoding = {
        // <- fuerza a Swagger a enviar dto como application/json
        @Encoding(name = "dto", contentType = MediaType.APPLICATION_JSON_VALUE),
        @Encoding(name = "file", contentType = MediaType.APPLICATION_OCTET_STREAM_VALUE)
      }
    )
  )
  @PostMapping(path="/nuevo", consumes = MediaType.MULTIPART_FORM_DATA_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
  @PreAuthorize("hasAuthority('ROL_PRODUCTOR')")
  public ResponseEntity<GetEventoDto> crearEvento(
    @RequestPart("dto") String dtoJson,
    @RequestPart(value = "imagen", required = false) MultipartFile imagen
  ) throws JsonProcessingException {

    ObjectMapper mapper = new ObjectMapper();
    PostEventoDto dto = mapper.readValue(dtoJson, PostEventoDto.class);

    var out = eventoService.createEvento(dto, imagen);
    return ResponseEntity.status(HttpStatus.CREATED).body(out);
  }

  @PutMapping(path = "/{id}/imagen", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
  public ResponseEntity<Void> subirImagen(@PathVariable Long id, @RequestPart("file") MultipartFile file) {
    eventoService.actualizarImagen(id, file);
    return ResponseEntity.noContent().build();
  }

  @GetMapping("/{id}/imagen")
  public ResponseEntity<byte[]> verImagen(@PathVariable Long id) {
    EventoEntity ev = eventoService.obtenerEvento(id);
    if (ev.getImagenDatos() == null) return ResponseEntity.notFound().build();
    return ResponseEntity.ok()
      .contentType(MediaType.parseMediaType(
        ev.getImagenContentType() != null ? ev.getImagenContentType() : "application/octet-stream"))
      .body(ev.getImagenDatos());
  }

  @GetMapping("/{id}/imagen/download")
  public ResponseEntity<byte[]> descargarImagen(@PathVariable Long id) {
    EventoEntity ev = eventoService.obtenerEvento(id);
    if (ev.getImagenDatos() == null) return ResponseEntity.notFound().build();
    HttpHeaders headers = new HttpHeaders();
    headers.setContentType(MediaType.parseMediaType(
      ev.getImagenContentType() != null ? ev.getImagenContentType() : "application/octet-stream"));
    String nombre = ev.getImagenNombre() != null ? ev.getImagenNombre() : ("evento-" + id);
    headers.setContentDisposition(ContentDisposition.attachment().filename(nombre).build());
    return new ResponseEntity<>(ev.getImagenDatos(), headers, HttpStatus.OK);
  }

  @DeleteMapping("/{id}/imagen")
  public ResponseEntity<Void> eliminarImagen(@PathVariable Long id) {
    eventoService.eliminarImagen(id);
    return ResponseEntity.noContent().build();
  }

  @Operation(summary = "Actualizar evento existente (multipart)")
  @io.swagger.v3.oas.annotations.parameters.RequestBody(
    required = true,
    content = @Content(
      mediaType = MediaType.MULTIPART_FORM_DATA_VALUE,
      schema = @Schema(implementation = CreateEventoMultipart.class),
      encoding = {
        @Encoding(name = "dto", contentType = MediaType.APPLICATION_JSON_VALUE),
        @Encoding(name = "imagen", contentType = MediaType.APPLICATION_OCTET_STREAM_VALUE)
      }
    )
  )
  @PutMapping(path = "/editar/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
  @PreAuthorize("hasAuthority('ROL_PRODUCTOR')")
  public ResponseEntity<GetEventoDto> actualizarEvento(
    @PathVariable Long id,
    @RequestPart("dto") String dtoJson,
    @RequestPart(value = "imagen", required = false) MultipartFile imagen
  ) throws JsonProcessingException {

    ObjectMapper mapper = new ObjectMapper();
    var dto = mapper.readValue(dtoJson, org.example.escenalocal.dtos.put.PutEventoDto.class);

    var updated = eventoService.updateEvento(id, dto);

    // si también viene imagen, la actualiza aparte
    if (imagen != null && !imagen.isEmpty()) {
      eventoService.actualizarImagen(id, imagen);
      // recargar datos actualizados con imagen incluida
      updated = eventoService.getEventoById(id);
    }

    return ResponseEntity.ok(updated);
  }

  @GetMapping("/establecimientos/{id}")
  public ResponseEntity<List<GetEventoDto>> getEventosByEstablecimientoId(@PathVariable Long id) {
    List<GetEventoDto> eventosDtos = eventoService.getEventosByEstablecimientoId(id);

    return ResponseEntity.ok(eventosDtos);
  }

  @GetMapping("/artistas/{id}")
  public ResponseEntity<List<GetEventoDto>> getEventosByArtistaId(@PathVariable Long id) {
    List<GetEventoDto> eventosDtos = eventoService.getEventosByArtistaId(id);

    return ResponseEntity.ok(eventosDtos);
  }

  @GetMapping("/productores/{id}")
  public ResponseEntity<List<GetEventoDto>> getEventosByProductorId(@PathVariable Long id) {
    List<GetEventoDto> eventosDtos = eventoService.getEventosByProductorId(id);

    return ResponseEntity.ok(eventosDtos);
  }

}
