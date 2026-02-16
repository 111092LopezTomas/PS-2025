package org.example.escenalocal.auth.controller;

import org.example.escenalocal.auth.dtos.*;
import org.example.escenalocal.auth.repository.RolRepository;
import org.example.escenalocal.auth.repository.UserRepository;
import org.example.escenalocal.auth.security.JwtUtil;
import org.example.escenalocal.auth.service.AuthService;
import org.example.escenalocal.dtos.get.GetArtProdDto;
import org.example.escenalocal.dtos.post.PostArtProdDto;
import org.example.escenalocal.dtos.put.UpdateArtProdDto;
import org.example.escenalocal.entities.*;
import org.example.escenalocal.repositories.ArtistaRepository;
import org.example.escenalocal.repositories.GeneroRepository;
import org.example.escenalocal.repositories.ProductorRepository;
import org.example.escenalocal.services.NotificacionService;
import org.example.escenalocal.services.PasswordResetService;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.security.core.Authentication;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/auth")
public class AuthController {

  private final AuthService authService;
  private final UserRepository userRepo;
  private final RolRepository rolRepository;
  private final PasswordEncoder passwordEncoder;
  private final ArtistaRepository artistaRepo;
  private final ProductorRepository productorRepo;
  private final GeneroRepository generoRepo;
  private final JwtUtil jwtUtil;
  private final PasswordResetService passwordResetService;
  private final NotificacionService notificacionService;

