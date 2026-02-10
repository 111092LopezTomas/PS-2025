package org.example.escenalocal.payments;

import com.mercadopago.client.payment.PaymentClient;
import com.mercadopago.client.preference.*;
import com.mercadopago.exceptions.MPApiException;
import com.mercadopago.exceptions.MPException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class MercadoPagoGateway implements PaymentGateway {

  @Value("${app.base-url:http://localhost:8080}")
  private String baseUrl;

  @Value("${mercadopago.enable-webhook:true}")
  private boolean enableWebhook;

  // ======================================================
  // CREATE PREFERENCE
  // ======================================================
  @Override
  public CreatePrefResult createPreferenceWithBase(
    CreatePrefCommand cmd,
    String base
  ) throws MPException, MPApiException {

    String b = normalizeBase(base);

    var items = cmd.items().stream().map(i -> {
      int qty = Math.max(i.quantity(), 1);

      BigDecimal price = i.unitPrice();
      if (price == null || price.compareTo(BigDecimal.ZERO) <= 0) {
        throw new IllegalArgumentException("Precio inválido");
      }

      price = price.setScale(2, RoundingMode.HALF_UP);

      return PreferenceItemRequest.builder()
        .id(i.id())
        .title(i.title() == null || i.title().isBlank() ? "Entrada" : i.title())
        .description(i.description())
        .quantity(qty)
        .currencyId("ARS")
        .unitPrice(price)
        .build();
    }).collect(Collectors.toList());

    var backUrls = PreferenceBackUrlsRequest.builder()
      .success(b + "/checkout/success")
      .pending(b + "/checkout/pending")
      .failure(b + "/checkout/failure")
      .build();

    var builder = PreferenceRequest.builder()
      .items(items)
      .backUrls(backUrls)
      .externalReference(cmd.externalReference())
      .metadata(Map.of(
        "usuarioId", cmd.usuarioId(),
        "eventoId", cmd.eventoId(),
        "tipoEntradaId", cmd.items().get(0).id(),
        "cantidad", cmd.items().get(0).quantity(),
        "precio", cmd.items().get(0).unitPrice()
      ));

    // Auto return solo HTTPS
    if (b.startsWith("https://")) {
      builder.autoReturn("approved");
    }

    // Webhook solo HTTPS
    if (enableWebhook && b.startsWith("https://")) {
      builder.notificationUrl(b + "/payments/webhook");
    }

    System.out.println("🧾 METADATA ENVIADA A MP:");
    System.out.println("usuarioId=" + cmd.usuarioId());
    System.out.println("eventoId=" + cmd.eventoId());
    System.out.println("tipoEntradaId=" + cmd.items().get(0).id());
    System.out.println("cantidad=" + cmd.items().get(0).quantity());
    System.out.println("precio=" + cmd.items().get(0).unitPrice());


    var pref = new PreferenceClient().create(builder.build());

    return new CreatePrefResult(pref.getId(), pref.getInitPoint());
  }

  // ======================================================
  // PAYMENT STATUS
  // ======================================================
  @Override
  public PaymentStatus getStatus(String id) {
    try {
      var payment = new PaymentClient().get(Long.parseLong(id));
      return switch (payment.getStatus()) {
        case "approved" -> PaymentStatus.APPROVED;
        case "pending", "in_process" -> PaymentStatus.PENDING;
        default -> PaymentStatus.REJECTED;
      };
    } catch (Exception e) {
      return PaymentStatus.PENDING;
    }
  }

  // ======================================================
  // BASE NORMALIZER
  // ======================================================
  private String normalizeBase(String base) {
    String b = (base == null || base.isBlank())
      ? baseUrl
      : base.trim();

    if (b.endsWith("/")) {
      b = b.substring(0, b.length() - 1);
    }

    // ngrok siempre HTTPS
    if (b.startsWith("http://") && b.contains("ngrok")) {
      b = "https://" + b.substring(7);
    }

    return b;
  }
}



