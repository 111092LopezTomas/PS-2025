package org.example.escenalocal.payments;

import com.mercadopago.client.payment.PaymentClient;
import com.mercadopago.client.preference.*;
import com.mercadopago.exceptions.MPApiException;
import com.mercadopago.exceptions.MPException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.stream.Collectors;

@Service
public class MercadoPagoGateway implements PaymentGateway {

  @Value("${app.base-url:http://localhost:8080}")
  private String baseUrl;

  /** Si querés desactivar el webhook en local, seteá a false (o no pongas https) */
  @Value("${mercadopago.enable-webhook:true}")
  private boolean enableWebhook;

  @Override
  public CreatePrefResult createPreference(CreatePrefCommand cmd) throws MPException, MPApiException {

    String b = (baseUrl == null || baseUrl.isBlank()) ? "http://localhost:8080" : baseUrl.trim();
    if (b.endsWith("/")) b = b.substring(0, b.length() - 1);
    System.out.println("[MP] baseUrl = " + b);

    var items = cmd.items().stream().map(i -> {
      // quantity segura
      Integer qty = i.quantity();
      if (qty == null || qty < 1) qty = 1;

      // precio seguro: NUNCA null/<=0 y con 2 decimales
      BigDecimal price = i.unitPrice();
      if (price == null) {
        throw new IllegalArgumentException("unit_price es null para item id=" + i.id());
      }
      price = price.setScale(2, RoundingMode.HALF_UP);
      if (price.compareTo(BigDecimal.ZERO) <= 0) {
        throw new IllegalArgumentException("unit_price <= 0 para item id=" + i.id());
      }

      // log útil para ver qué se envía realmente
      System.out.printf("[MP] item id=%s qty=%d price=%s ARS%n",
        i.id(), qty, price.toPlainString());

      return PreferenceItemRequest.builder()
        .id(i.id())
        .title((i.title()==null || i.title().isBlank()) ? "Entrada" : i.title())
        .description(i.description())
        .quantity(qty)
        .currencyId("ARS")
        .unitPrice(price)
        .build();
    }).collect(Collectors.toList());

// Validación final (fail-fast) antes de armar el PreferenceRequest
    if (items.isEmpty()) throw new IllegalArgumentException("Sin items");
    for (var it : items) {
      if (it.getQuantity() == null || it.getQuantity() < 1)
        throw new IllegalArgumentException("quantity inválida");
      if (it.getUnitPrice() == null || it.getUnitPrice().compareTo(BigDecimal.ZERO) <= 0)
        throw new IllegalArgumentException("unit_price inválido");
    }

    var back = PreferenceBackUrlsRequest.builder()
      .success(b + "/checkout/success")
      .pending(b + "/checkout/pending")
      .failure(b + "/checkout/failure")
      .build();

    var builder = PreferenceRequest.builder()
      .items(items)
      .backUrls(back)
      .externalReference(cmd.externalReference());

    if (b.startsWith("https://")) {
      builder.autoReturn("approved");
    }

    String webhookUrl = b + "/payments/webhook";
    if (enableWebhook && webhookUrl.startsWith("https://")) {
      builder.notificationUrl(webhookUrl);
    }

    var prefReq = builder.build();

    try {
      var client = new PreferenceClient();
      var pref = client.create(prefReq);
      return new CreatePrefResult(pref.getId(), pref.getInitPoint());
    } catch (MPApiException e) {
      System.err.println("MPApiException status = " + e.getApiResponse().getStatusCode());
      System.err.println("MPApiException body   = " + e.getApiResponse().getContent());
      throw e;
    }
  }


  @Override
  public PaymentStatus getStatus(String id) {
    try {
      var payment = new PaymentClient().get(Long.parseLong(id));
      return switch (String.valueOf(payment.getStatus()).toLowerCase()) {
        case "approved" -> PaymentStatus.APPROVED;
        case "pending" -> PaymentStatus.PENDING;
        default -> PaymentStatus.REJECTED;
      };
    } catch (Exception e) {
      return PaymentStatus.PENDING;
    }
  }

