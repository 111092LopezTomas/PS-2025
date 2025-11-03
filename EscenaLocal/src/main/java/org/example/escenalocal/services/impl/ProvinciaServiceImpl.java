package org.example.escenalocal.services.impl;

import jakarta.persistence.EntityNotFoundException;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.example.escenalocal.dtos.get.GetProvinciaDto;
import org.example.escenalocal.entities.ProvinciaEntity;
import org.example.escenalocal.repositories.ProvinciaRepository;
import org.example.escenalocal.services.ProvinciaService;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Data
public class ProvinciaServiceImpl implements ProvinciaService {

  private final ProvinciaRepository provinciaRepository;
  private final ModelMapper modelMapper = new ModelMapper();

  @Override
  public List<GetProvinciaDto> getProvincias() {
    List<ProvinciaEntity> provincias = provinciaRepository.findAll();
    List<GetProvinciaDto> lista = new ArrayList<>();

    for (ProvinciaEntity provinciaEntity : provincias) {
      GetProvinciaDto provinciaDto = modelMapper.map(provinciaEntity, GetProvinciaDto.class);
      lista.add(provinciaDto);
    }

    return  lista;
  }

  @Override
  public GetProvinciaDto getProvincia(Long id) {
    ProvinciaEntity provinciaEntity = provinciaRepository.findById(id)
      .orElseThrow(() -> new EntityNotFoundException("Provincia not found with id: " + id));

    return modelMapper.map(provinciaEntity, GetProvinciaDto.class);

  }
}
