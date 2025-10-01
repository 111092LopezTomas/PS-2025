package org.example.escenalocal.repositories;

import org.example.escenalocal.entities.ClasificacionEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ClasificacionRepository extends JpaRepository<ClasificacionEntity,Long> {
}
