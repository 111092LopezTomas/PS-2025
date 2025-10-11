import { Routes } from '@angular/router';
import { EventListComponent } from './components/event-list/event-list.component';
import { EventFormComponent } from './components/event-form/event-form.component';
import { EventViewComponent } from './components/event-view/event-view.component';
import { CheckoutComponent } from './components/checkout/checkout.component';

export const routes: Routes = [
  
  {path: '', redirectTo: '/eventos', pathMatch: 'full' },
  { path: 'eventos', component: EventListComponent },
  { path: 'eventos/nuevo', component: EventFormComponent},
  { path: 'evento/:id', component: EventViewComponent },
  { path: 'checkout', component: CheckoutComponent },
  { path: 'checkout/success', component: CheckoutComponent },
  { path: 'checkout/pending', component: CheckoutComponent },
  { path: 'checkout/failure', component: CheckoutComponent },
  { path: '**', redirectTo: '/eventos' }
];
