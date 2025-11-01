package org.example.escenalocal.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

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
  private String username;

  @Column(unique = true, nullable = false)
  private String email;

  @Column(nullable = false)
  private String password;

  @ManyToOne
  @JoinColumn(name = "idRol")
  private RolEntity rol;

  @Column(name = "img_nombre", length = 255, nullable = true)
  private String imagenNombre;

  @Column(name = "img_content_type", length = 255, nullable = true)
  private String imagenContentType;

  @Lob
  @Basic(fetch = FetchType.LAZY)
  @JdbcTypeCode(SqlTypes.BINARY)
  @Column(name = "img_datos", nullable = true)
  private byte[] imagenDatos;

  @Column(name = "img_tamano", nullable = true)
  private Long imagenTamano;

}
