package org.example.escenalocal.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Objects;

@Embeddable
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class EventoTiposEntradaId implements java.io.Serializable {
    @Column(name = "id_evento")
    private Long eventoId;

    @Column(name = "id_tipos_entrada")
    private Long tiposEntradaId;

    @Override public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof EventoTiposEntradaId that)) return false;
        return Objects.equals(eventoId, that.eventoId) && Objects.equals(tiposEntradaId, that.tiposEntradaId);
    }
    @Override public int hashCode() { return Objects.hash(eventoId, tiposEntradaId); }
}
