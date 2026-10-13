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
   x.setAllowCredentials(true);
   x.setMaxAge(3600L);
   return x;
  }));
  http.sessionManagement(s -> s
   .sessionCreationPolicy(org.springframework.security.config.http.SessionCreationPolicy.IF_REQUIRED)
   .sessionFixation(f -> f.migrateSession())
  );
  http.formLogin(c -> c.disable()).httpBasic(c -> c.disable());
  // SameSite=None is required for JSESSIONID when the web app and API are on
  // separate Render subdomains. Secure cookies are expected on hosted HTTPS.
  http.addFilterAfter(new org.springframework.web.filter.OncePerRequestFilter() {
   @Override
   protected void doFilterInternal(jakarta.servlet.http.HttpServletRequest request,
                                   jakarta.servlet.http.HttpServletResponse response,
                                   jakarta.servlet.FilterChain chain)
                                   throws jakarta.servlet.ServletException, java.io.IOException {
    chain.doFilter(request, response);
    for (String header : response.getHeaders("Set-Cookie")) {
     if (header.startsWith("JSESSIONID=") && !header.toLowerCase().contains("samesite")) {
      response.setHeader("Set-Cookie", header + "; SameSite=None; Secure; HttpOnly");
     }
    }
   }
  }, org.springframework.security.web.context.SecurityContextHolderFilter.class);
  http.authorizeHttpRequests(a -> a
   .requestMatchers("/api/auth/**", "/api/health", "/api/jobs", "/api/jobs/*").permitAll()
   .requestMatchers(org.springframework.http.HttpMethod.GET, "/api/jobs/recruiter/**").permitAll()
   .anyRequest().permitAll()
  );
  return http.build();
 }
}