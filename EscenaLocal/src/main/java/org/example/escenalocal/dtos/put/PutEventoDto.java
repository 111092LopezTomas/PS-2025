package org.example.escenalocal.dtos.put;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.util.List;
import java.util.Set;

@Data
public class PutEventoDto {

    @NotNull(message = "El evento no puede ser nulo")
    @Size(min = 1, max = 50, message = "El evento debe tener entre 1 y 50 caracteres")
    private String evento;

    @NotNull(message = "La descripción no puede ser nula")
    @Size(min = 1, max = 500, message = "La descripción debe tener entre 1 y 500 caracteres")
    private String descripcion;

    private String fecha;

    private String hora;

    @NotNull(message = "El estado no puede ser nulo")
    private Boolean activo;

    @NotNull(message = "El establecimiento no puede ser nulo")
    @Size(min = 1, max = 50, message = "El establecimiento debe tener entre 1 y 50 caracteres")
    private String establecimiento;

    @NotNull(message = "La clasificacion no puede ser nula")
    @Size(min = 1, max = 5, message = "La clasificacion debe tener entre 1 y 5 caracteres")
    private String clasificacion;

    private Set<Long> artistas;

    private Set<Long> entradas;
}
