package org.example.escenalocal.entities;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.Hibernate;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@Entity
@Table(name="tipos_entrada")
@Getter
@Setter
@ToString(onlyExplicitlyIncluded = true)
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TiposEntradaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column
    private String entrada;

//    @ManyToMany
//    @JoinTable(
//            name = "evento_entrada",
//            joinColumns = @JoinColumn(name = "idEvento"),
//            inverseJoinColumns = @JoinColumn(name = "idTiposEntrada"))
//    @ManyToMany(mappedBy = "tiposEntrada")
    @OneToMany(mappedBy = "tiposEntrada")
    private Set<EventoTiposEntradaEntity> eventoTipos = new HashSet<>();

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        // importante para proxies
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        TiposEntradaEntity that = (TiposEntradaEntity) o;
        // si id es null, NO son iguales
        return id != null && Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        // recomendado por Hibernate: clase, no colecciones ni campos mutables
        return getClass().hashCode();
    }
}
