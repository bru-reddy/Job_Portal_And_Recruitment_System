package com.jobsphere.controller;
import com.jobsphere.model.*;
import com.jobsphere.repository.*;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import java.util.List;
@RestController @RequestMapping("/api/applications")
public class ApplicationController {
 private final ApplicationRepository apps; private final JobRepository jobs; private final UserRepository users;
 public ApplicationController(ApplicationRepository a,JobRepository j,UserRepository u){apps=a;jobs=j;users=u;}
 @PostMapping public ResponseEntity<?> apply(@RequestParam Long jobId,@RequestParam Long candidateId){
  if(apps.existsByJobIdAndCandidateId(jobId,candidateId))return ResponseEntity.badRequest().body("Already applied");
  var job=jobs.findById(jobId).orElse(null);var user=users.findById(candidateId).orElse(null);
  if(job==null||user==null||user.getRole()!=Role.CANDIDATE)return ResponseEntity.badRequest().body("Valid job and candidate required");
  Application a=new Application();a.setJob(job);a.setCandidate(user);return ResponseEntity.status(HttpStatus.CREATED).body(apps.save(a));
 }
 @GetMapping("/candidate/{id}") public List<Application> candidate(@PathVariable Long id){return apps.findByCandidateIdOrderByAppliedAtDesc(id);}
 @GetMapping("/recruiter/{id}") public List<Application> recruiter(@PathVariable Long id){return apps.findByJobRecruiterIdOrderByAppliedAtDesc(id);}
 @PatchMapping("/{id}/status") public ResponseEntity<?> status(@PathVariable Long id,@RequestParam ApplicationStatus status){return apps.findById(id).map(a->{a.setStatus(status);return ResponseEntity.ok(apps.save(a));}).orElse(ResponseEntity.notFound().build());}
}
