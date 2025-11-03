import { Component, OnInit } from '@angular/core';
import { FormControl, ReactiveFormsModule } from '@angular/forms';
import { EventService } from '../../services/event.service';
import { debounceTime, distinctUntilChanged } from 'rxjs';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-event-search',
  imports: [ReactiveFormsModule, CommonModule],
  templateUrl: './event-search.component.html',
  styleUrl: './event-search.component.css'
})

export class EventSearchComponent implements OnInit {
  searchControl = new FormControl('');
  provinciaControl = new FormControl('');
  provincias: any[] = [];

  constructor(private eventService: EventService) {}

  ngOnInit(): void {
    // Búsqueda con debounce
    this.searchControl.valueChanges
      .pipe(
        debounceTime(300),
        distinctUntilChanged()
      )
      .subscribe(() => this.notificar());

    // Provincia sin debounce
    this.provinciaControl.valueChanges
      .subscribe(() => this.notificar());

    this.cargarProvincias();
  }

  private notificar(): void {
    this.eventService.actualizarFiltros({
      busqueda: this.searchControl.value || '',
      provincia: this.provinciaControl.value || ''
    });
  }

  cargarProvincias(): void {
    this.eventService.getProvincias().subscribe({
      next: (data) => {
        this.provincias = data;
      },
      error: (error) => {
        console.error('Error al cargar provincias:', error);
      }
    });
  }
}
