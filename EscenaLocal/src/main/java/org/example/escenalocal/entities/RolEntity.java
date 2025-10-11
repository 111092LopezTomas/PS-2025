package org.example.escenalocal.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "roles")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RolEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column
  private String rol;

  @OneToMany(mappedBy = "rol", cascade = {}, orphanRemoval = false, fetch = FetchType.LAZY)
  @Builder.Default
  private Set<UsuarioEntity> usuarios = new HashSet<>();
}
