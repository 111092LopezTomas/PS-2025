package org.example.escenalocal.payments;

import com.mercadopago.exceptions.MPApiException;
import com.mercadopago.exceptions.MPException;

public interface PaymentGateway {
  PaymentStatus getStatus(String externalReferenceOrPaymentId);
  CreatePrefResult createPreferenceWithBase(CreatePrefCommand cmd, String baseUrl) throws MPException, MPApiException;
}
