package org.example.escenalocal.auth.repository;


import org.example.escenalocal.entities.UsuarioEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface UserRepository extends JpaRepository<UsuarioEntity, Long> {
  Optional<UsuarioEntity> findByUsername(String username);
  boolean existsByUsername(String username);

    Optional<UsuarioEntity> findByEmail(String email);

}
