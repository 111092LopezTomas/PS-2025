package org.example.escenalocal.services.impl;

import jakarta.persistence.EntityNotFoundException;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.example.escenalocal.dtos.get.GetClasificacionDto;
import org.example.escenalocal.entities.ClasificacionEntity;
import org.example.escenalocal.repositories.ClasificacionRepository;
import org.example.escenalocal.services.ClasificacionService;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@Data
@RequiredArgsConstructor
public class ClasificacionServiceImpl implements ClasificacionService {

  private final ClasificacionRepository clasificacionRepository;
  private final ModelMapper modelMapper = new ModelMapper();

  @Override
  public List<GetClasificacionDto> getClasificaciones() {
    List<ClasificacionEntity> clasificacionEntities = clasificacionRepository.findAll();
    List<GetClasificacionDto> list =  new ArrayList<>();
    for (ClasificacionEntity clasificacion : clasificacionEntities) {
      GetClasificacionDto clasificacionDto = modelMapper.map(clasificacion, GetClasificacionDto.class);
      list.add(clasificacionDto);
    }

    return list;
  }

  @Override
  public GetClasificacionDto getClasificacionById(Long id) {
    ClasificacionEntity clasificacionEntity = clasificacionRepository.findById(id)
      .orElseThrow(() -> new EntityNotFoundException("Clasificacion not found with id: " + id));

    GetClasificacionDto getClasificacionDto = modelMapper.map(clasificacionEntity, GetClasificacionDto.class);

    return  getClasificacionDto;
  }
}
