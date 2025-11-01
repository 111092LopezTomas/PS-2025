package org.example.escenalocal.auth.repository;

import org.example.escenalocal.entities.UsuarioEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.example.escenalocal.entities.RolEntity;

import java.util.Optional;

public interface RolRepository extends JpaRepository<RolEntity, Long> {
  Optional<RolEntity> findByRol(String rol);
}
