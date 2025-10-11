import { Component, OnInit } from '@angular/core';
import { EventService, EventGet } from '../../services/event.service';
import { CommonModule, DatePipe } from '@angular/common';
import { Router, RouterModule } from '@angular/router';


@Component({
  selector: 'app-event-list',
  standalone: true,
  imports: [CommonModule, DatePipe,RouterModule],
  templateUrl: './event-list.component.html',
  styleUrls: ['./event-list.component.css']
})
export class EventListComponent implements OnInit {
  events: EventGet[] = [];
  imagenUrl: string = "";
  apiBase = 'http://localhost:8080';
  

  mostrarFila1 = false;
  

  constructor(private eventService: EventService, private router: Router) {}

  ngOnInit(): void {
    this.cargarEventos();
  }

  cargarEventos() {
    this.eventService.getEvents().subscribe(data => {
      this.events = data;
    });

  }
    cargarFlyer(id: number) {
      this.eventService.getImagenEvento(id).subscribe({
    next: (blob) => {
      this.imagenUrl = URL.createObjectURL(blob);
    },
    error: (err) => {
      console.error('No se pudo cargar la imagen' + id, err);
    }
  });
    }
  
  toggleFila1() {
    this.mostrarFila1 = !this.mostrarFila1;
  }

  VerEvento(id: number) {
    console.log('Navegando a evento ID:', id); // Debug
  this.router.navigate(['/evento', id])
    .then(() => console.log('Navegación exitosa'))
    .catch(err => console.error('Error en navegación:', err));

  }

}
