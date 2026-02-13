package org.example.escenalocal.repositories;

import org.example.escenalocal.entities.Notificacion;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface NotificacionRepository extends JpaRepository<Notificacion, Long> {
  List<Notificacion> findByUserIdOrderByCreadoDesc(Long userId);


  long countByUserIdAndLeidoFalse(Long userId);

  Page<Notificacion> findByUserIdOrderByCreadoDesc(Long userId, Pageable pageable);

  @Modifying
  @Query("update Notificacion n set n.leido = true where n.userId = :userId and n.leido = false")
  int markAllAsRead(@Param("userId") Long userId);
}
