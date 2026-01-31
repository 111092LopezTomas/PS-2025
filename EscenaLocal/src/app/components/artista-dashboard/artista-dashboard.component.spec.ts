import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ArtistaDashboardComponent } from './artista-dashboard.component';

describe('ArtistaDashboardComponent', () => {
  let component: ArtistaDashboardComponent;
  let fixture: ComponentFixture<ArtistaDashboardComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ArtistaDashboardComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(ArtistaDashboardComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
