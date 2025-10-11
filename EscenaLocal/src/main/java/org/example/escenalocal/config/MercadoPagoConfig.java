package org.example.escenalocal.config;

import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MercadoPagoConfig {


  @Value("${mercadopago.access-token}")
  private String accessToken;

  @PostConstruct
  public void init() {
    com.mercadopago.MercadoPagoConfig.setAccessToken(accessToken);
    String masked = (accessToken == null) ? "NULL"
      : accessToken.substring(0, Math.min(accessToken.length(), 10)) + "...";
    System.out.println("[MP] Using access token = " + masked);
    System.out.println("[MP] From env MP_ACCESS_TOKEN = " + System.getenv("MP_ACCESS_TOKEN"));
  }
}
