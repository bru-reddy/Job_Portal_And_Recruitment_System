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
 private static final String COMPANY_CODE="1029";
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

  Role role=Role.CANDIDATE;
  if(r.role()!=null&&!r.role().isBlank()){
   try{role=Role.valueOf(r.role().trim().toUpperCase(Locale.ROOT));}catch(IllegalArgumentException ignored){}
  }
  if(role==Role.RECRUITER){
   if(!COMPANY_CODE.equals(r.companyCode())) return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Map.of("message","Invalid company authentication code"));
   if(r.companyName()==null||r.companyName().trim().length()<2) return ResponseEntity.badRequest().body(Map.of("message","Company name is required"));
  }

  if(users.findByEmail(email).isPresent()) return ResponseEntity.status(HttpStatus.CONFLICT).body(Map.of("message","This email is already registered. Please sign in instead."));
  User u=new User();
  u.setName(name);u.setEmail(email);u.setPassword(encoder.encode(r.password()));u.setRole(role);
  if(role==Role.RECRUITER){
   u.setCompanyName(r.companyName().trim());
   u.setCompanyWebsite(trim(r.companyWebsite()));
   u.setCompanyIndustry(trim(r.companyIndustry()));
   u.setCompanyDescription(trim(r.companyDescription()));
  }
  try{
   User saved=users.save(u);
   return ResponseEntity.status(HttpStatus.CREATED).body(safe(saved));
  }catch(DataIntegrityViolationException ex){
   return ResponseEntity.status(HttpStatus.CONFLICT).body(Map.of("message","This email is already registered. Please sign in instead."));
  }
 }

 @PostMapping("/login")
 public ResponseEntity<?> login(@RequestBody LoginRequest r){
  String email=r.email()==null?null:r.email().trim().toLowerCase(Locale.ROOT);
  if(email==null||email.isBlank()||r.password()==null) return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("message","Invalid email or password"));
  var user=users.findByEmail(email).orElse(null);
  if(user==null||!encoder.matches(r.password(),user.getPassword())) return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("message","Invalid email or password"));
  if(user.getRole()==Role.RECRUITER && !COMPANY_CODE.equals(r.companyCode())) return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Map.of("message","Company authentication code is required to sign in as a company"));
  if(r.role()!=null&&!r.role().isBlank()&&!user.getRole().name().equalsIgnoreCase(r.role())) return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Map.of("message","This account is registered for a different role"));
  return ResponseEntity.ok(safe(user));
 }

 private Map<String,Object> safe(User u){
  return Map.of(
   "id",u.getId(),
   "name",u.getName(),
   "email",u.getEmail(),
   "role",u.getRole(),
   "phone",u.getPhone()==null?"":u.getPhone(),
   "companyName",u.getCompanyName()==null?"":u.getCompanyName(),
   "companyWebsite",u.getCompanyWebsite()==null?"":u.getCompanyWebsite(),
   "companyIndustry",u.getCompanyIndustry()==null?"":u.getCompanyIndustry(),
   "companyDescription",u.getCompanyDescription()==null?"":u.getCompanyDescription()
  );
 }
 private String trim(String value){return value==null?null:value.trim();}
}
