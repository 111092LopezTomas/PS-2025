package org.example.escenalocal.dtos.get;

public interface EventoDetalleProj {
    Boolean getActivo(); String getDescripcion(); String getEvento();
    java.time.LocalDate getFecha(); java.time.LocalTime getHora();
    String getNombre(); String getEntrada(); String getClasificacion();
    String getEstablecimiento(); String getDireccion(); String getCiudad(); String getProvincia();
}
