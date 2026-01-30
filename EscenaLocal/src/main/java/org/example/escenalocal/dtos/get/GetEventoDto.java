package org.example.escenalocal.dtos.get;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

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
  List<GetEntradaDto> entradasDetalle;
  String clasificacion;
  Long productorId;
  String productor;
  Long establecimientoId;
  String establecimiento;
  Integer capacidad;
  String direccion;
  String barrio;
  String ciudad;
  String provincia;
  String genero;

}
