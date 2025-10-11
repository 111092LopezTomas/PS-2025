package org.example.escenalocal.dtos.get;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class GetProductorDto {

    private Long id;

    private String nombre;

    private String representante;

    private String telefono_representante;

    private String red_social;
}
