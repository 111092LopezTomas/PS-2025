package org.example.escenalocal.entities;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Entity
public class Notificacion {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  private Long userId; // o una relación @ManyToOne con User
  private String mensaje;
  private boolean leido = false;
  private LocalDateTime creado = LocalDateTime.now();

  // Getters y Setters
}