  @Override
  public CreatePrefResult createPreferenceWithBase(CreatePrefCommand cmd, String base) throws MPException, MPApiException {
    // 1) Sanitizar base y asegurar que sea absoluta
    String b = (base == null || base.isBlank()) ? "http://localhost:8080" : base.trim();
    if (b.endsWith("/")) b = b.substring(0, b.length() - 1);
    System.out.println("[MP] baseUrl (req) = " + b);

    // 2) Mapear ítems
//    var items = cmd.items().stream().map(i ->
//      PreferenceItemRequest.builder()
//        .id(i.id())
//        .title(i.title())
//        .description(i.description())
//        .quantity(i.quantity())     // >= 1
//        .currencyId("ARS")
//        .unitPrice(i.unitPrice())   // BigDecimal con punto
//        .build()
//    ).collect(Collectors.toList());

    var items = cmd.items().stream().map(i -> {
      // quantity segura
      Integer qty = i.quantity();
      if (qty == null || qty < 1) qty = 1;

      // precio seguro: NUNCA null/<=0 y con 2 decimales
      BigDecimal price = i.unitPrice();
      if (price == null) {
        throw new IllegalArgumentException("unit_price es null para item id=" + i.id());
      }
      price = price.setScale(2, RoundingMode.HALF_UP);
      if (price.compareTo(BigDecimal.ZERO) <= 0) {
        throw new IllegalArgumentException("unit_price <= 0 para item id=" + i.id());
      }

      // log útil para ver qué se envía realmente
      System.out.printf("[MP] item id=%s qty=%d price=%s ARS%n",
        i.id(), qty, price.toPlainString());

      return PreferenceItemRequest.builder()
        .id(i.id())
        .title((i.title()==null || i.title().isBlank()) ? "Entrada" : i.title())
        .description(i.description())
        .quantity(qty)
        .currencyId("ARS")
        .unitPrice(price)
        .build();
    }).collect(Collectors.toList());

// Validación final (fail-fast) antes de armar el PreferenceRequest
    if (items.isEmpty()) throw new IllegalArgumentException("Sin items");
    for (var it : items) {
      if (it.getQuantity() == null || it.getQuantity() < 1)
        throw new IllegalArgumentException("quantity inválida");
      if (it.getUnitPrice() == null || it.getUnitPrice().compareTo(BigDecimal.ZERO) <= 0)
        throw new IllegalArgumentException("unit_price inválido");
    }

    // 3) back_urls ABSOLUTAS
    var back = PreferenceBackUrlsRequest.builder()
      .success(b + "/checkout/success")
      .pending(b + "/checkout/pending")
      .failure(b + "/checkout/failure")
      .build();

    // 4) Construir preference (autoReturn condicional por HTTPS)
    var builder = PreferenceRequest.builder()
      .items(items)
      .backUrls(back)
      .externalReference(cmd.externalReference());

    // Sólo habilitar autoReturn si la base es HTTPS (MP lo exige)
    if (b.startsWith("https://")) {
      builder.autoReturn("approved");
    }

    // 5) Webhook sólo si hay HTTPS y está habilitado
    String webhookUrl = b + "/payments/webhook";
    if (enableWebhook && webhookUrl.startsWith("https://")) {
      builder.notificationUrl(webhookUrl);
    }

    var prefReq = builder.build();

    try {
      var client = new com.mercadopago.client.preference.PreferenceClient();
      var pref = client.create(prefReq);
      return new CreatePrefResult(pref.getId(), pref.getInitPoint());
    } catch (com.mercadopago.exceptions.MPApiException e) {
      System.err.println("MPApiException status = " + e.getApiResponse().getStatusCode());
      System.err.println("MPApiException body   = " + e.getApiResponse().getContent());
      throw e;
    }
  }


}
