package org.example.escenalocal.services.impl;

import jakarta.persistence.EntityNotFoundException;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.example.escenalocal.dtos.get.GetTipoEntradaDto;
import org.example.escenalocal.entities.TiposEntradaEntity;
import org.example.escenalocal.repositories.TiposEntradaRepository;
import org.example.escenalocal.services.TipoEntradaService;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@Data
@RequiredArgsConstructor
public class TipoEntradaServiceImpl implements TipoEntradaService {

  private final TiposEntradaRepository tiposEntradaRepository;
  private final ModelMapper modelMapper = new ModelMapper();

  @Override
  public List<GetTipoEntradaDto> getTipoEntrada() {
    List<TiposEntradaEntity>  tiposEntradaEntites = tiposEntradaRepository.findAll();
    List<GetTipoEntradaDto> list = new ArrayList<>();
    for (TiposEntradaEntity tiposEntradaEntity : tiposEntradaEntites) {
      GetTipoEntradaDto getTipoEntradaDto = modelMapper.map(tiposEntradaEntity, GetTipoEntradaDto.class);
      list.add(getTipoEntradaDto);
    }

    return list;
  }

  @Override
  public GetTipoEntradaDto getTipoEntradaById(Long id) {
    TiposEntradaEntity tiposEntradaEntity = tiposEntradaRepository.findById(id)
      .orElseThrow(() -> new EntityNotFoundException("Entrada not found with id: " + id));

    GetTipoEntradaDto getTipoEntradaDto = modelMapper.map(tiposEntradaEntity, GetTipoEntradaDto.class);

    return getTipoEntradaDto;
  }
}
