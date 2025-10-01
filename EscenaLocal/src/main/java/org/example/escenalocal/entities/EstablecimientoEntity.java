package org.example.escenalocal.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name="establecimientos")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class EstablecimientoEntity {

    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    private Long id;

    @Column
    private String establecimiento;

    @Column
    private String direccion;

    @Column
    private Integer capacidad;

    @ManyToOne
    @JoinColumn(name = "idBarrio")
    private BarrioEntity barrio;


}
