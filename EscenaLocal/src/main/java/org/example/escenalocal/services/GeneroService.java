package org.example.escenalocal.services;

import org.example.escenalocal.dtos.get.GetGeneroDto;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface GeneroService {

  List<GetGeneroDto> getGeneros();
  GetGeneroDto getGenero(Long id);
}
