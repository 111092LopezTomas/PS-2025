package org.example.escenalocal.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.LinkedHashSet;
import java.util.Set;


@Entity
@Table(name="barrios")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class BarrioEntity {

    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    private Long id;

    @Column
    private String barrio;

    @ManyToOne
    @JoinColumn(name = "idCiudad")
    private CiudadEntity ciudad;

    @OneToMany(mappedBy = "barrio", cascade = {}, orphanRemoval = false, fetch = FetchType.LAZY)
    @Builder.Default
    private Set<EstablecimientoEntity> establecimiento = new LinkedHashSet<>();
}
