package org.example.escenalocal.payments;

import com.mercadopago.exceptions.MPApiException;
import com.mercadopago.exceptions.MPException;

public interface PaymentGateway {
  CreatePrefResult createPreference(CreatePrefCommand cmd) throws MPException, MPApiException;
  PaymentStatus getStatus(String externalReferenceOrPaymentId);
}
