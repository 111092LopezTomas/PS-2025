package org.example.escenalocal.services.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mercadopago.client.payment.PaymentClient;
import com.mercadopago.resources.payment.Payment;
import org.example.escenalocal.dtos.post.PostPaymentInfoDto;
import org.example.escenalocal.services.MercadopagoService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@Service
public class  MercadopagoServiceImpl implements MercadopagoService {

  private final RestTemplate restTemplate;
  private final ObjectMapper objectMapper;

  // Inyectamos el access token desde application.yml
  @Value("${mercadopago.access-token}")
  private String accessToken;

  public MercadopagoServiceImpl(RestTemplate restTemplate, ObjectMapper objectMapper) {
    this.restTemplate = restTemplate;
    this.objectMapper = objectMapper;
  }

  @Override
  public PostPaymentInfoDto getPaymentInfo(Long paymentId) {
    try {
      PaymentClient client = new PaymentClient();
      Payment p = client.get(paymentId);

      if (p == null) return null;

      if (p.getOrder() == null || p.getOrder().getId() == null) {
        System.out.println("⚠ Pago sin order/preferenceId id=" + paymentId);
        return null;
      }

      Long orderId = p.getOrder().getId();

      var order = new com.mercadopago.client.merchantorder.MerchantOrderClient()
        .get(orderId);

      String preferenceId = order.getPreferenceId();

      var pref = new com.mercadopago.client.preference.PreferenceClient()
        .get(preferenceId);


      Map<String, Object> md = pref.getMetadata();
      if (md == null) return null;

      return new PostPaymentInfoDto(
        paymentId,
        p.getStatus(),
        getLong(md.get("usuarioId")),
        getLong(md.get("eventoId")),
        getLong(md.get("tipoEntradaId")),
        getInteger(md.get("cantidad")),
        getBigDecimal(md.get("precio")),
        p.getExternalReference(),
        p.getStatusDetail()
      );

    } catch (Exception e) {
      System.out.println("⚠ Error consultando MP payment ID=" + paymentId + ": " + e.getMessage());
      return null;
    }
  }

  private Long getLong(Object value) {
    if (value == null) return null;
    if (value instanceof Number n) {
      return n.longValue();
    }
    return Long.valueOf(value.toString());
  }

  private Integer getInteger(Object value) {
    if (value == null) return null;
    if (value instanceof Number n) {
      return n.intValue();
    }
    return Integer.valueOf(value.toString());
  }

  private BigDecimal getBigDecimal(Object value) {
    if (value == null) return null;
    if (value instanceof Number n) {
      return BigDecimal.valueOf(n.doubleValue());
    }
    return new BigDecimal(value.toString());
  }


}
