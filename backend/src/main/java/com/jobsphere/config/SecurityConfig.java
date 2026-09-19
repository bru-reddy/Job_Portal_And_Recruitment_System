package com.jobsphere.config;
import org.springframework.context.annotation.*;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
@Configuration
public class SecurityConfig {
 @Bean PasswordEncoder passwordEncoder(){return new BCryptPasswordEncoder();}
 @Bean SecurityFilterChain security(HttpSecurity http)throws Exception{
  http.csrf(c->c.disable()).cors(c->c.configurationSource(req->{
   var x=new org.springframework.web.cors.CorsConfiguration();
   x.setAllowedOrigins(java.util.List.of("http://localhost:5173","https://job-portal-frontend.onrender.com"));
   x.setAllowedMethods(java.util.List.of("GET","POST","PUT","PATCH","DELETE","OPTIONS")); x.setAllowedHeaders(java.util.List.of("*")); return x;
  }));
  http.authorizeHttpRequests(a->a.anyRequest().permitAll()); return http.build();
 }
}