package org.example.escenalocal.entities;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.Hibernate;
import org.hibernate.annotations.JdbcTypeCode;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@Entity
@Table(name="eventos")
@Getter
@Setter
@ToString(onlyExplicitlyIncluded = true)
@NoArgsConstructor
public class EventoEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column
    private String evento;

    @Column
    private String descripcion;

    @Column
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy")
    private LocalDate fecha;

    @Column
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "HH:mm")
    private LocalTime hora;

    @Column
    private Boolean activo;

    @OneToMany(mappedBy = "evento", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<EventoTiposEntradaEntity> eventoTiposEntrada = new HashSet<>();


    @OneToMany(mappedBy = "evento", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<ArtistaEventoEntity> artistasEvento = new HashSet<>();

    @ManyToOne
    @JoinColumn(name = "idEstablecimiento")
    private EstablecimientoEntity establecimiento;

    @ManyToOne
    @JoinColumn(name = "idClasificacion")
    private ClasificacionEntity clasificacion;

    @ManyToOne
    @JoinColumn(name = "idProductor")
    private ProductorEntity productor;

//    @Column
//    private Integer capacidad;


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        // importante para proxies
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        EventoEntity that = (EventoEntity) o;
        // si id es null, NO son iguales
        return id != null && Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        // recomendado por Hibernate: clase, no colecciones ni campos mutables
        return getClass().hashCode();
    }

  @Lob
  @Basic(fetch = FetchType.LAZY)
  @Column(name = "img_datos", columnDefinition = "bytea", nullable = true)
  @JdbcTypeCode(0)
  private byte[] imagenDatos;

  @Column(name = "img_tamano", nullable = true)
  private Long imagenTamano;

  @Column(name = "img_content_type", length = 255, nullable = true)
  private String imagenContentType;

  @Column(name = "img_nombre", length = 255, nullable = true)
  private String imagenNombre;

}
