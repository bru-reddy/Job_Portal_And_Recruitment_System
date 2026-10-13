package com.jobsphere.controller;
import com.jobsphere.dto.JobRequest;
import com.jobsphere.model.*;
import com.jobsphere.repository.*;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import jakarta.servlet.http.HttpSession;
import java.util.Map;
@RestController @RequestMapping("/api/jobs")
public class JobController {
 private final JobRepository jobs; private final UserRepository users;
 public JobController(JobRepository j,UserRepository u){jobs=j;users=u;}

 @GetMapping
 public List<Job> list(@RequestParam(required=false) String search){
  return search==null||search.isBlank()?jobs.findByActiveTrueOrderByCreatedAtDesc():jobs.findByActiveTrueAndTitleContainingIgnoreCaseOrderByCreatedAtDesc(search);
 }
 @GetMapping("/recruiter/{recruiterId}")
 public List<Job> recruiterJobs(@PathVariable Long recruiterId){return jobs.findByRecruiterIdOrderByCreatedAtDesc(recruiterId);}

 @GetMapping("/{id}")
 public ResponseEntity<Job> get(@PathVariable Long id){return jobs.findById(id).map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());}

 @PostMapping
 public ResponseEntity<?> create(@RequestBody JobRequest r, HttpSession session){
  Long signedInUserId = (Long) session.getAttribute("jobsphereUserId");
  if (signedInUserId == null) return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("message","Please sign in as a recruiter."));
  if (r.recruiterId() == null || !signedInUserId.equals(r.recruiterId())) return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Map.of("message","You cannot post jobs for another account."));
  var recruiter=users.findById(signedInUserId).orElse(null);
  if(recruiter==null||recruiter.getRole()!=Role.RECRUITER)return ResponseEntity.badRequest().body("Valid recruiter required");
  if(r.title()==null||r.title().isBlank()||r.location()==null||r.location().isBlank()||r.company()==null||r.company().isBlank())
   return ResponseEntity.badRequest().body("Title, company and location are required");
  Job j=new Job();
  j.setTitle(r.title().trim());j.setCompany(r.company().trim());j.setLocation(r.location().trim());j.setType(r.type());j.setLevel(r.level());
  j.setSalary(r.salary());j.setDescription(r.description());j.setSkills(r.skills());j.setEligibilityCriteria(r.eligibilityCriteria());
  j.setSourceName(r.sourceName());j.setSourceUrl(r.sourceUrl());
  j.setRecruiter(recruiter);
  return ResponseEntity.status(HttpStatus.CREATED).body(jobs.save(j));
 }

 @DeleteMapping("/{id}")
 public ResponseEntity<?> close(@PathVariable Long id, HttpSession session){
  Long signedInUserId = (Long) session.getAttribute("jobsphereUserId");
  if (signedInUserId == null) return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("message","Please sign in to continue."));
  return jobs.findById(id).map(j->{
   if (j.getRecruiter() == null || !signedInUserId.equals(j.getRecruiter().getId())) return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
   j.setActive(false);jobs.save(j);return ResponseEntity.ok().build();
  }).orElse(ResponseEntity.notFound().build());
 }
}
