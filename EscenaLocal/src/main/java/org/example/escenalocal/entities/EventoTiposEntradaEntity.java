package org.example.escenalocal.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.Objects;

@Entity
@Table(name="evento_entrada")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class EventoTiposEntradaEntity {

    @EmbeddedId
    private EventoTiposEntradaId id = new EventoTiposEntradaId();

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("eventoId")
    @JoinColumn(name = "id_evento")
    private EventoEntity evento;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("tiposEntradaId")
    @JoinColumn(name = "id_tipos_entrada")
    private TiposEntradaEntity tiposEntrada;

    @Column(name = "precio", precision = 12, scale = 2, nullable = false)
    private BigDecimal precio;

    private Integer disponibilidad;

    // equals/hashCode por id embebido
    @Override public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof EventoTiposEntradaEntity that)) return false;
        return Objects.equals(id, that.id);
    }
    @Override public int hashCode() { return Objects.hash(id); }
}
