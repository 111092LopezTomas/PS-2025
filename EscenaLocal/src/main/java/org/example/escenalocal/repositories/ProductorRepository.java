package org.example.escenalocal.repositories;

import org.example.escenalocal.entities.ProductorEntity;
import org.example.escenalocal.entities.UsuarioEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ProductorRepository extends JpaRepository<ProductorEntity, Long> {
  Optional<ProductorEntity> findByUsuario(UsuarioEntity usuario);
}
