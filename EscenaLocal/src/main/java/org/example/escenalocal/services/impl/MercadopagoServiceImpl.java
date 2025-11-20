package org.example.escenalocal.services.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.escenalocal.dtos.post.PostPaymentInfoDto;
import org.example.escenalocal.services.MercadopagoService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
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

  @Override
  public PostPaymentInfoDto getPaymentInfo(Long paymentId) throws Exception {
    // URL de la API de Mercado Pago
    String url = "https://api.mercadopago.com/v1/payments/" + paymentId;

    // Headers con Authorization Bearer
    HttpHeaders headers = new HttpHeaders();
    headers.setBearerAuth(accessToken);
    HttpEntity<Void> request = new HttpEntity<>(headers);

    // Llamada GET a la API
    ResponseEntity<Map> response = restTemplate.exchange(url, HttpMethod.GET, request, Map.class);
    Map<String, Object> data = response.getBody();

    if (data == null) {
      throw new Exception("No se recibió información del pago");
    }

    // Creamos nuestro DTO PaymentInfo y mapeamos los datos relevantes
    PostPaymentInfoDto info = new PostPaymentInfoDto();

    // Estado del pago
    info.setStatus(data.get("status").toString());

    // ID del usuario (si se envió en la preferencia)
    Map<String, Object> payer = (Map<String, Object>) data.get("payer");
    if (payer != null && payer.get("id") != null) {
      info.setUsuarioId(Long.valueOf(payer.get("id").toString()));
    }

    // external_reference → usamos para identificar el evento
    String externalReference = (String) data.get("external_reference");
    if (externalReference != null && externalReference.contains("-")) {
      info.setEventoId(Long.valueOf(externalReference.split("-")[1]));
    }

    // items → lista de entradas compradas
    List<Map<String, Object>> items = (List<Map<String, Object>>) data.get("items");
    if (items != null && !items.isEmpty()) {
      Map<String, Object> item = items.get(0); // tomamos el primero
      info.setTipoEntradaId(Long.valueOf(item.get("id").toString()));
      info.setCantidad(Integer.parseInt(item.get("quantity").toString()));
      info.setPrecio(Double.parseDouble(item.get("unit_price").toString()));
    }

    return info;
  }
}
