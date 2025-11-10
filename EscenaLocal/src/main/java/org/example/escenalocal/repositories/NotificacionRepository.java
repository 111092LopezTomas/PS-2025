package org.example.escenalocal.repositories;

import org.example.escenalocal.entities.Notificacion;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface NotificacionRepository extends JpaRepository<Notificacion, Long> {
  List<Notificacion> findByUserIdOrderByCreadoDesc(Long userId);
}
