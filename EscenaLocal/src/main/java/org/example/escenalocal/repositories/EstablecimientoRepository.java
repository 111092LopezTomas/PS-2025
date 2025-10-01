package org.example.escenalocal.repositories;

import org.example.escenalocal.entities.EstablecimientoEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EstablecimientoRepository extends JpaRepository<EstablecimientoEntity,Long> {
}
