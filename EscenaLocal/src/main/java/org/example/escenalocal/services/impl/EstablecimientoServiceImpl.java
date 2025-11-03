package org.example.escenalocal.services.impl;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.example.escenalocal.dtos.get.GetEstablecimientoDto;
import org.example.escenalocal.entities.EstablecimientoEntity;
import org.example.escenalocal.repositories.EstablecimientoRepository;
import org.example.escenalocal.services.EstablecimientoService;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Data
@RequiredArgsConstructor
public class EstablecimientoServiceImpl implements EstablecimientoService {

  private final EstablecimientoRepository establecimientoRepository;
  private final ModelMapper modelMapper = new ModelMapper();


  @Override
  public List<GetEstablecimientoDto> getEstablecimientos() {

      return establecimientoRepository.finAllEstablecimientos()
        .stream()
        .map(this::toDto)
        .toList();
    }

    private GetEstablecimientoDto toDto(EstablecimientoEntity es) {
      return new GetEstablecimientoDto(
        es.getId(),
        es.getEstablecimiento(),
        es.getCapacidad(),
        es.getDireccion(),
        es.getBarrio() != null ? es.getBarrio().getBarrio() : null,
        es.getBarrio() != null && es.getBarrio().getCiudad() != null
          ? es.getBarrio().getCiudad().getCiudad()
          : null,
        es.getBarrio() != null
          && es.getBarrio().getCiudad() != null
          && es.getBarrio().getCiudad().getProvincia() != null
          ? es.getBarrio().getCiudad().getProvincia().getProvincia()
          : null
      );
    }


  @Override
  public GetEstablecimientoDto getEstablecimientoById(Long id) {
    EstablecimientoEntity es = establecimientoRepository
      .findEstablecimientoById(id)
      .orElseThrow(() -> new RuntimeException("No existe establecimiento " + id));

    return new GetEstablecimientoDto(
      es.getId(),
      es.getEstablecimiento(),
      es.getCapacidad(),
      es.getDireccion(),
      es.getBarrio() != null ? es.getBarrio().getBarrio() : null,
      es.getBarrio() != null && es.getBarrio().getCiudad() != null
        ? es.getBarrio().getCiudad().getCiudad()
        : null,
      es.getBarrio() != null
        && es.getBarrio().getCiudad() != null
        && es.getBarrio().getCiudad().getProvincia() != null
        ? es.getBarrio().getCiudad().getProvincia().getProvincia()
        : null
    );
  }
}
