package org.example.escenalocal.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Set;

@Entity
@Table(name="clasificaciones")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ClasificacionEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column
    private String clasificacion;

    @OneToMany(mappedBy = "clasificacion", cascade = {}, orphanRemoval = false, fetch = FetchType.LAZY)
    @Builder.Default
    private Set<EventoEntity> eventos = new HashSet<>();
}
