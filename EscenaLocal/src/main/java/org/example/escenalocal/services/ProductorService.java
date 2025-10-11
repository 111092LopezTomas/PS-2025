package org.example.escenalocal.services;

import org.example.escenalocal.dtos.get.GetProductorDto;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface ProductorService {

  List<GetProductorDto> getProductores();
  GetProductorDto getProductorById(Long id);
}
