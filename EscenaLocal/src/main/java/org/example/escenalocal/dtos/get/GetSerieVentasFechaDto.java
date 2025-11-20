package org.example.escenalocal.dtos.get;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class GetSerieVentasFechaDto {
  private LocalDate fecha;
  private int cantidad;
}
