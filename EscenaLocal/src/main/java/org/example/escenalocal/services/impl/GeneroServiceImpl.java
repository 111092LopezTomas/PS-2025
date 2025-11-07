package org.example.escenalocal.services.impl;

import jakarta.persistence.EntityNotFoundException;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.example.escenalocal.dtos.get.GetGeneroDto;
import org.example.escenalocal.entities.GeneroEntity;
import org.example.escenalocal.repositories.GeneroRepository;
import org.example.escenalocal.services.GeneroService;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Data
public class GeneroServiceImpl implements GeneroService {

  private final GeneroRepository generoRepository;
  private final ModelMapper modelMapper = new ModelMapper();

  @Override
  public List<GetGeneroDto> getGeneros() {
    List<GeneroEntity> generos = generoRepository.findAll();
    List<GetGeneroDto> getGenerosDto = new ArrayList<>();

    for (GeneroEntity genero : generos) {
      GetGeneroDto getGeneroDto = new GetGeneroDto();
      getGeneroDto.setGenero(genero.getGenero());
      getGeneroDto.setId(genero.getId());
      getGeneroDto.setGenero(genero.getGenero());
      getGenerosDto.add(getGeneroDto);

    }

    return getGenerosDto;
  }

  @Override
  public GetGeneroDto getGenero(Long id) {
    GeneroEntity genero = generoRepository.findById(id)
      .orElseThrow(() -> new EntityNotFoundException("genero not found with id: " + id));

    GetGeneroDto getGeneroDto = modelMapper.map(genero, GetGeneroDto.class);

    return  getGeneroDto;


  }
}
