import { ComponentFixture, TestBed } from '@angular/core/testing';

import { LoginFormArtProdComponent } from './login-form-art-prod.component';

describe('LoginFormArtProdComponent', () => {
  let component: LoginFormArtProdComponent;
  let fixture: ComponentFixture<LoginFormArtProdComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [LoginFormArtProdComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(LoginFormArtProdComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
