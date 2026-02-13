package org.example.escenalocal.dtos;

import lombok.*;
import java.time.LocalDateTime;

@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class NotificacionItemDto {
  private Long id;
  private String mensaje;
  private boolean leido;
  private LocalDateTime creado;
}
