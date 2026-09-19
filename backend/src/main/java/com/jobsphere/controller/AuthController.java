package com.jobsphere.controller;
import com.jobsphere.dto.*;
import com.jobsphere.model.*;
import com.jobsphere.repository.UserRepository;
import org.springframework.http.*;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import java.util.Map;
@RestController @RequestMapping("/api/auth")
public class AuthController {
 private final UserRepository users; private final PasswordEncoder encoder;
 public AuthController(UserRepository u,PasswordEncoder e){users=u;encoder=e;}
 @PostMapping("/register") public ResponseEntity<?> register(@RequestBody RegisterRequest r){
  if(users.findByEmail(r.email()).isPresent()) return ResponseEntity.badRequest().body(Map.of("message","Email already registered"));
  User u=new User();u.setName(r.name());u.setEmail(r.email());u.setPassword(encoder.encode(r.password()));u.setRole(r.role()==null?Role.CANDIDATE:r.role());return ResponseEntity.ok(users.save(u));
 }
 @PostMapping("/login") public ResponseEntity<?> login(@RequestBody LoginRequest r){
  return users.findByEmail(r.email()).filter(u->encoder.matches(r.password(),u.getPassword()))
   .<ResponseEntity<?>>map(u->ResponseEntity.ok(Map.of("id",u.getId(),"name",u.getName(),"email",u.getEmail(),"role",u.getRole())))
   .orElseGet(()->ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("message","Invalid email or password")));
 }
}
