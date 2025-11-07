package org.example.escenalocal.repositories;

import org.example.escenalocal.entities.ArtistaEntity;
import org.example.escenalocal.entities.UsuarioEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ArtistaRepository extends JpaRepository<ArtistaEntity, Long> {
  Optional<ArtistaEntity> findByUsuario(UsuarioEntity usuario);
}
