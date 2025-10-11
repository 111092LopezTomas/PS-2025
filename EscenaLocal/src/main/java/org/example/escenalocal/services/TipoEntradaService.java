package org.example.escenalocal.services;

import org.example.escenalocal.dtos.get.GetTipoEntradaDto;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface TipoEntradaService {

  List<GetTipoEntradaDto> getTipoEntrada();
  GetTipoEntradaDto getTipoEntradaById(Long id);
}
