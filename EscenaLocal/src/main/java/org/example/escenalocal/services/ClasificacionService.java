package org.example.escenalocal.services;

import org.example.escenalocal.dtos.get.GetClasificacionDto;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface ClasificacionService {

  List<GetClasificacionDto> getClasificaciones();
  GetClasificacionDto getClasificacionById(Long id);
}
