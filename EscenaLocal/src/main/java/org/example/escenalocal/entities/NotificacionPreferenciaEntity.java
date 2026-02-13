package org.example.escenalocal.entities;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "notificacion_preferencia")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class NotificacionPreferenciaEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(name = "user_id", nullable = false, unique = true)
  private Long userId;

  // canales
  @Builder.Default
  @Column(nullable = false)
  private boolean email = true;

  @Builder.Default
  @Column(nullable = false)
  private boolean push = false;

  // tipos
  @Builder.Default
  @Column(name = "marketing_novedades", nullable = false)
  private boolean marketingNovedades = false;

  @Builder.Default
  @Column(name = "recordatorios_eventos", nullable = false)
  private boolean recordatoriosEventos = true;

  @Builder.Default
  @Column(name = "mensajes_directos", nullable = false)
  private boolean mensajesDirectos = true;

  // artista
  @Builder.Default
  @Column(name = "invitaciones_a_eventos", nullable = false)
  private boolean invitacionesAEventos = true;

  @Builder.Default
  @Column(name = "nuevos_seguidores", nullable = false)
  private boolean nuevosSeguidores = true;

  // productor
  @Builder.Default
  @Column(name = "ventas_entradas", nullable = false)
  private boolean ventasEntradas = true;

  @Builder.Default
  @Column(name = "pagos_mercadopago", nullable = false)
  private boolean pagosMercadopago = true;

  @Builder.Default
  @Column(name = "updated_at", nullable = false)
  private LocalDateTime updatedAt = LocalDateTime.now();

  @Builder.Default
  @Column(name = "alertas_capacidad", nullable = false)
  private boolean alertasCapacidad = false;

  @PrePersist
  @PreUpdate
  void touch() {
    updatedAt = LocalDateTime.now();
  }
}
