package org.example.escenalocal.services.impl;

import jakarta.persistence.EntityNotFoundException;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.example.escenalocal.dtos.get.GetEstablecimientoDto;
import org.example.escenalocal.entities.EstablecimientoEntity;
import org.example.escenalocal.repositories.EstablecimientoRepository;
import org.example.escenalocal.services.EstablecimientoService;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@Data
@RequiredArgsConstructor
public class EstablecimientoServiceImpl implements EstablecimientoService {

  private final EstablecimientoRepository establecimientoRepository;
  private final ModelMapper modelMapper = new ModelMapper();


  @Override
  public List<GetEstablecimientoDto> getEstablecimientos() {
    List<EstablecimientoEntity> establecimientoEntities = establecimientoRepository.findAll();
    List<GetEstablecimientoDto> list = new ArrayList<>();
    for (EstablecimientoEntity establecimientoEntity : establecimientoEntities) {
      GetEstablecimientoDto getEstablecimientoDto = modelMapper.map(establecimientoEntity, GetEstablecimientoDto.class);
      list.add(getEstablecimientoDto);
    }

    return list;
  }

  @Override
  public GetEstablecimientoDto getEstablecimientoById(Long id) {
    EstablecimientoEntity establecimientoEntity = establecimientoRepository.findById(id)
      .orElseThrow(() -> new EntityNotFoundException("Establecimiento not found with id: " + id));

    GetEstablecimientoDto getEstablecimientoDto =  modelMapper.map(establecimientoEntity, GetEstablecimientoDto.class);

    return getEstablecimientoDto;
  }
}
