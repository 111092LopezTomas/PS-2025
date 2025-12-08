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
public class MercadopagoServiceImpl implements MercadopagoService {

  private final RestTemplate restTemplate;
  private final ObjectMapper objectMapper;

  // Inyectamos el access token desde application.yml
  @Value("${mercadopago.access-token}")
  private String accessToken;

  public MercadopagoServiceImpl(RestTemplate restTemplate, ObjectMapper objectMapper) {
    this.restTemplate = restTemplate;
    this.objectMapper = objectMapper;
  }

//  @Override
//  public PostPaymentInfoDto getPaymentInfo(Long paymentId) {
//    String url = "https://api.mercadopago.com/v1/payments/" + paymentId;
//
//    try {
//      // Headers con Authorization Bearer
//      HttpHeaders headers = new HttpHeaders();
//      headers.setBearerAuth(accessToken);
//      HttpEntity<Void> request = new HttpEntity<>(headers);
//
//      // Llamada GET a la API
//      ResponseEntity<Map> response = restTemplate.exchange(url, HttpMethod.GET, request, Map.class);
//      Map<String, Object> data = response.getBody();
//
//      if (data == null) {
//        System.out.println("⚠️ No se recibió información del pago para id=" + paymentId);
//        return null;
//      }
//
//      // Creamos nuestro DTO PaymentInfo y mapeamos los datos relevantes
//      PostPaymentInfoDto info = new PostPaymentInfoDto();
//
//      // Estado del pago
//      Object statusObj = data.get("status");
//      if (statusObj != null) {
//        info.setStatus(statusObj.toString());
//      }
//
//      // ID del usuario (si se envió en la preferencia)
//      Map<String, Object> payer = (Map<String, Object>) data.get("payer");
//      if (payer != null && payer.get("id") != null) {
//        info.setUsuarioId(Long.valueOf(payer.get("id").toString()));
//      }
//
//      // external_reference → usamos para identificar el evento
//      String externalReference = (String) data.get("external_reference");
//      if (externalReference != null && externalReference.contains("-")) {
//        // ej: USERID-EVENTOID
//        String[] parts = externalReference.split("-");
//        if (parts.length > 1) {
//          info.setEventoId(Long.valueOf(parts[1]));
//        }
//      }
//
//      // items → lista de entradas compradas
//      List<Map<String, Object>> items = (List<Map<String, Object>>) data.get("items");
//      if (items != null && !items.isEmpty()) {
//        Map<String, Object> item = items.get(0); // tomamos el primero
//        if (item.get("id") != null) {
//          info.setTipoEntradaId(Long.valueOf(item.get("id").toString()));
//        }
//        if (item.get("quantity") != null) {
//          info.setCantidad(Integer.parseInt(item.get("quantity").toString()));
//        }
//        if (item.get("unit_price") != null) {
//          info.setPrecio(Double.parseDouble(item.get("unit_price").toString()));
//        }
//      }
//
//      return info;
//
//    } catch (HttpClientErrorException e) {
//      if (e.getStatusCode() == HttpStatus.NOT_FOUND) {
//        // 💡 Caso típico en simulaciones: MP manda un id inventado (ej: 123456)
//        System.out.println("⚠️ Pago no encontrado en MP para id=" + paymentId + " (404 not_found)");
//        return null;
//      }
//
//      System.out.println("⚠️ Error HTTP al consultar MP para id=" + paymentId + ": " + e.getMessage());
//      return null;
//
//    } catch (Exception e) {
//      System.out.println("⚠️ Error inesperado al consultar MP para id=" + paymentId + ": " + e.getMessage());
//      return null;
//    }
//  }
@Override
public PostPaymentInfoDto getPaymentInfo(Long paymentId) {

  try {
    PaymentClient client = new PaymentClient();

    // Llamamos a MP para obtener datos del pago
    Payment p = client.get(paymentId);

    if (p == null) {
      System.out.println("⚠ MP no devolvió pago para id = " + paymentId);
      return null;
    }

    // Extraer metadata enviado desde la preferencia o pago API
    Map<String, Object> md = p.getMetadata();

    Long usuarioId = md.get("usuarioId") != null ? Long.valueOf(md.get("usuarioId").toString()) : null;
    Long eventoId  = md.get("eventoId")  != null ? Long.valueOf(md.get("eventoId").toString())  : null;
    Long tipoEntradaId = md.get("tipoEntradaId") != null ? Long.valueOf(md.get("tipoEntradaId").toString()) : null;
    Integer cantidad   = md.get("cantidad") != null ? Integer.valueOf(md.get("cantidad").toString()) : null;
    BigDecimal precio  = md.get("precio") != null ? new BigDecimal(md.get("precio").toString()) : null;

    return new PostPaymentInfoDto(
      paymentId,
      p.getStatus(),      // approved, rejected, pending
      usuarioId,
      eventoId,
      tipoEntradaId,
      cantidad,
      precio
    );

  } catch (Exception e) {
    e.printStackTrace();
    System.out.println("⚠ Error consultando MP payment ID=" + paymentId + ": " + e.getMessage());
    return null;
  }
}
}
