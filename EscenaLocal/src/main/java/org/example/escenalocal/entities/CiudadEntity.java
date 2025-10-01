package org.example.escenalocal.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.LinkedHashSet;
import java.util.Set;

@Entity
@Table(name="Ciudades")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CiudadEntity {

    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    private Long id;

    @Column
    private String ciudad;

    @ManyToOne
    @JoinColumn(name = "idProvincia")
    private ProvinciaEntity provincia;

    @OneToMany(mappedBy = "ciudad", cascade = {}, orphanRemoval = false, fetch = FetchType.LAZY)
    @Builder.Default
    private Set<BarrioEntity> barrios = new LinkedHashSet<>();
}
