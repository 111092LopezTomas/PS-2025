import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

export interface Event {
  id: number;
  name: string;
  date: string;
  price: number;
  location: string;
}

@Injectable({
  providedIn: 'root'
})
export class EventService {
  private apiUrl = '/api/events'; // gracias al proxy

  constructor(private http: HttpClient) {}

  getEvents(): Observable<Event[]> {
    return this.http.get<Event[]>(this.apiUrl);
  }
}
