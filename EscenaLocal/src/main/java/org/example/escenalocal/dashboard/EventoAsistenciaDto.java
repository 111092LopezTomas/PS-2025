package org.example.escenalocal.dashboard;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EventoAsistenciaDto {
  private Long eventoId;
  private String eventoNombre;
  private Long entradasVendidas;
}

