import { ComponentFixture, TestBed } from '@angular/core/testing';

import { TicketsHistorialComponent } from './tickets-historial.component';

describe('TicketsHistorialComponent', () => {
  let component: TicketsHistorialComponent;
  let fixture: ComponentFixture<TicketsHistorialComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [TicketsHistorialComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(TicketsHistorialComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
