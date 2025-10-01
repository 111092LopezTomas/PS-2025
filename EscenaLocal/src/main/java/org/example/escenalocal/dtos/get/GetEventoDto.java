package org.example.escenalocal.dtos.get;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class GetEventoDto {

    Long id;
    Boolean activo;
    String descripcion;
    String evento;
    LocalDate fecha;
    LocalTime hora;
    List<String> artistas;
    List<String> entradas;
    String clasificacion;
    String establecimiento;
    String direccion;
    String ciudad;
    String provincia;
}
