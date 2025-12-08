package org.example.escenalocal.dtos.post;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class PostPaymentInfoDto {
  private Long usuarioId;
  private Long eventoId;
  private Long tipoEntradaId;
  private int cantidad;
  private double precio;
  private String status;

  public PostPaymentInfoDto(Long paymentId, String status, Long usuarioId, Long eventoId, Long tipoEntradaId, Integer cantidad, BigDecimal precio) {
  }
}
