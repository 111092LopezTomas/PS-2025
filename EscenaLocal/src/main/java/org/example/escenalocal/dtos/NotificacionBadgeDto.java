package org.example.escenalocal.dtos;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NotificacionBadgeDto {
  private long unreadCount;
}
