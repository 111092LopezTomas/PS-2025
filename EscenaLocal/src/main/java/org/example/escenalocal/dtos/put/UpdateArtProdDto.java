package org.example.escenalocal.dtos.put;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UpdateArtProdDto {
  private String username;
  private String email;
  private String password;
  private String nombre;
  private String representante;
  private String telefono_representante;
  private String red_social;
  private Long idGenero;
}
