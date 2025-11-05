import { ComponentFixture, TestBed } from '@angular/core/testing';

import { LoginArtProdComponent } from './login-art-prod.component';

describe('LoginArtProdComponent', () => {
  let component: LoginArtProdComponent;
  let fixture: ComponentFixture<LoginArtProdComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [LoginArtProdComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(LoginArtProdComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
