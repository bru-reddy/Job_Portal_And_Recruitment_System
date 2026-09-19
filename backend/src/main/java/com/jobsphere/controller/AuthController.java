package com.jobsphere.controller;

import com.jobsphere.dto.*;
import com.jobsphere.model.*;
import com.jobsphere.repository.UserRepository;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.*;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import java.util.Locale;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
 private final UserRepository users;
 private final PasswordEncoder encoder;

 public AuthController(UserRepository u, PasswordEncoder e){users=u;encoder=e;}

 @PostMapping("/register")
 public ResponseEntity<?> register(@RequestBody RegisterRequest r){
  String name=r.name()==null?null:r.name().trim();
  String email=r.email()==null?null:r.email().trim().toLowerCase(Locale.ROOT);
  if(name==null||name.length()<2) return ResponseEntity.badRequest().body(Map.of("message","Please enter your full name"));
  if(email==null||email.isBlank()) return ResponseEntity.badRequest().body(Map.of("message","Email address is required"));
  if(r.password()==null||r.password().length()<6) return ResponseEntity.badRequest().body(Map.of("message","Password must contain at least 6 characters"));
  if(users.findByEmail(email).isPresent()) return ResponseEntity.status(HttpStatus.CONFLICT).body(Map.of("message","This email is already registered. Please sign in instead."));
  Role role=Role.CANDIDATE;
  if(r.role()!=null&&!r.role().isBlank()){
   try{role=Role.valueOf(r.role().trim().toUpperCase(Locale.ROOT));}catch(IllegalArgumentException ignored){}
  }
  User u=new User();
  u.setName(name);u.setEmail(email);u.setPassword(encoder.encode(r.password()));u.setRole(role);
  try{return ResponseEntity.status(HttpStatus.CREATED).body(users.save(u));}
  catch(DataIntegrityViolationException ex){return ResponseEntity.status(HttpStatus.CONFLICT).body(Map.of("message","This email is already registered. Please sign in instead."));}
 }

 @PostMapping("/login")
 public ResponseEntity<?> login(@RequestBody LoginRequest r){
  String email=r.email()==null?null:r.email().trim().toLowerCase(Locale.ROOT);
  return users.findByEmail(email).filter(u->r.password()!=null&&encoder.matches(r.password(),u.getPassword()))
   .<ResponseEntity<?>>map(u->ResponseEntity.ok(Map.of("id",u.getId(),"name",u.getName(),"email",u.getEmail(),"role",u.getRole())))
   .orElseGet(()->ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("message","Invalid email or password")));
 }
}
