import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, RouterModule } from '@angular/router';
import { CommonModule } from '@angular/common';
import { NgxChartsModule } from '@swimlane/ngx-charts';
import { DashboardService } from '../../services/dashboard.service';

@Component({
  selector: 'app-artista-dashboard',
  standalone: true,
  imports: [CommonModule, RouterModule, NgxChartsModule],
  templateUrl: './artista-dashboard.component.html',
  styleUrls: ['./artista-dashboard.component.css']
})
export class ArtistaDashboardComponent implements OnInit {

  artistaId!: number;
  from!: string;
  to!: string;

  loading = false;
  error?: string;

  dashboard: any;

  // Charts
  entradasPorDiaChart: { name: string; value: number }[] = [];
  entradasPorTipoChart: { name: string; value: number }[] = [];
  rankingEventosChart: { name: string; value: number }[] = [];

  view: [number, number] = [700, 300];

  constructor(
    private route: ActivatedRoute,
    private dashboardService: DashboardService
  ) {}

  ngOnInit(): void {
    this.route.queryParams.subscribe(params => {
      this.artistaId = Number(params['artistaId']);
      this.from = params['from'] || '2026-01-01';
      this.to = params['to'] || '2026-01-31';

      if (!this.artistaId) {
        this.error = 'Falta artistaId para cargar el dashboard';
        return;
      }

      this.cargar();
    });
  }

  cargar(): void {
    this.loading = true;
    this.error = undefined;

    this.dashboardService.getDashboardArtista(this.from, this.to, this.artistaId).subscribe({
      next: data => {
        this.dashboard = data;
        this.buildCharts();
        this.loading = false;
      },
      error: err => {
        console.error(err);
        this.error = 'No se pudo cargar el dashboard del artista';
        this.loading = false;
      }
    });
  }

  private buildCharts(): void {
    // Entradas por día
    const porDia = this.dashboard?.entradasPorDia || [];
    this.entradasPorDiaChart = porDia.map((p: any) => ({
      name: p.fecha,                  // 'YYYY-MM-DD'
      value: Number(p.cantidad ?? 0)
    }));

    // Entradas por tipo
    const porTipo = this.dashboard?.entradasPorTipo || [];
    this.entradasPorTipoChart = porTipo.map((t: any) => ({
      name: t.tipoEntradaNombre,
      value: Number(t.cantidadVendida ?? 0)
    }));

    // Ranking de eventos por asistencia
    const ranking = this.dashboard?.rankingEventos || [];
    this.rankingEventosChart = ranking.map((e: any) => ({
      name: e.eventoNombre,
      value: Number(e.entradasVendidas ?? 0)
    }));
  }

  formatFechaEjeX = (value: string): string => {
    if (!value) return '';
    const parts = value.split('-');
    if (parts.length === 3) {
      const [, mm, dd] = parts;
      return `${dd}/${mm}`;
    }
    return value;
  };

  formatNumero = (value: any): string => {
    const n = Number(value ?? 0);
    return new Intl.NumberFormat('es-AR').format(n);
  };
}
