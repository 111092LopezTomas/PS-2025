package org.example.escenalocal.repositories;

import org.example.escenalocal.entities.NotificacionPreferenciaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface NotificacionPreferenciaRepository extends JpaRepository<NotificacionPreferenciaEntity, Long> {
  Optional<NotificacionPreferenciaEntity> findByUserId(Long userId);
}
