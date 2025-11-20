package org.example.escenalocal.dtos.post;
import lombok.Data;

@Data
public class PostPaymentInfoDto {
  private Long usuarioId;
  private Long eventoId;
  private Long tipoEntradaId;
  private int cantidad;
  private double precio;
  private String status;
}
