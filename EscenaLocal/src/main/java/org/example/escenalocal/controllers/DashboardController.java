package org.example.escenalocal.controllers;

import lombok.RequiredArgsConstructor;
import org.example.escenalocal.dashboard.ArtistaDashboardDto;
import org.example.escenalocal.dashboard.ProductorDashboardDto;
import org.example.escenalocal.services.DashboardService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;

import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@RestController
@RequestMapping("/dashboard")
@RequiredArgsConstructor
public class DashboardController {

  private final DashboardService dashboardService;

  @GetMapping("/productor")
  public ProductorDashboardDto getDashboardProductor(
    @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
    @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to,
    @RequestParam Long productorId
  ) {
    return dashboardService.getDashboardProductor(productorId, from, to);
  }

  @GetMapping("/artista")
  public ArtistaDashboardDto get(@RequestParam Long artistaId,
                                 @RequestParam String from,
                                 @RequestParam String to) {
    LocalDateTime fromDt = LocalDate.parse(from).atStartOfDay();
    LocalDateTime toDt = LocalDate.parse(to).atTime(23, 59, 59);
    return dashboardService.dashboardArtista(artistaId, fromDt, toDt);
  }
}
