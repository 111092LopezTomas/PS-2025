package org.example.escenalocal.dashboard;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EntradaCompradaDto {

  private Long ventaId;

  private Long eventoId;
  private String eventoNombre;
  private LocalDate fechaEvento;

  private String establecimientoNombre;

  private String tipoEntrada;
  private Integer cantidad;
  private BigDecimal precioUnitario;

  private BigDecimal montoTotal;

  private String estadoPago;
  private Long paymentId;
  private String externalReference;

  private LocalDateTime fechaCompra;

  public EntradaCompradaDto(
    Long ventaId,
    Long eventoId,
    String eventoNombre,
    LocalDate fechaEvento,
    String establecimientoNombre,
    String tipoEntrada,
    Integer cantidad,
    BigDecimal precioUnitario,
    String estadoPago,
    Long paymentId,
    String externalReference,
    LocalDateTime fechaCompra
  ) {
    this.ventaId = ventaId;
    this.eventoId = eventoId;
    this.eventoNombre = eventoNombre;
    this.fechaEvento = fechaEvento;
    this.establecimientoNombre = establecimientoNombre;
    this.tipoEntrada = tipoEntrada;
    this.cantidad = cantidad;
    this.precioUnitario = precioUnitario;
    this.estadoPago = estadoPago;
    this.paymentId = paymentId;
    this.externalReference = externalReference;
    this.fechaCompra = fechaCompra;

    if (precioUnitario != null && cantidad != null) {
      this.montoTotal = precioUnitario.multiply(BigDecimal.valueOf(cantidad));
    } else {
      this.montoTotal = BigDecimal.ZERO;
    }
  }
}
