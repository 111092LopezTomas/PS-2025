package org.example.escenalocal.dashboard;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class EntradasPorTipoDto {

  private String tipoEntradaNombre;
  private Long cantidadVendida;

  public EntradasPorTipoDto(String tipoEntradaNombre, Long cantidadVendida) {
    this.tipoEntradaNombre = tipoEntradaNombre;
    this.cantidadVendida = cantidadVendida;
  }
}
