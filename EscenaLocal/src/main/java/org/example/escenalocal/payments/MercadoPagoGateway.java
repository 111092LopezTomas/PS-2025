package org.example.escenalocal.payments;

import com.mercadopago.client.payment.PaymentClient;
import com.mercadopago.client.preference.PreferenceBackUrlsRequest;
import com.mercadopago.client.preference.PreferenceItemRequest;
import com.mercadopago.client.preference.PreferenceRequest;
import com.mercadopago.exceptions.MPApiException;
import com.mercadopago.exceptions.MPException;
import com.mercadopago.resources.preference.Preference;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.stream.Collectors;

@Service
public class MercadoPagoGateway implements PaymentGateway {


  @Value("${app.base-url:http://localhost:8080}")
  private String baseUrl;


  @Override
  public CreatePrefResult createPreference(CreatePrefCommand cmd) throws MPException, MPApiException {
    var items = cmd.items().stream().map(i ->
      PreferenceItemRequest.builder()
        .id(i.id())
        .title(i.title())
        .description(i.description())
        .quantity(i.quantity())
        .currencyId("ARS")
        .unitPrice(i.unitPrice())
        .build()
    ).collect(Collectors.toList());


    var back = PreferenceBackUrlsRequest.builder()
      .success(baseUrl + "/checkout/success")
      .pending(baseUrl + "/checkout/pending")
      .failure(baseUrl + "/checkout/failure")
      .build();


    var prefReq = PreferenceRequest.builder()
      .items(items)
      .backUrls(back)
      .autoReturn("approved")
      .externalReference(cmd.externalReference())
      .notificationUrl(baseUrl + "/api/payments/webhook")
      .build();

    try {
      var client = new com.mercadopago.client.preference.PreferenceClient();
      var pref = client.create(prefReq);
      return new CreatePrefResult(pref.getId(), pref.getInitPoint());

    } catch (com.mercadopago.exceptions.MPApiException e) {
      System.err.println("MPApiException status = " + e.getApiResponse().getStatusCode());
      System.err.println("MPApiException body   = " + e.getApiResponse().getContent()); // <- JSON con 'cause'
      throw e; // o devolvé un 400 con ese JSON para verlo desde el front
    }

//    var client = new com.mercadopago.client.preference.PreferenceClient();
//    Preference pref = client.create(prefReq);
//
//
//    return new CreatePrefResult(pref.getId(), pref.getInitPoint());
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
}
