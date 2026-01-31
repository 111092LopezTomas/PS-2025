package org.example.escenalocal.services;

import org.example.escenalocal.dashboard.ArtistaDashboardDto;
import org.example.escenalocal.dashboard.ProductorDashboardDto;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Service
public interface DashboardService {

  ProductorDashboardDto getDashboardProductor(Long productorId, LocalDate from, LocalDate to);

    ArtistaDashboardDto dashboardArtista(Long artistaId, LocalDateTime fromDt, LocalDateTime toDt);
}
