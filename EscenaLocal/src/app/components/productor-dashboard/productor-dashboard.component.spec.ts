import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ProductorDashboardComponent } from './productor-dashboard.component';

describe('ProductorDashboardComponent', () => {
  let component: ProductorDashboardComponent;
  let fixture: ComponentFixture<ProductorDashboardComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ProductorDashboardComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(ProductorDashboardComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
