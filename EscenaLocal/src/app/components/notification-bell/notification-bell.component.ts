import { Component, HostListener } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';
import { Notificacion, NotificacionService } from '../../services/notificacion.service';
import { AuthService } from '../../services/auth.service';

@Component({
  selector: 'app-notification-bell',
  standalone: true,
  imports: [CommonModule, RouterModule],
  templateUrl: './notification-bell.component.html',
  styleUrls: ['./notification-bell.component.css'],
})
export class NotificationBellComponent {
  open = false;
  loading = false;

  unreadCount = 0;
  notifications: Notificacion[] = [];

  page = 0;
  size = 10;
  hasMore = true;

  constructor(
    private notificationsService: NotificacionService,
    private auth: AuthService
  ) {}

  ngOnInit() {
    // Solo tiene sentido si está logueado
    if (!this.isLoggedIn()) return;

    this.refreshBadge();

    // opcional: refrescar cada X segundos
    // setInterval(() => this.refreshBadge(), 15000);
  }

  isLoggedIn(): boolean {
    // ajustá a tu AuthService real
    return !!this.auth.isLoggedIn?.() || !!this.auth.getToken?.();
  }

  refreshBadge() {
    this.notificationsService.getBadge().subscribe({
      next: (r) => (this.unreadCount = r.unreadCount),
      error: () => (this.unreadCount = 0),
    });
  }

  toggleDropdown() {
    this.open = !this.open;
  if (this.open) {
    this.loadFirstPage();
    
  }
}

  loadFirstPage() {
    this.page = 0;
    this.hasMore = true;
    this.notifications = [];
    this.loadMore();
  }

  loadMore() {
    if (this.loading || !this.hasMore) return;

    this.loading = true;
    this.notificationsService.getMyNotifications(this.page, this.size).subscribe({
      next: (pageResp) => {
        const content: Notificacion[] = pageResp.content ?? [];
        this.notifications = [...this.notifications, ...content];

        this.hasMore = !(pageResp.last ?? true);
        this.page = this.page + 1;

        this.loading = false;
      },
      error: () => {
        this.loading = false;
      },
    });
  }

  onClickNotification(n: Notificacion) {
    // si querés llevar a algún lado, acá.
    // por ahora solo marca como leída si estaba no leída
    if (!n.leido) {
      this.notificationsService.markAsRead(n.id).subscribe({
        next: () => {
          n.leido = true;
          if (this.unreadCount > 0) this.unreadCount--;
        },
        error: () => {},
      });
    }

    // ejemplo: cerrar dropdown
    // this.open = false;
  }

  markAll() {
    this.notificationsService.markAllAsRead().subscribe({
      next: () => {
        this.notifications.forEach((x) => (x.leido = true));
        this.unreadCount = 0;
      },
      error: () => {},
    });
  }

  // cerrar dropdown si clickea afuera
  @HostListener('document:click', ['$event'])
  onDocumentClick(event: MouseEvent) {
    const target = event.target as HTMLElement;
    // ajustá el selector según tu HTML
    if (!target.closest('.notif-bell')) {
      this.open = false;
    }
  }
}
