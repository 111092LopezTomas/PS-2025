package org.example.escenalocal.dtos.get;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class GetArtProdDto {

    private Long id;

    private Long idArtista;

    private Long idProductor;

    private String nombre;

    private String representante;

    private String telefono_representante;

    private String red_social;

    private Long idGenero;

    private String tipo;

    private String username;

    private String email;

    private String password;

    private String imagenNombre;

    private String imagenContentType;

    private byte[] imagenDatos;

    private Long imagenTamano;

    private String rol;

  private String generoNombre;
}
