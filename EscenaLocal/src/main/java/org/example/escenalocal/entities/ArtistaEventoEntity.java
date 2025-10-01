package org.example.escenalocal.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Objects;

@Entity
@Table(name="artista_evento")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ArtistaEventoEntity {

    @EmbeddedId
    private ArtistaEventoId id = new ArtistaEventoId();

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("eventoId")
    @JoinColumn(name = "id_evento")
    private EventoEntity evento;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("artistaId")
    @JoinColumn(name = "id_artista")
    private ArtistaEntity artista;

    // (si tuvieras campos extra, agrégalos aquí)

    // equals/hashCode por id embebido
    @Override public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ArtistaEventoEntity that)) return false;
        return Objects.equals(id, that.id);
    }
    @Override public int hashCode() { return Objects.hash(id); }
}

