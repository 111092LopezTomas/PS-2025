package org.example.escenalocal.dtos.post;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.Getter;
import org.springframework.cglib.core.Local;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Set;

@Data
public class PostEventoDto {

  @NotNull @Size(min = 1, max = 50)
  private String evento;

  @NotNull @Size(min = 1, max = 500)
  private String descripcion;

  private String fecha;
  private String hora;

  @NotNull
  private Boolean activo;

  private Set<PostEntradaDetalleDto> entradasDetalle;

  @NotNull
  private Long establecimientoId;

  @NotNull
  private Long clasificacionId;

  private Set<Long> artistaId;

  private Long productorId;

}
