package org.example.escenalocal.auth.security;

import org.example.escenalocal.auth.security.JwtAuthenticationFilter;
import org.example.escenalocal.auth.service.CustomUserDetailsService;
import org.example.escenalocal.auth.security.JwtUtil;
import org.springframework.context.annotation.*;
import org.springframework.security.authentication.*;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

@Configuration
@EnableMethodSecurity
public class SecurityConfig {

  @Autowired
  private JwtUtil jwtUtil;

  @Autowired
  private CustomUserDetailsService userDetailsService;

  @Bean
  public JwtAuthenticationFilter jwtAuthenticationFilter() {
    return new JwtAuthenticationFilter(jwtUtil, userDetailsService);
  }

  @Bean
  public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
    http
      .csrf(csrf -> csrf.disable())
      .cors(cors -> cors.configurationSource(corsConfigurationSource()))
      .sessionManagement(session -> session
        .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
      )
      .authorizeHttpRequests(auth -> auth
        .requestMatchers("/auth/**").permitAll()

        // Swagger / H2 públicos si los usás
        .requestMatchers(
          "/h2-console/**",
          "/swagger-ui/**",
          "/v3/api-docs/**",
          "/v3/api-docs.yaml",
          "/swagger-resources/**",
          "/webjars/**"
        ).permitAll()

        .requestMatchers(
          "/artistas/**",
          "/provincias/**",
          "/eventos/**",
          "/generos/**",
          "/establecimientos/**",
          "/clasificaciones/**",
          "/entradas/**",
          "/productores/**",
          "/payments/create-preference/**",
          "/payments/webhook",
          "/api/notificaciones/**",
          "/dashboard/**",
          "/tickets/**"
        ).permitAll()

        // Cualquier otra cosa requiere estar autenticado
        .anyRequest().authenticated()
      )
      .addFilterBefore(jwtAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class)
      .headers(headers -> headers.frameOptions(frame -> frame.sameOrigin())); // H2

    return http.build();
  }

  @Bean
  public CorsConfigurationSource corsConfigurationSource() {
    CorsConfiguration configuration = new CorsConfiguration();
    configuration.addAllowedOriginPattern("*");
    configuration.addAllowedMethod("*");
    configuration.addAllowedHeader("*");
    configuration.setAllowCredentials(true);

    UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
    source.registerCorsConfiguration("/**", configuration);
    return source;
  }

  @Bean
  public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
    return config.getAuthenticationManager();
  }

  @Bean
  public PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder();
  }
}
