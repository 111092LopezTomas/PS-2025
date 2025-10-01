package org.example.escenalocal.entities;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.Hibernate;

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
@AllArgsConstructor
@NoArgsConstructor
@Builder
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

//    @ManyToMany
//    @JoinTable(
//            name = "evento_entrada",
//            joinColumns = @JoinColumn(name = "idEvento"),
//            inverseJoinColumns = @JoinColumn(name = "idTiposEntrada"))
//    @ToString.Exclude
//    @ManyToMany(mappedBy = "eventos", fetch = FetchType.LAZY)
@OneToMany(mappedBy = "evento", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<EventoTiposEntradaEntity> eventoTiposEntrada = new HashSet<>();

//    @ManyToMany
//    @JoinTable(
//            name = "artista_evento",
//            joinColumns = @JoinColumn(name = "idArtista"),
//            inverseJoinColumns = @JoinColumn(name = "idEvento")
//    )
//    @ToString.Exclude
    @OneToMany(mappedBy = "evento", cascade = CascadeType.ALL, orphanRemoval = true)
//    @ManyToMany(mappedBy = "eventos", fetch = FetchType.LAZY)
    private Set<ArtistaEventoEntity> artistasEvento = new HashSet<>();

    @ManyToOne
    @JoinColumn(name = "idEstablecimiento")
    private EstablecimientoEntity establecimiento;

    @ManyToOne
    @JoinColumn(name = "idClasificacion")
    private ClasificacionEntity clasificacion;

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

}
