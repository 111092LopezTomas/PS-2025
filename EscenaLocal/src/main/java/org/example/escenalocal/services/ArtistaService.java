package org.example.escenalocal.services;

import org.example.escenalocal.dtos.get.GetArtistaDto;
import org.example.escenalocal.dtos.post.PostArtistaDto;
import org.example.escenalocal.dtos.put.PutArtistaDto;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface ArtistaService {

    GetArtistaDto getArtistaById(Long id);
    List<GetArtistaDto> getArtistas();
    GetArtistaDto createArtista(PostArtistaDto postArtistaDto);
    GetArtistaDto updateArtista(Long id, PutArtistaDto putArtistaDto);
    void deleteArtista(Long id);
}
