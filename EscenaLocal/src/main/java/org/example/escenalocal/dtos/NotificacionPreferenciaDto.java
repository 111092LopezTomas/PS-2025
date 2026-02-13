package org.example.escenalocal.dtos;

import lombok.*;

@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class NotificacionPreferenciaDto {
  public boolean email;
  public boolean push;

  public boolean marketingNovedades;
  public boolean recordatoriosEventos;
  public boolean mensajesDirectos;

  public boolean invitacionesAEventos;
  public boolean nuevosSeguidores;

  public boolean ventasEntradas;
  public boolean pagosMercadopago;
}
