package org.example.escenalocal.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "productores")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProductorEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column
  private String nombre;

  @Column
  private String representante;

  @Column
  private String telefono_representante;

  @Column
  private String red_social;

  @OneToOne
  @JoinColumn(name = "idUsuario")
  private UsuarioEntity usuario;
}
