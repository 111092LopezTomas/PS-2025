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
import { TerminosCondicionesComponent } from './components/terminos-condiciones/terminos-condiciones.component';
import { PoliticaPrivacidadComponent } from './components/politica-privacidad/politica-privacidad.component';
import { AyudaComponent } from './components/ayuda/ayuda.component';
import { ForgotPasswordComponent } from './components/forgot-password/forgot-password.component';
import { ResetPasswordComponent } from './components/reset-password/reset-password.component';
import { ChangePasswordComponent } from './components/change-password/change-password.component';
import { ProductorDashboardComponent } from './components/productor-dashboard/productor-dashboard.component';
import { GraficosProductorComponent } from './components/graficos-productor/graficos-productor.component';
import { TicketsHistorialComponent } from './components/tickets-historial/tickets-historial.component';
import { ArtistaDashboardComponent } from './components/artista-dashboard/artista-dashboard.component';
import { NotificationPreferencesComponent } from './components/notification-preferences/notification-preferences.component';
import { CheckoutSuccessComponent } from './components/checkout-success/checkout-success.component';

export const routes: Routes = [
  
  { path: '', redirectTo: '/eventos', pathMatch: 'full' },
  { path: 'eventos', component: EventListComponent },
  { path: 'eventos/productor/:id', component: EventListComponent},
  { path: 'eventos/artista/:id', component: EventListComponent },
  { path: 'eventos/nuevo', component: EventFormComponent, canActivate: [RoleGuard],
    data: { role: 'ROL_PRODUCTOR' }},
  { path: 'evento/:id', component: EventViewComponent },
  { path: 'eventos/editar/:id', component: EventUpdateComponent, canActivate: [RoleGuard],
    data: { role: 'ROL_PRODUCTOR' }},
  { path: 'checkout', component: CheckoutComponent },
  { path: 'checkout/success', component: CheckoutSuccessComponent, canActivate: [AuthGuard] },
  { path: 'checkout/pending', component: CheckoutComponent, canActivate: [AuthGuard] },
  { path: 'checkout/failure', component: CheckoutComponent, canActivate: [AuthGuard] },
  { path: 'establecimientos/:id', component: EstablishmentComponent},
  { path: 'login', component: LoginComponent },
  { path: 'login/nuevo', component: LoginFormComponent },
  { path: 'login/art-prod', component: LoginArtProdComponent },
  { path: 'login/art-prod/nuevo', component: LoginFormArtProdComponent},
  { path: 'perfil', component: UserProfileComponent},
  { path: 'perfil/editar', component: UserEditComponent},
  { path: 'terminos-condiciones', component: TerminosCondicionesComponent},
  { path: 'politica-privacidad', component: PoliticaPrivacidadComponent},
  { path: 'ayuda', component: AyudaComponent},
  { path: 'forgot-password', component: ForgotPasswordComponent},
  { path: 'reset-password', component: ResetPasswordComponent},
  { path: 'change-password', component: ChangePasswordComponent},
  { path: 'dashboard/productor', component: ProductorDashboardComponent},
  { path: 'dashboard/productor/graficos', component: GraficosProductorComponent},
  { path: 'dashboard/artista', component: ArtistaDashboardComponent},
  { path: 'historial', component: TicketsHistorialComponent, canActivate: [AuthGuard]},
  { path: 'perfil/notificaciones', component: NotificationPreferencesComponent},
  { path: '**', redirectTo: '/eventos' }

];
