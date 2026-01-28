import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { DashboardService } from '../../services/dashboard.service';
import { ProductorDashboardDto } from '../../models/dashboard.model';
import { ActivatedRoute, Router } from '@angular/router';

@Component({
  selector: 'app-productor-dashboard',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './productor-dashboard.component.html',
  styleUrls: ['./productor-dashboard.component.css']
})
export class ProductorDashboardComponent implements OnInit {

  dashboard?: ProductorDashboardDto;
  loading = false;
  error?: string;
  productorId!: number;

  // filtros de fecha (formato yyyy-MM-dd)
  from: string;
  to: string;

  constructor(private dashboardService: DashboardService, private route: ActivatedRoute, private router: Router) {
    const hoy = new Date();
    const primerDiaMes = new Date(hoy.getFullYear(), hoy.getMonth(), 1);

    this.from = primerDiaMes.toISOString().substring(0, 10);
    this.to   = hoy.toISOString().substring(0, 10);
  }

  ngOnInit(): void {
    this.route.queryParams.subscribe(params => {
    this.productorId = Number(params['productorId']);

    if (!this.productorId) {
      console.error("Error: no llegó productorId en la URL");
      return;
    }

    this.cargarDashboard();
  });
}

  cargarDashboard(): void {
  this.loading = true;
  this.error = undefined;

  this.dashboardService.getDashboardProductor(this.from, this.to, this.productorId)
    .subscribe({
      next: (data) => {
        console.log('Dashboard recibido', data);
        this.dashboard = data;
        this.loading = false;
      },
      error: (err) => {
        console.error('Error cargando dashboard', err, err.error);
        this.error = 'No se pudo cargar el dashboard';
        this.loading = false;
      }
    });
}

verGraficos(): void {
    if (!this.productorId) {
      console.error('No hay productorId para ver gráficos');
      return;
    }

    this.router.navigate(
      ['dashboard/productor/graficos'],
      {
        queryParams: {
          productorId: this.productorId,
          from: this.from,
          to: this.to
        }
      }
    );
  }
}
