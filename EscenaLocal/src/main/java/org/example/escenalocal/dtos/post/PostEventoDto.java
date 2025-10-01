package org.example.escenalocal.dtos.post;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.Getter;
import org.springframework.cglib.core.Local;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Set;

@Data
public class PostEventoDto {

    @NotNull(message = "El evento no puede ser nulo")
    @Size(min = 1, max = 50, message = "El evento debe tener entre 1 y 50 caracteres")
    private String evento;

    @NotNull(message = "La descripción no puede ser nula")
    @Size(min = 1, max = 500, message = "La descripción debe tener entre 1 y 500 caracteres")
    private String descripcion;

    private LocalDate fecha;

    private LocalTime hora;

    @NotNull(message = "El estado no puede ser nulo")
    private Boolean activo;

    private Set<Long> entradas;

    @NotNull(message = "El establecimiento no puede ser nulo")
    private Long establecimiento;

    @NotNull(message = "La clasificacion no puede ser nula")
    private Long clasificacion;

    private Set<Long> artistas;

}
