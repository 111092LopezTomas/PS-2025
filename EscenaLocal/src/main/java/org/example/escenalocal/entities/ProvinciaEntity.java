package org.example.escenalocal.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.LinkedHashSet;
import java.util.Set;

@Entity
@Table(name="Provincias")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ProvinciaEntity {

    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    private Long id;

    @Column
    private String provincia;

    @OneToMany(mappedBy = "provincia", cascade = {}, orphanRemoval = false, fetch = FetchType.LAZY)
    @Builder.Default
    private Set<CiudadEntity> ciudades = new LinkedHashSet<>();
}