  public AuthController(AuthService authService,
                        UserRepository userRepo,
                        RolRepository rolRepository,
                        PasswordEncoder passwordEncoder,
                        ArtistaRepository artistaRepo,
                        ProductorRepository productorRepo,
                        GeneroRepository generoRepo,
                        JwtUtil jwtUtil, PasswordResetService passwordResetService,
                        NotificacionService notificacionService) {
    this.authService = authService;
    this.userRepo = userRepo;
    this.rolRepository = rolRepository;
    this.passwordEncoder = passwordEncoder;
    this.artistaRepo = artistaRepo;
    this.productorRepo = productorRepo;
    this.generoRepo = generoRepo;
    this.jwtUtil = jwtUtil;
    this.passwordResetService = passwordResetService;
    this.notificacionService = notificacionService;
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

  @GetMapping("/usuarios/{id}")
  public ResponseEntity<GetArtProdDto> obtenerUsuario(@PathVariable Long id) {
    UsuarioEntity usuario = userRepo.findById(id)
      .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

    GetArtProdDto dto = new GetArtProdDto();
    dto.setId(usuario.getId());
    dto.setUsername(usuario.getUsername());
    dto.setEmail(usuario.getEmail());
    dto.setRol(usuario.getRol().getRol());

    String rolNombre = usuario.getRol().getRol();

    // ARTISTA
    if ("ROL_ARTISTA".equals(rolNombre)) {
      artistaRepo.findByUsuario(usuario).ifPresent(artista -> {
        dto.setIdArtista(artista.getId());
        dto.setNombre(artista.getNombre());
        dto.setRepresentante(artista.getRepresentante());
        dto.setTelefono_representante(artista.getTelefono_representante());
        dto.setRed_social(artista.getRed_social());
        if (artista.getGenero() != null) {
          dto.setIdGenero(artista.getGenero().getId());
          dto.setGeneroNombre(artista.getGenero().getGenero());
        }
      });
    }

    // PRODUCTOR
    if ("ROL_PRODUCTOR".equals(rolNombre)) {
      productorRepo.findByUsuario(usuario).ifPresent(productor -> {
        dto.setIdProductor(productor.getId());
        dto.setNombre(productor.getNombre());
        dto.setRepresentante(productor.getRepresentante());
        dto.setTelefono_representante(productor.getTelefono_representante());
        dto.setRed_social(productor.getRed_social());
      });
    }

    return ResponseEntity.ok(dto);
  }

  @PostMapping("register/art-prod")
  public ResponseEntity<AuthResponse> registrar(@RequestBody PostArtProdDto req) {

    // 1) validar usuario
    if (userRepo.existsByUsername(req.getUsername())) {
      throw new RuntimeException("El usuario ya existe");
    }

    // 2) determinar rol según tipo
    String rolNombre = switch (req.getTipo()) {
      case "ARTISTA" -> "ROL_ARTISTA";
      case "PRODUCTOR" -> "ROL_PRODUCTOR";
      default -> throw new RuntimeException("Tipo no soportado: " + req.getTipo());
    };

    RolEntity rol = rolRepository.findByRol(rolNombre)
      .orElseThrow(() -> new RuntimeException("Rol no encontrado: " + rolNombre));

    // 3) crear usuario
    UsuarioEntity user = new UsuarioEntity();
    user.setUsername(req.getUsername());
    user.setPassword(passwordEncoder.encode(req.getPassword()));
    user.setEmail(req.getEmail());
    user.setRol(rol);
    userRepo.save(user);

    // 4) crear entidad específica con campos comunes
    if ("ARTISTA".equals(req.getTipo())) {
      ArtistaEntity artista = new ArtistaEntity();
      artista.setNombre(req.getNombre());
      artista.setRepresentante(req.getRepresentante());
      artista.setTelefono_representante(req.getTelefono_representante());
      artista.setRed_social(req.getRed_social());
      if (req.getIdGenero() == null) {
        throw new RuntimeException("El género es obligatorio para artista");
      }
      GeneroEntity genero = generoRepo.findById(req.getIdGenero())
        .orElseThrow(() -> new RuntimeException("Género no encontrado"));
      artista.setGenero(genero);
      artista.setUsuario(user);
      artistaRepo.save(artista);
    } else {
      ProductorEntity productor = new ProductorEntity();
      productor.setNombre(req.getNombre());
      productor.setRepresentante(req.getRepresentante());
      productor.setTelefono_representante(req.getTelefono_representante());
      productor.setRed_social(req.getRed_social());
      productor.setUsuario(user);
      productorRepo.save(productor);
    }

    // 5) devolver token + id usuario
    String token = jwtUtil.generateToken(user.getUsername(), user.getRol().getRol());
    notificacionService.createBinvenidaNotificacion(user.getId());
    return ResponseEntity.ok(new AuthResponse(token, user.getId()));
  }

  @PutMapping("/usuarios/{id}")
  public ResponseEntity<Void> actualizarUsuarioJson(
    @PathVariable Long id,
    @RequestBody UpdateArtProdDto dto
  ) {
    UsuarioEntity usuario = userRepo.findById(id)
      .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

    // datos básicos
    usuario.setUsername(dto.getUsername());
    usuario.setEmail(dto.getEmail());
    if (dto.getPassword() != null && !dto.getPassword().isBlank()) {
      usuario.setPassword(passwordEncoder.encode(dto.getPassword()));
    }
    userRepo.save(usuario);

    String rolNombre = usuario.getRol().getRol();

    // si es artista
    if ("ROL_ARTISTA".equals(rolNombre)) {
      artistaRepo.findByUsuario(usuario).ifPresent(artista -> {
        artista.setNombre(dto.getNombre());
        artista.setRepresentante(dto.getRepresentante());
        artista.setTelefono_representante(dto.getTelefono_representante());
        artista.setRed_social(dto.getRed_social());
        if (dto.getIdGenero() != null) {
          generoRepo.findById(dto.getIdGenero()).ifPresent(artista::setGenero);
        }
        artistaRepo.save(artista);
      });
    }

    // si es productor
    if ("ROL_PRODUCTOR".equals(rolNombre)) {
      productorRepo.findByUsuario(usuario).ifPresent(productor -> {
        productor.setNombre(dto.getNombre());
        productor.setRepresentante(dto.getRepresentante());
        productor.setTelefono_representante(dto.getTelefono_representante());
        productor.setRed_social(dto.getRed_social());
        productorRepo.save(productor);
      });
    }

    return ResponseEntity.ok().build();
  }

  @PostMapping("/forgot-password")
  public ResponseEntity<Map<String, String>> forgotPassword(
    @RequestBody ForgotPasswordRequest request) {

    System.out.println("FORGOT-PASSWORD email = " + request.getEmail());

    try {
      passwordResetService.requestPasswordReset(request.getEmail());
      return ResponseEntity.ok(
        Map.of("message", "Si el correo está registrado, te enviaremos instrucciones.")
      );
    } catch (Exception e) {
      e.printStackTrace();
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
        .body(Map.of("message", "Hubo un problema al procesar la solicitud."));
    }
  }

  @PostMapping("/reset-password")
  public ResponseEntity<Map<String, String>> resetPassword(
    @RequestBody ResetPasswordRequest request) {

    System.out.println("TOKEN RECIBIDO EN BACK: " + request.getToken());
    System.out.println("NEW PASSWORD RECIBIDA: " + request.getNewPassword());

    try {
      passwordResetService.resetPassword(request.getToken(), request.getNewPassword());
      return ResponseEntity.ok(
        Map.of("message", "Contraseña actualizada correctamente.")
      );
    } catch (IllegalArgumentException e) {
      return ResponseEntity.badRequest().body(
        Map.of("message", e.getMessage())
      );
    } catch (Exception e) {
      e.printStackTrace();
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
        .body(Map.of("message", "Ocurrió un error al actualizar la contraseña."));
    }
  }

  @PutMapping("/change-password")
  public ResponseEntity<?> cambiarPassword(@RequestBody ChangePasswordRequest req, Authentication authentication) {

    String username = authentication.getName();
    authService.cambiarPassword(req, username);

    return ResponseEntity.ok(new MessageResponse("Contraseña actualizada correctamente"));
  }

}
