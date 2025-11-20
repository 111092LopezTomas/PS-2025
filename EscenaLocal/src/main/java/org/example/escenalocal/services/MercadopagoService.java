package org.example.escenalocal.services;

import org.example.escenalocal.dtos.post.PostPaymentInfoDto;

public interface MercadopagoService {
  PostPaymentInfoDto getPaymentInfo(Long paymentId) throws Exception;
}
