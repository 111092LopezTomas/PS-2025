package org.example.escenalocal.auth.service;

import org.example.escenalocal.auth.dtos.*;
import org.example.escenalocal.auth.repository.RolRepository;
import org.example.escenalocal.entities.RolEntity;
import org.example.escenalocal.entities.UsuarioEntity;
import org.example.escenalocal.auth.repository.UserRepository;
import org.example.escenalocal.auth.security.JwtUtil;
import org.springframework.security.authentication.*;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Service
public class AuthService {
  private final AuthenticationManager authManager;
  private final UserRepository userRepo;
  private final RolRepository rolRepo;
  private final PasswordEncoder passwordEncoder;
  private final JwtUtil jwtUtil;

  public AuthService(AuthenticationManager authManager, UserRepository userRepo, RolRepository rolRepo,
                     PasswordEncoder passwordEncoder, JwtUtil jwtUtil) {
    this.authManager = authManager;
    this.userRepo = userRepo;
    this.rolRepo = rolRepo;
    this.passwordEncoder = passwordEncoder;
    this.jwtUtil = jwtUtil;
  }

  public AuthResponse login(AuthRequest req) {
    UsernamePasswordAuthenticationToken authToken =
      new UsernamePasswordAuthenticationToken(req.getUsername(), req.getPassword());
    authManager.authenticate(authToken); // lanzará excepción si falla

    // Obtener el usuario desde la base
    UsuarioEntity usuario = userRepo.findByUsername(req.getUsername())
      .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado"));

    String token = jwtUtil.generateToken(req.getUsername());

    return new AuthResponse(token, usuario.getId());
  }

  public AuthResponse register(RegisterRequest req) {
    if (userRepo.existsByUsername(req.getUsername())) {
      throw new RuntimeException("Usuario ya existe");
    }

    // Buscar rol por defecto en la base de datos
    RolEntity rolUser = rolRepo.findByRol("ROL_USUARIO")
      .orElseThrow(() -> new RuntimeException("Rol no encontrado"));

    UsuarioEntity u = new UsuarioEntity();
    u.setUsername(req.getUsername());
    u.setPassword(passwordEncoder.encode(req.getPassword()));
    u.setEmail(req.getEmail());
    u.setRol(rolUser);
    userRepo.save(u);

    if (req.getImagen() != null && !req.getImagen().isEmpty()) {
      guardarImagenUsuario(u.getId(), req.getImagen());
    }

    String token = jwtUtil.generateToken(u.getUsername());
    return new AuthResponse(token, u.getId());
  }

  public void guardarImagenUsuario(Long usuarioId, MultipartFile file) {
    if (file == null || file.isEmpty()) return;

    String ct = file.getContentType() != null ? file.getContentType() : "application/octet-stream";
    if (!ct.startsWith("image/")) {
      throw new IllegalArgumentException("El archivo debe ser una imagen. Content-Type recibido: " + ct);
    }

    long maxBytes = 10L * 1024 * 1024;
    if (file.getSize() > maxBytes) {
      throw new IllegalArgumentException("La imagen excede el tamaño máximo de 10MB");
    }

    UsuarioEntity u = userRepo.findById(usuarioId)
      .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado"));

    try {
      u.setImagenNombre(file.getOriginalFilename() != null ? file.getOriginalFilename() : "archivo");
      u.setImagenContentType(ct);
      u.setImagenDatos(file.getBytes());
      u.setImagenTamano(file.getSize());
      userRepo.save(u);
    } catch (IOException e) {
      throw new RuntimeException("No se pudo leer la imagen: " + e.getMessage(), e);
    }
  }
}
