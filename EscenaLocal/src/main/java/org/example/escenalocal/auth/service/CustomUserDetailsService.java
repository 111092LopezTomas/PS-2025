package org.example.escenalocal.auth.service;

import org.example.escenalocal.entities.UsuarioEntity;
import org.example.escenalocal.auth.repository.UserRepository;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.*;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class CustomUserDetailsService implements UserDetailsService {
  private final UserRepository repo;
  public CustomUserDetailsService(UserRepository repo) { this.repo = repo; }

  @Override
  public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
    UsuarioEntity u = repo.findByUsername(username)
      .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado"));


    // Crear una lista con una sola autoridad
    List<GrantedAuthority> authorities =
      List.of(new SimpleGrantedAuthority(u.getRol().getRol()));

    return new User(
      u.getUsername(),
      u.getPassword(),
      authorities

    );
  }
}
