package org.example.escenalocal.services;

import org.example.escenalocal.dashboard.ProductorDashboardDto;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
public interface DashboardService {

  ProductorDashboardDto getDashboardProductor(Long productorId, LocalDate from, LocalDate to);
}
