package org.example.escenalocal.services;

import org.example.escenalocal.dtos.get.GetProvinciaDto;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface ProvinciaService {

    List<GetProvinciaDto> getProvincias();
    GetProvinciaDto getProvincia(Long id);
}
