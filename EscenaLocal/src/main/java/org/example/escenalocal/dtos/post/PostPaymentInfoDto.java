package org.example.escenalocal.dtos.post;

import lombok.Data;
import java.math.BigDecimal;

@Data
public class PostPaymentInfoDto {

  private Long paymentId;
  private String status;
  private Long usuarioId;
  private Long eventoId;
  private Long tipoEntradaId;
  private Integer cantidad;
  private BigDecimal precio;
  private String externalReference;
  private String statusDetail;

  public PostPaymentInfoDto(
    Long paymentId,
    String status,
    Long usuarioId,
    Long eventoId,
    Long tipoEntradaId,
    Integer cantidad,
    BigDecimal precio,
    String externalReference,
    String statusDetail
  ) {
    this.paymentId = paymentId;
    this.status = status;
    this.usuarioId = usuarioId;
    this.eventoId = eventoId;
    this.tipoEntradaId = tipoEntradaId;
    this.cantidad = cantidad;
    this.precio = precio;
    this.externalReference = externalReference;
    this.statusDetail = statusDetail;
  }
}

