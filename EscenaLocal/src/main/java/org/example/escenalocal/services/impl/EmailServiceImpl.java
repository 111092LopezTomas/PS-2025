package org.example.escenalocal.services.impl;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.example.escenalocal.services.EmailService;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.io.Serial;

@Service
@RequiredArgsConstructor
@Data
public class EmailServiceImpl implements EmailService {

  private final JavaMailSender mailSender;

  public void sendPasswordResetEmail(String to, String token) {
    String resetLink = "https://tu-frontend.com/reset-password?token=" + token;

    String subject = "Recuperación de contraseña - EscenaLocal";
    String body = """
                Hola,

                Hacé clic en el siguiente enlace para restablecer tu contraseña:

                %s

                Este enlace expira en 1 hora.

                Si no solicitaste el cambio, ignorá este mensaje.

                Saludos,
                Equipo EscenaLocal
                """.formatted(resetLink);

    SimpleMailMessage message = new SimpleMailMessage();
    message.setTo(to);
    message.setSubject(subject);
    message.setText(body);

    mailSender.send(message);
  }

  public void sendPasswordResetEmailHtml(String to, String token) throws MessagingException {

    String resetLink = "http://localhost:4200/reset-password?token=" + token;
    System.out.println("LINK ENVIADO POR MAIL: " + resetLink);


    MimeMessage message = mailSender.createMimeMessage();
    MimeMessageHelper helper = new MimeMessageHelper(message, "utf-8");

    String html = """
      <h2>Recuperación de contraseña</h2>
      <p>Hacé clic en el siguiente botón:</p>
      <a href="%s"
         style="background:#007bff;color:white;padding:10px 15px;border-radius:5px;text-decoration:none;">
          Restablecer contraseña
      </a>
      <p>El enlace vence en 1 hora.</p>
      """.formatted(resetLink);

    helper.setTo(to);
    helper.setSubject("Recuperación de contraseña - EscenaLocal");
    helper.setText(html, true); // true = HTML

    mailSender.send(message);
  }
}
