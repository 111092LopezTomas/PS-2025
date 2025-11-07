import { Routes } from '@angular/router';
import { EventListComponent } from './components/event-list/event-list.component';
import { EventFormComponent } from './components/event-form/event-form.component';
import { EventViewComponent } from './components/event-view/event-view.component';
import { CheckoutComponent } from './components/checkout/checkout.component';
import { AuthGuard } from './guards/auth.guard';
import { LoginComponent } from './components/login/login.component';
import { LoginFormComponent } from './components/login-form/login-form.component';

import { EventUpdateComponent } from './components/event-update/event-update.component';
import { EstablishmentComponent } from './components/establishment/establishment.component';
import { LoginArtProdComponent } from './components/login-art-prod/login-art-prod.component';
import { RoleGuard } from './role.guard';
import { LoginFormArtProdComponent } from './components/login-form-art-prod/login-form-art-prod.component';
import { UserProfileComponent } from './components/user-profile/user-profile.component';
import { UserEditComponent } from './components/user-edit/user-edit.component';

export const routes: Routes = [
  
  { path: '', redirectTo: '/eventos', pathMatch: 'full' },
  { path: 'eventos', component: EventListComponent },
  { path: 'eventos/nuevo', component: EventFormComponent, canActivate: [RoleGuard],
    data: { role: 'ROL_PRODUCTOR' }},
  { path: 'evento/:id', component: EventViewComponent },
  { path: 'eventos/editar/:id', component: EventUpdateComponent, canActivate: [RoleGuard],
    data: { role: 'ROL_PRODUCTOR' }},
  { path: 'checkout', component: CheckoutComponent },
  { path: 'checkout/success', component: CheckoutComponent, canActivate: [AuthGuard] },
  { path: 'checkout/pending', component: CheckoutComponent, canActivate: [AuthGuard] },
  { path: 'checkout/failure', component: CheckoutComponent, canActivate: [AuthGuard] },
  { path: 'establecimientos/:id', component: EstablishmentComponent},
  { path: 'login', component: LoginComponent },
  { path: 'login/nuevo', component: LoginFormComponent },
  { path: 'login/art-prod', component: LoginArtProdComponent },
  { path: 'login/art-prod/nuevo', component: LoginFormArtProdComponent},
  { path: 'perfil', component: UserProfileComponent},
  { path: 'perfil/editar', component: UserEditComponent},
  { path: '**', redirectTo: '/eventos' }
  
];
