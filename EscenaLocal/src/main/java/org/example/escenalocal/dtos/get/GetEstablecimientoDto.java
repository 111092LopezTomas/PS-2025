package org.example.escenalocal.dtos.get;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.escenalocal.entities.BarrioEntity;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class GetEstablecimientoDto {

  private Long id;

  private String establecimiento;

  private Integer capacidad;

  private String direccion;

  private String barrio;

  private String ciudad;

  private String provincia;
}
