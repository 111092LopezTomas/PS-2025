package org.example.escenalocal.auth.controller;

import org.example.escenalocal.auth.dtos.*;
import org.example.escenalocal.auth.repository.RolRepository;
import org.example.escenalocal.auth.repository.UserRepository;
import org.example.escenalocal.auth.security.JwtUtil;
import org.example.escenalocal.auth.service.AuthService;
import org.example.escenalocal.dtos.get.GetArtProdDto;
import org.example.escenalocal.dtos.post.PostArtProdDto;
import org.example.escenalocal.entities.*;
import org.example.escenalocal.repositories.ArtistaRepository;
import org.example.escenalocal.repositories.GeneroRepository;
import org.example.escenalocal.repositories.ProductorRepository;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

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

  public AuthController(AuthService authService,
                        UserRepository userRepo,
                        RolRepository rolRepository,
                        PasswordEncoder passwordEncoder,
                        ArtistaRepository artistaRepo,
                        ProductorRepository productorRepo,
                        GeneroRepository generoRepo,
                        JwtUtil jwtUtil) {
    this.authService = authService;
    this.userRepo = userRepo;
    this.rolRepository = rolRepository;
    this.passwordEncoder = passwordEncoder;
    this.artistaRepo = artistaRepo;
    this.productorRepo = productorRepo;
    this.generoRepo = generoRepo;
    this.jwtUtil = jwtUtil;
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

//  @GetMapping("/usuarios/{id}")
//  public ResponseEntity<UsuarioEntity> obtenerUsuario(@PathVariable Long id) {
//    UsuarioEntity usuario = userRepo.findById(id)
//      .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
//    return ResponseEntity.ok(usuario);
//  }

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
    return ResponseEntity.ok(new AuthResponse(token, user.getId()));
  }

  @PutMapping(value = "/usuarios/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
  public ResponseEntity<Void> actualizarUsuario(
    @PathVariable Long id,
    @RequestPart("username") String username,
    @RequestPart("email") String email,
    @RequestPart(value = "password", required = false) String password,
    @RequestPart(value = "nombre", required = false) String nombre,
    @RequestPart(value = "representante", required = false) String representante,
    @RequestPart(value = "telefono_representante", required = false) String telefonoRepresentante,
    @RequestPart(value = "red_social", required = false) String redSocial,
    @RequestPart(value = "idGenero", required = false) Long idGenero,
    @RequestPart(value = "imagen", required = false) MultipartFile imagen
  ) {

    // 1) buscar usuario
    UsuarioEntity usuario = userRepo.findById(id)
      .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

    // 2) actualizar datos básicos (sin tocar rol)
    usuario.setUsername(username);
    usuario.setEmail(email);

    if (password != null && !password.isBlank()) {
      usuario.setPassword(passwordEncoder.encode(password));
    }

    userRepo.save(usuario);

    // 3) según el rol, actualizar artista o productor
    String rolNombre = usuario.getRol().getRol(); // ej: ROL_ARTISTA, ROL_PRODUCTOR

    if ("ROL_ARTISTA".equals(rolNombre)) {
      ArtistaEntity artista = artistaRepo.findById(usuario.getId())
        .orElseThrow(() -> new RuntimeException("Artista no encontrado para el usuario"));

      if (nombre != null) artista.setNombre(nombre);
      if (representante != null) artista.setRepresentante(representante);
      if (telefonoRepresentante != null) artista.setTelefono_representante(telefonoRepresentante);
      if (redSocial != null) artista.setRed_social(redSocial);

      if (idGenero != null) {
        GeneroEntity genero = generoRepo.findById(idGenero)
          .orElseThrow(() -> new RuntimeException("Género no encontrado"));
        artista.setGenero(genero);
      }

      artistaRepo.save(artista);

    } else if ("ROL_PRODUCTOR".equals(rolNombre)) {
      ProductorEntity productor = productorRepo.findById(usuario.getId())
        .orElseThrow(() -> new RuntimeException("Productor no encontrado para el usuario"));

      if (nombre != null) productor.setNombre(nombre);
      if (representante != null) productor.setRepresentante(representante);
      if (telefonoRepresentante != null) productor.setTelefono_representante(telefonoRepresentante);
      if (redSocial != null) productor.setRed_social(redSocial);

      productorRepo.save(productor);
    }

    // 4) si vino imagen, la guardamos igual que en el POST
    if (imagen != null && !imagen.isEmpty()) {
      authService.guardarImagenUsuario(usuario.getId(), imagen);
    }

    return ResponseEntity.ok().build();
  }
}
