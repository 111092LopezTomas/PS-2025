import { Component, OnInit } from '@angular/core';

@Component({
  selector: 'app-ayuda',
  imports: [],
  templateUrl: './ayuda.component.html',
  styleUrl: './ayuda.component.css'
})
export class AyudaComponent implements OnInit {

  ngOnInit(): void {
    window.scrollTo(0, 0);
  }

}
