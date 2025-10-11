package org.example.escenalocal.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Collection;
import java.util.List;

@Entity
@Table(name = "usuarios")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class UsuarioEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(unique = true, nullable = false)
  private String usuario;

  @Column(unique = true, nullable = false)
  private String email;

  @Column(nullable = false)
  private String contrasenia;

  @ManyToOne
  @JoinColumn(name = "idRol")
  private RolEntity rol;

}
