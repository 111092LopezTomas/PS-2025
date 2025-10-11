package org.example.escenalocal.dtos;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;
import org.example.escenalocal.dtos.post.PostEventoDto;
import org.springframework.web.multipart.MultipartFile;

// DTO que "envuelve" el multipart
@Schema(name = "CreateEventoMultipart")
@Getter
@Setter
public class CreateEventoMultipart {

  @Schema(description = "DTO del evento (parte JSON)", implementation = PostEventoDto.class)
  private PostEventoDto dto;

  @Schema(description = "Archivo de imagen", type = "string", format = "binary")
  private MultipartFile file;

  // getters/setters
}

