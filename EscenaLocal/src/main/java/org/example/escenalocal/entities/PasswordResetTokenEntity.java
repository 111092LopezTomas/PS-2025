package org.example.escenalocal.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "password_reset_tokens")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class PasswordResetTokenEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(nullable = false, unique = true, length = 255)
  private String token;

  @ManyToOne(optional = false)
  @JoinColumn(name = "usuario_id")
  private UsuarioEntity usuario;

  @Column(nullable = false)
  private LocalDateTime expirationDate;

  @Column(nullable = false)
  private boolean used = false;
}

