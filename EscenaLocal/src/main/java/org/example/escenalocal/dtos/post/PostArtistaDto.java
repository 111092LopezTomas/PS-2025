package org.example.escenalocal.dtos.post;

import lombok.Data;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

@Data
public class PostArtistaDto {

    @NotNull(message = "El nombre no puede ser nulo")
    @Size(min = 1, max = 50, message = "El nombre debe tener entre 1 y 50 caracteres")
    private String nombre;

    @NotNull(message = "El representante no puede ser nulo")
    @Size(min = 1, max = 50, message = "El representante debe tener entre 1 y 50 caracteres")
    private String representante;

    @Size(min = 10, max = 20, message = "El numero de teléfono debe tener un formato valido")
    private String telefono_representante;

    @NotNull(message = "La red social no puede ser nula")
    @Size(min = 1, max = 50, message = "La red social debe tener entre 1 y 50 caracteres")
    private String red_social;

    @NotNull(message = "El genero no puede ser nulo")
//    @Size(min = 1, max = 50, message = "El genero debe tener entre 1 y 50 caracteres")
    private Long genero;

}
