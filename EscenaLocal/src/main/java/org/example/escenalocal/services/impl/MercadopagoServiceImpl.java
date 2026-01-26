package org.example.escenalocal.services.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mercadopago.client.payment.PaymentClient;
import com.mercadopago.resources.payment.Payment;
import org.example.escenalocal.dtos.post.PostPaymentInfoDto;
import org.example.escenalocal.services.MercadopagoService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.util.Map;

@Service
public class MercadopagoServiceImpl implements MercadopagoService {

  private final RestTemplate restTemplate;
  private final ObjectMapper objectMapper;

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
      Payment payment = client.get(paymentId);

      if (payment == null) {
        System.out.println("⚠ MP no devolvió pago para id = " + paymentId);
        return null;
      }

      String status = payment.getStatus();
      String statusDetail = payment.getStatusDetail();
      String externalReference = payment.getExternalReference();
      Map<String, Object> md = payment.getMetadata();

      System.out.println("🔍 MP metadata para paymentId=" + paymentId + ": " + md);
      System.out.println("🔍 MP external_reference=" + externalReference);
      System.out.println("🔍 MP status=" + status + ", status_detail=" + statusDetail);

      Long usuarioId     = null;
      Long eventoId      = null;
      Long tipoEntradaId = null;
      Integer cantidad   = null;
      BigDecimal precioUnitario = null; // precio por entrada, NO total

      // 1) Leer todo lo posible desde metadata
      if (md != null) {
        usuarioId     = getLongOr(md, "usuarioId", "usuario_id");
        eventoId      = getLongOr(md, "eventoId", "evento_id");
        tipoEntradaId = getLongOr(md, "tipoEntradaId", "tipo_entrada_id");
        cantidad      = getInteger(md.get("cantidad"));
        precioUnitario = getBigDecimal(md.get("precio"));
      } else {
        System.out.println("⚠ Pago sin metadata id = " + paymentId);
      }

      // 2) Fallback para eventoId desde external_reference "EVT-17"
      if (eventoId == null && externalReference != null && externalReference.startsWith("EVT-")) {
        try {
          eventoId = Long.valueOf(externalReference.substring(4));
        } catch (NumberFormatException e) {
          System.out.println("⚠ No se pudo parsear eventoId desde external_reference=" + externalReference);
        }
      }

      // 3) Fallback desde additional_info.items (tipoEntradaId, cantidad, precioUnitario)
      if (payment.getAdditionalInfo() != null &&
        payment.getAdditionalInfo().getItems() != null &&
        !payment.getAdditionalInfo().getItems().isEmpty()) {

        var item = payment.getAdditionalInfo().getItems().get(0);

        if (tipoEntradaId == null && item.getId() != null) {
          try {
            tipoEntradaId = Long.valueOf(item.getId());
          } catch (NumberFormatException e) {
            System.out.println("⚠ No se pudo parsear tipoEntradaId desde item.id=" + item.getId());
          }
        }

        if (cantidad == null && item.getQuantity() != null) {
          cantidad = item.getQuantity();
        }

        if (precioUnitario == null && item.getUnitPrice() != null) {
          precioUnitario = item.getUnitPrice(); // ya es BigDecimal
        }
      }

      // 4) Si todavía no tenemos precioUnitario pero sí transactionAmount y cantidad, lo deducimos
      if (precioUnitario == null &&
        payment.getTransactionAmount() != null &&
        cantidad != null &&
        cantidad > 0) {

        BigDecimal total = payment.getTransactionAmount();
        precioUnitario = total
          .divide(BigDecimal.valueOf(cantidad), 2, BigDecimal.ROUND_HALF_UP);
      }

      PostPaymentInfoDto info = new PostPaymentInfoDto(
        payment.getId(),        // paymentId
        status,                 // status
        usuarioId,
        eventoId,
        tipoEntradaId,
        cantidad,
        precioUnitario,
        externalReference,
        statusDetail
      );

      System.out.println("🔍 PostPaymentInfoDto construido: " + info);

      return info;

    } catch (Exception e) {
      System.out.println("⚠ Error consultando MP payment ID=" + paymentId + ": " + e.getMessage());
      return null;
    }
  }

    /* =========================
       HELPERS SEGUROS
       ========================= */

  private Long getLong(Object value) {
    if (value == null) return null;
    if (value instanceof Number n) {
      return n.longValue();
    }
    return Long.valueOf(value.toString());
  }

  private Long getLongOr(Map<String, Object> md, String... keys) {
    for (String k : keys) {
      Object v = md.get(k);
      if (v != null) {
        return getLong(v);
      }
    }
    return null;
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
    if (value instanceof BigDecimal bd) return bd;
    if (value instanceof Number n) {
      return BigDecimal.valueOf(n.doubleValue());
    }
    return new BigDecimal(value.toString());
  }
}
