package org.example.escenalocal.services.impl;

import jakarta.mail.MessagingException;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.example.escenalocal.auth.repository.UserRepository;
import org.example.escenalocal.entities.PasswordResetTokenEntity;
import org.example.escenalocal.entities.UsuarioEntity;
import org.example.escenalocal.repositories.PasswordResetTokenRepository;
import org.example.escenalocal.services.EmailService;
import org.example.escenalocal.services.PasswordResetService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Data
public class PasswordResetServiceImpl implements PasswordResetService {

  private final UserRepository userRepo;
  private final PasswordResetTokenRepository tokenRepo;
  private final PasswordEncoder passwordEncoder;
  private final EmailService emailService;


  @PreAuthorize("permitAll()")
  public void requestPasswordReset(String email) throws MessagingException {
    Optional<UsuarioEntity> optUser = userRepo.findByEmail(email);
    if (optUser.isEmpty()) return;

    UsuarioEntity user = optUser.get();
    String token = UUID.randomUUID().toString(); // ejemplo: 36 chars

    PasswordResetTokenEntity tokenEntity = new PasswordResetTokenEntity();
    tokenEntity.setToken(token);
    tokenEntity.setUsuario(user);
    tokenEntity.setExpirationDate(LocalDateTime.now().plusHours(1));
    tokenEntity.setUsed(false);

    tokenRepo.save(tokenEntity);

    emailService.sendPasswordResetEmailHtml(user.getEmail(), token);
    System.out.println("TOKEN GENERADO: " + token);
  }

  @PreAuthorize("permitAll()")
  public void resetPassword(String token, String newPassword) {
    PasswordResetTokenEntity tokenEntity = tokenRepo.findByToken(token)
      .orElseThrow(() -> new IllegalArgumentException("El enlace de recuperación es inválido."));

    if (tokenEntity.isUsed()) {
      throw new IllegalArgumentException("Este enlace ya fue utilizado.");
    }

    if (tokenEntity.getExpirationDate().isBefore(LocalDateTime.now())) {
      throw new IllegalArgumentException("El enlace de recuperación ha expirado.");
    }

    UsuarioEntity user = tokenEntity.getUsuario();
    user.setPassword(passwordEncoder.encode(newPassword));
    userRepo.save(user);

    tokenEntity.setUsed(true);
    tokenRepo.save(tokenEntity);
  }
}
