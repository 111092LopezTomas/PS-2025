package org.example.escenalocal.auth.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.example.escenalocal.auth.service.CustomUserDetailsService;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

public class JwtAuthenticationFilter extends OncePerRequestFilter {

  private final JwtUtil jwtUtil;
  private final CustomUserDetailsService userDetailsService;

  public JwtAuthenticationFilter(JwtUtil jwtUtil, CustomUserDetailsService userDetailsService) {
    this.jwtUtil = jwtUtil;
    this.userDetailsService = userDetailsService;
  }

  // 👇👇👇 AÑADÍ ESTO
  @Override
  protected boolean shouldNotFilter(HttpServletRequest request) {
    String path = request.getServletPath();
    // ignorá todas las públicas
    return path.startsWith("/auth")
      || path.startsWith("/swagger-ui")
      || path.startsWith("/v3/api-docs")
      || path.startsWith("/h2-console")
      || path.startsWith("/swagger-resources")
      || path.startsWith("/webjars")
      || path.startsWith("/generos")      // <--- lo que te está pegando Angular
      || path.startsWith("/api/generos"); // por si después lo pasás a /api
  }
  // 👆👆👆

  @Override
  protected void doFilterInternal(HttpServletRequest request,
                                  HttpServletResponse response,
                                  FilterChain filterChain)
    throws ServletException, IOException {

    String path = request.getServletPath();
    System.out.println("Interceptando: " + path);

    String header = request.getHeader("Authorization");
    System.out.println("Header: " + header);

    if (StringUtils.hasText(header) && header.startsWith("Bearer ")) {
      String token = header.substring(7);
      try {
        if (jwtUtil.validateToken(token)) {
          String username = jwtUtil.extractUsername(token);
          UserDetails userDetails = userDetailsService.loadUserByUsername(username);

          UsernamePasswordAuthenticationToken authentication =
            new UsernamePasswordAuthenticationToken(
              userDetails,
              null,
              userDetails.getAuthorities()
            );
          SecurityContextHolder.getContext().setAuthentication(authentication);
        }
      } catch (Exception e) {
        SecurityContextHolder.clearContext();
      }
    }

    filterChain.doFilter(request, response);
  }
}
