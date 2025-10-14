package org.example.escenalocal.dtos.post;

import lombok.Data;

@Data
public class PostEntradaDto {
  private String tipo;
  private Double precio;
  private Integer disponibilidad;
}
