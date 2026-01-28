import { ComponentFixture, TestBed } from '@angular/core/testing';

import { GraficosProductorComponent } from './graficos-productor.component';

describe('GraficosProductorComponent', () => {
  let component: GraficosProductorComponent;
  let fixture: ComponentFixture<GraficosProductorComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [GraficosProductorComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(GraficosProductorComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
