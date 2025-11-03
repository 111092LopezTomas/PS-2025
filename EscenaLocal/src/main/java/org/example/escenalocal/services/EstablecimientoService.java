package org.example.escenalocal.services;

import org.example.escenalocal.dtos.get.GetEstablecimientoDto;
import org.example.escenalocal.dtos.get.GetEventoDto;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

@Service
public interface EstablecimientoService {

  List<GetEstablecimientoDto> getEstablecimientos();
  GetEstablecimientoDto getEstablecimientoById(@PathVariable Long id);
}
