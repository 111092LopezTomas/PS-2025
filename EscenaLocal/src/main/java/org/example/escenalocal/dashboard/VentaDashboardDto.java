package org.example.escenalocal.dashboard;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class VentaDashboardDto {

  private LocalDateTime fechaCompra;
  private String usuarioNombre;
  private String usuarioEmail;
  private Long eventoId;
  private String eventoNombre;
  private String tipoEntradaNombre;
  private Integer cantidad;
  private BigDecimal total;
  private String estadoPago;
  private Long paymentId;
}
