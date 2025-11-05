package org.example.escenalocal.auth.controller;

import org.example.escenalocal.auth.dtos.*;
import org.example.escenalocal.auth.repository.RolRepository;
import org.example.escenalocal.auth.repository.UserRepository;
import org.example.escenalocal.auth.service.AuthService;
import org.example.escenalocal.entities.UsuarioEntity;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/auth")
public class AuthController {

  private final AuthService authService;
  private final UserRepository userRepo;

  public AuthController(AuthService authService, UserRepository userRepo) {
    this.authService = authService;
    this.userRepo = userRepo;
  }


  @PostMapping("/login")
  public ResponseEntity<AuthResponse> login(@RequestBody AuthRequest req) {
    return ResponseEntity.ok(authService.login(req));
  }

  @PostMapping(value = "/register", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
  public ResponseEntity<AuthResponse> register(
    @RequestPart("username") String username,
    @RequestPart("password") String password,
    @RequestPart("email") String email,
    @RequestParam Long rolId,
    @RequestPart(value = "imagen", required = false) MultipartFile imagen) {

    RegisterRequest req = new RegisterRequest(username, password, email, imagen, rolId);
    return ResponseEntity.ok(authService.register(req));
  }

  // endpoint de prueba protegido
  @GetMapping("/hello")
  public ResponseEntity<String> hello() {
    return ResponseEntity.ok("Hola, estás autenticado");
  }

  @PostMapping("/{id}/imagen")
  public ResponseEntity<Void> subirImagen(@PathVariable Long id, @RequestParam("file") MultipartFile file) {
    authService.guardarImagenUsuario(id, file);
    return ResponseEntity.ok().build();
  }

  @GetMapping("/{id}/imagen")
  public ResponseEntity<byte[]> obtenerImagen(@PathVariable Long id) {
    UsuarioEntity u = userRepo.findById(id)
      .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

    if (u.getImagenDatos() == null) {
      throw new RuntimeException("Usuario no tiene imagen");
    }

    return ResponseEntity.ok()
      .contentType(MediaType.parseMediaType(u.getImagenContentType()))
      .body(u.getImagenDatos());
  }

  @GetMapping("/roles")
  public ResponseEntity<List<GetRolDto>> obtenerRoles() {
    List<GetRolDto> list = authService.getRoles();
    return ResponseEntity.ok(list);

  }
}
