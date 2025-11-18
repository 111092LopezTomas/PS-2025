package org.example.escenalocal.services;

import jakarta.mail.MessagingException;
import org.springframework.stereotype.Service;

@Service
public interface EmailService {
  void sendPasswordResetEmail(String to, String token);

  void sendPasswordResetEmailHtml(String email, String token) throws MessagingException;
}
