package com.jobsphere.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import java.util.ArrayList;
import java.util.List;

@Configuration
public class SecurityConfig {
 @Bean PasswordEncoder passwordEncoder() { return new BCryptPasswordEncoder(); }

 @Bean
 SecurityFilterChain security(HttpSecurity http) throws Exception {
  http.csrf(c -> c.disable()).cors(c -> c.configurationSource(req -> {
   var x = new org.springframework.web.cors.CorsConfiguration();
   List<String> patterns = new ArrayList<>(List.of(
    "http://localhost:5173", "http://localhost:4173",
    "https://*.onrender.com", "https://*.vercel.app"
   ));
   String frontend = System.getenv("FRONTEND_URL");
   if (frontend != null && !frontend.isBlank()) patterns.add(frontend.trim().replaceAll("/$", ""));
   x.setAllowedOriginPatterns(patterns);
   x.setAllowedMethods(List.of("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));
   x.setAllowedHeaders(List.of("*"));
   x.setMaxAge(3600L);
   return x;
  }));
  // The login endpoint creates a server-side session. All reads remain public
  // for job discovery; changing data and submitting applications require auth.
  http.authorizeHttpRequests(a -> a
   .requestMatchers("/api/auth/**", "/api/health", "/api/jobs", "/api/jobs/*").permitAll()
   .requestMatchers(org.springframework.http.HttpMethod.GET, "/api/jobs/recruiter/**").permitAll()
   .requestMatchers(org.springframework.http.HttpMethod.GET, "/api/applications/**").authenticated()
   .requestMatchers(org.springframework.http.HttpMethod.POST, "/api/applications").authenticated()
   .requestMatchers(org.springframework.http.HttpMethod.PATCH, "/api/applications/**").authenticated()
   .requestMatchers(org.springframework.http.HttpMethod.POST, "/api/jobs").authenticated()
   .anyRequest().permitAll()
  );
  return http.build();
 }
}