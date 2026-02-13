package org.example.escenalocal.auth.service;

import lombok.RequiredArgsConstructor;
import org.example.escenalocal.auth.repository.UserRepository;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthContextService {

  private final UserRepository usuarioRepository;

  public Long currentUserId() {
    Authentication auth = SecurityContextHolder.getContext().getAuthentication();
    if (auth == null || !auth.isAuthenticated()) {
      throw new IllegalStateException("Usuario no autenticado");
    }

    String username = auth.getName(); // JuanMS

    return usuarioRepository.findByUsername(username)
      .orElseThrow(() -> new IllegalStateException("Usuario no encontrado: " + username))
      .getId();
  }
}
