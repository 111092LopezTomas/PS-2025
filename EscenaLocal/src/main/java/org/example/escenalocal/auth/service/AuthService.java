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
import java.util.ArrayList;
import java.util.List;

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
      new UsernamePasswordAuthenticationToken(
        req.getUsername(),
        req.getPassword()
      );
    authManager.authenticate(authToken);

    // 2) buscar usuario REAL en la base
    UsuarioEntity usuario = userRepo.findByUsername(req.getUsername())
      .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

    // 3) obtener rol REAL de la base (NO usar el del request)
    String rolReal = usuario.getRol().getRol(); // p.ej. "ROL_PRODUCTOR"

    // 4) generar token con el rol REAL
    String token = jwtUtil.generateToken(usuario.getUsername(), rolReal);

    // 5) devolver respuesta al front
    return new AuthResponse(token, usuario.getId());
  }

public AuthResponse register(RegisterRequest req) {
  if (userRepo.existsByUsername(req.getUsername())) {
    throw new RuntimeException("Usuario ya existe");
  }

  // 1. obtener el rol: si viene rolId, usarlo; si no, usar el default
  RolEntity rolEntity;

  if (req.getRolId() != null) {
    // buscar por id
    rolEntity = rolRepo.findById(req.getRolId())
      .orElseThrow(() -> new RuntimeException("Rol no encontrado"));
  } else {
    // fallback al rol por defecto
    rolEntity = rolRepo.findByRol("ROL_USUARIO")
      .orElseThrow(() -> new RuntimeException("Rol no encontrado"));
  }

  // 2. crear usuario
  UsuarioEntity u = new UsuarioEntity();
  u.setUsername(req.getUsername());
  u.setPassword(passwordEncoder.encode(req.getPassword()));
  u.setEmail(req.getEmail());
  u.setRol(rolEntity);
  userRepo.save(u);

  // 3. guardar imagen si vino
  if (req.getImagen() != null && !req.getImagen().isEmpty()) {
    guardarImagenUsuario(u.getId(), req.getImagen());
  }

  // 4. generar token con el rol REAL que quedó en el usuario
  String token = jwtUtil.generateToken(u.getUsername(), u.getRol().getRol());

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

  public List<GetRolDto> getRoles() {
    List<RolEntity> roles = rolRepo.findAll();
    List<GetRolDto> list = new ArrayList<>();
    for (RolEntity rol : roles) {
      GetRolDto dto = new GetRolDto();
      dto.setId(rol.getId());
      dto.setRol(rol.getRol());
      list.add(dto);
    }

    return list;
  }
}
