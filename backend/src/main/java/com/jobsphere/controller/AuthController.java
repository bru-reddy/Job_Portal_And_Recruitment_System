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

 public AuthController(UserRepository u,PasswordEncoder e){users=u;encoder=e;}

 @PostMapping("/register")
 public ResponseEntity<?> register(@RequestBody RegisterRequest r){
  String name=trim(r.name()), email=lower(r.email());
  if(name.length()<2) return bad("Please enter your full name");
  if(email.isBlank()) return bad("Email address is required");
  if(r.password()==null||r.password().length()<6) return bad("Password must contain at least 6 characters");
  Role role=parseRole(r.role());
  if(role!=Role.CANDIDATE&&role!=Role.RECRUITER) return bad("Only Candidate and Recruiter accounts are supported");
  if(users.findByEmail(email).isPresent()) return ResponseEntity.status(HttpStatus.CONFLICT).body(Map.of("message","This email is already registered. Please sign in instead."));
  User u=new User();
  u.setName(name);u.setEmail(email);u.setPassword(encoder.encode(r.password()));u.setRole(role);
  try{return ResponseEntity.status(HttpStatus.CREATED).body(safe(users.save(u)));}
  catch(DataIntegrityViolationException ex){return ResponseEntity.status(HttpStatus.CONFLICT).body(Map.of("message","This email is already registered. Please sign in instead."));}
 }

 @PostMapping("/login")
 public ResponseEntity<?> login(@RequestBody LoginRequest r){
  String email=lower(r.email());
  if(email.isBlank()||r.password()==null) return unauthorized();
  var user=users.findByEmail(email).orElse(null);
  if(user==null||!encoder.matches(r.password(),user.getPassword())) return unauthorized();
  if(user.getRole()!=Role.CANDIDATE&&user.getRole()!=Role.RECRUITER) return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Map.of("message","This account type is no longer supported"));
  if(r.role()!=null&&!r.role().isBlank()&&!user.getRole().name().equalsIgnoreCase(r.role()))
   return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Map.of("message","This account is registered for a different role"));
  return ResponseEntity.ok(safe(user));
 }

 private Role parseRole(String r){
  if(r==null||r.isBlank()) return Role.CANDIDATE;
  try{return Role.valueOf(r.trim().toUpperCase(Locale.ROOT));}catch(Exception e){return Role.CANDIDATE;}
 }
 private Map<String,Object> safe(User u){return Map.of(
  "id",u.getId(),"name",u.getName(),"email",u.getEmail(),"role",u.getRole(),
  "phone",u.getPhone()==null?"":u.getPhone(),
  "companyName",u.getCompanyName()==null?"":u.getCompanyName(),
  "companyWebsite",u.getCompanyWebsite()==null?"":u.getCompanyWebsite(),
  "companyIndustry",u.getCompanyIndustry()==null?"":u.getCompanyIndustry(),
  "companyDescription",u.getCompanyDescription()==null?"":u.getCompanyDescription()
 );}
 private ResponseEntity<?> bad(String m){return ResponseEntity.badRequest().body(Map.of("message",m));}
 private ResponseEntity<?> unauthorized(){return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("message","Invalid email or password"));}
 private String trim(String s){return s==null?"":s.trim();}
 private String lower(String s){return trim(s).toLowerCase(Locale.ROOT);}
}
