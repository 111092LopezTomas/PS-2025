import { CommonModule } from '@angular/common';
import { HttpClient } from '@angular/common/http';
import { Component, EventEmitter, Input, OnInit, Output } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { EstablishmentService } from '../../services/establishment.service';


export interface EstablecimientoDetalle {
  id: number;
  nombre: string;
  direccion: string;
  capacidad: number;
  barrio: string;
  ciudad: string;
  provincia: string;
}

@Component({
  selector: 'app-establishment',
  imports: [CommonModule],
  templateUrl: './establishment.component.html',
  styleUrl: './establishment.component.css'
})
export class EstablishmentComponent implements OnInit {

 establecimiento: EstablecimientoDetalle | null = null;
  loading = true;
  error: string | null = null;

  constructor(
    private route: ActivatedRoute,
    private establishmentService: EstablishmentService
  ) {}

  ngOnInit(): void {
    this.cargar();
  }

  cargar(): void {
    this.loading = true;
    this.error = null;

    const id = this.route.snapshot.paramMap.get('id');
    if (!id) {
      this.error = 'No se especificó un establecimiento válido.';
      this.loading = false;
      return;
    }

    this.establishmentService.getEstablecimientoById(id).subscribe({
      next: (data) => {
        this.establecimiento = data;
        this.loading = false;
      },
      error: (err) => {
        console.error('Error al obtener establecimiento', err);
        this.error = 'No se pudo cargar el establecimiento.';
        this.loading = false;
      },
    });
  }

  volver(): void {
    history.back();
  }

}
