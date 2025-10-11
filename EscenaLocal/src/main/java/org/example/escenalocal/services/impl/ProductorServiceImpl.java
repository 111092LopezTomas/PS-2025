package org.example.escenalocal.services.impl;

import jakarta.persistence.EntityNotFoundException;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.example.escenalocal.dtos.get.GetProductorDto;
import org.example.escenalocal.entities.ProductorEntity;
import org.example.escenalocal.repositories.ProductorRepository;
import org.example.escenalocal.services.ProductorService;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@Data
@RequiredArgsConstructor
public class ProductorServiceImpl implements ProductorService {

  private final ProductorRepository productorRepository;
  private final ModelMapper modelMapper =  new ModelMapper();

  @Override
  public List<GetProductorDto> getProductores() {
    List<ProductorEntity> productores = productorRepository.findAll();
    List<GetProductorDto> list = new ArrayList<>();

    for (ProductorEntity productorEntity : productores) {
      GetProductorDto getProductorDto = modelMapper.map(productorEntity, GetProductorDto.class);
      list.add(getProductorDto);
    }

    return list;
  }

  @Override
  public GetProductorDto getProductorById(Long id) {

    ProductorEntity productorEntity = productorRepository.findById(id)
      .orElseThrow(() -> new EntityNotFoundException("Productor not found with id: " + id));

    GetProductorDto getProductorDto = modelMapper.map(productorEntity, GetProductorDto.class);

    return getProductorDto;
  }
}
