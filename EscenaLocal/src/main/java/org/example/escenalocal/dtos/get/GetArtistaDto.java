package org.example.escenalocal.dtos.get;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.escenalocal.entities.GeneroEntity;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class GetArtistaDto {

    private Long id;

    private String nombre;

    private String representante;

    private String telefono_representante;

    private String red_social;

    private String genero;

    public GetArtistaDto(Long id, String nombre) {
    }
}
