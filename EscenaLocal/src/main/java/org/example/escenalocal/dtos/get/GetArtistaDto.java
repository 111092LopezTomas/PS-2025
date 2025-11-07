package org.example.escenalocal.dtos.get;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.escenalocal.entities.GeneroEntity;
import org.example.escenalocal.entities.RolEntity;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

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
}
