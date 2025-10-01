package org.example.escenalocal.repositories;

import org.example.escenalocal.entities.BarrioEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BarrioRepository extends JpaRepository<BarrioEntity, Long> {
}
