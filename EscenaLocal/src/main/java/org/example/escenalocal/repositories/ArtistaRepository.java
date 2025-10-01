package org.example.escenalocal.repositories;

import org.example.escenalocal.entities.ArtistaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ArtistaRepository extends JpaRepository<ArtistaEntity, Long> {
}
