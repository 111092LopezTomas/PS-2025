import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { AuthService } from '../../services/auth.service';
import { NotificationPreferencesService } from '../../services/notification-preferences.service';

@Component({
  standalone: true,
  selector: 'app-notification-preferences',
  imports: [CommonModule, FormsModule],
  templateUrl: './notification-preferences.component.html',
  styleUrls: ['./notification-preferences.component.css']
})
export class NotificationPreferencesComponent implements OnInit {

  preferences: any;
  role: string | null = null;
  saving = false;

  constructor(
    private auth: AuthService,
    private service: NotificationPreferencesService
  ) {}

  ngOnInit(): void {
    this.role = this.auth.getUserRoleFromToken();
    this.load();
  }

  load(): void {
    this.service.getMine().subscribe(p => this.preferences = p);
  }

  save(): void {
    this.saving = true;
    this.service.updateMine(this.preferences).subscribe({
      next: () => this.saving = false,
      error: () => this.saving = false
    });
  }
}
