package org.example.escenalocal.services.impl;

import jakarta.persistence.EntityNotFoundException;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.example.escenalocal.dtos.get.GetArtistaDto;
import org.example.escenalocal.dtos.post.PostArtistaDto;
import org.example.escenalocal.dtos.put.PutArtistaDto;
import org.example.escenalocal.entities.ArtistaEntity;
import org.example.escenalocal.repositories.ArtistaRepository;
import org.example.escenalocal.services.ArtistaService;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@Data
@RequiredArgsConstructor
public class ArtistaServiceImpl implements ArtistaService {

    private final ModelMapper modelMapper =  new ModelMapper();
    private final ArtistaRepository artistaRepository;



    @Override
    public GetArtistaDto getArtistaById(Long id) {

        ArtistaEntity artistaEntity = artistaRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Artista not found with id: " + id));

        GetArtistaDto getArtistaDto = modelMapper.map(artistaEntity, GetArtistaDto.class);

        return getArtistaDto;
    }

    @Override
    public List<GetArtistaDto> getArtistas() {

        List<ArtistaEntity> artistaEntities = artistaRepository.findAll();
        List<GetArtistaDto> list =  new ArrayList<>();

        for (ArtistaEntity artistaEntity : artistaEntities) {
            GetArtistaDto artistaDto = modelMapper.map(artistaEntity, GetArtistaDto.class);
            list.add(artistaDto);
        }

        return list;
    }

    @Override
    public GetArtistaDto createArtista(PostArtistaDto postArtistaDto) {
        return null;
    }

    @Override
    public GetArtistaDto updateArtista(Long id, PutArtistaDto putArtistaDto) {
        return null;
    }

    @Override
    public void deleteArtista(Long id) {

    }
}
