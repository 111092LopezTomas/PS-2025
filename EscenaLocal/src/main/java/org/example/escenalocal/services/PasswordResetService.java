package org.example.escenalocal.services;

import jakarta.mail.MessagingException;
import org.springframework.stereotype.Service;

@Service
public interface PasswordResetService {

  void requestPasswordReset(String email) throws MessagingException;
  void resetPassword(String token, String newPassword);
}
