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
  // Frontend/API may be on separate Render subdomains. SameSite=None is needed
  // for cross-site XHR cookies, and Secure is appropriate on HTTPS deployments.
  http.addFilterAfter(new org.springframework.web.filter.OncePerRequestFilter() {
   @Override
   protected void doFilterInternal(jakarta.servlet.http.HttpServletRequest request,
                                   jakarta.servlet.http.HttpServletResponse response,
                                   jakarta.servlet.FilterChain filterChain)
                                   throws jakarta.servlet.ServletException, java.io.IOException {
    response.setHeader("Access-Control-Expose-Headers", "Set-Cookie");
    filterChain.doFilter(request, response);
   }
  }, org.springframework.security.web.context.SecurityContextHolderFilter.class);
  http.formLogin(c -> c.disable()).httpBasic(c -> c.disable());
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