package org.example.escenalocal.dtos.post;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class PostEntradaDetalleDto {

  @NotNull(message = "El tipo de entrada no puede ser nulo")
  private Long tipo;

  @NotNull(message = "El precio no puede ser nulo")
  @DecimalMin(value = "0.0", inclusive = true, message = "El precio no puede ser negativo")
  private BigDecimal precio;

  @NotNull(message = "La disponibilidad no puede ser nula")
  @Min(value = 0, message = "La disponibilidad no puede ser negativa")
  private Integer disponibilidad;
}
