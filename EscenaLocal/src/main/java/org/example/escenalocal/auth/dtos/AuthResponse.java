package org.example.escenalocal.auth.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AuthResponse {
  private String token;
  private Long userId;
  public AuthResponse(String token) { this.token = token; }
  public String getToken() { return token; }
  public void setToken(String token) { this.token = token; }
}
