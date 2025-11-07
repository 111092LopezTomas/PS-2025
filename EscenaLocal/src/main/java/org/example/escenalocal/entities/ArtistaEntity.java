package org.example.escenalocal.entities;


import jakarta.persistence.*;
import lombok.*;
import org.hibernate.Hibernate;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@Entity
@Table(name="artistas")
@Getter
@Setter
@ToString(onlyExplicitlyIncluded = true)
@AllArgsConstructor
@NoArgsConstructor
public class ArtistaEntity {

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

    @OneToMany(mappedBy = "artista")
    private Set<ArtistaEventoEntity> artistaEventos = new HashSet<>();

    @ManyToOne
    @JoinColumn(name = "idGenero")
    private GeneroEntity genero;

    @OneToOne
    @JoinColumn(name = "idUsuario")
    private UsuarioEntity usuario;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        // importante para proxies
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        ArtistaEntity that = (ArtistaEntity) o;
        // si id es null, NO son iguales
        return id != null && Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        // recomendado por Hibernate: clase, no colecciones ni campos mutables
        return getClass().hashCode();
    }
}
