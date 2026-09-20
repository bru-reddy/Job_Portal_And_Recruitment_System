package com.jobsphere.controller;

import com.jobsphere.dto.ApplicationRequest;
import com.jobsphere.model.*;
import com.jobsphere.repository.*;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/applications")
public class ApplicationController {
 private final ApplicationRepository apps;
 private final JobRepository jobs;
 private final UserRepository users;

 public ApplicationController(ApplicationRepository a,JobRepository j,UserRepository u){apps=a;jobs=j;users=u;}

 @PostMapping
 public ResponseEntity<?> apply(@RequestBody ApplicationRequest r){
  if(r.jobId()==null) return bad("A valid job is required");
  var job=jobs.findById(r.jobId()).orElse(null);
  if(job==null) return bad("The selected job could not be found");

  var candidate=r.candidateId()==null?null:users.findById(r.candidateId()).orElse(null);
  if((candidate==null||candidate.getRole()!=Role.CANDIDATE)&&r.candidateEmail()!=null&&!r.candidateEmail().isBlank())
   candidate=users.findByEmail(r.candidateEmail().trim().toLowerCase()).orElse(null);
  if(candidate==null||candidate.getRole()!=Role.CANDIDATE)
   return bad("Valid candidate account required. Please sign in again.");

  if(apps.existsByJobIdAndCandidateId(job.getId(),candidate.getId()))
   return bad("You have already applied for this role");

  if(blank(r.applicantName())||blank(r.applicantEmail())||blank(r.education())||blank(r.skills()))
   return bad("Please complete all required application fields");

  Application a=new Application();
  a.setJob(job); a.setCandidate(candidate);
  a.setApplicantName(r.applicantName().trim());
  a.setApplicantEmail(r.applicantEmail().trim());
  a.setApplicantPhone(trim(r.applicantPhone()));
  a.setEducation(trim(r.education()));
  a.setExperience(trim(r.experience()));
  a.setSkills(trim(r.skills()));
  a.setCoverLetter(trim(r.coverLetter()));
  Application saved=apps.save(a);
  return ResponseEntity.status(HttpStatus.CREATED).body(Map.of(
   "id",saved.getId(),"message","Application submitted successfully","status",saved.getStatus().name()
  ));
 }

 @PatchMapping("/{id}/status")
 public ResponseEntity<?> updateStatus(@PathVariable Long id,@RequestParam ApplicationStatus status,@RequestParam Long recruiterId){
  var recruiter=users.findById(recruiterId).orElse(null);
  if(recruiter==null||recruiter.getRole()!=Role.RECRUITER) return bad("Valid recruiter account required");

  var application=apps.findById(id).orElse(null);
  if(application==null) return ResponseEntity.notFound().build();

  var job=application.getJob();
  if(job.getRecruiter()==null||!recruiter.getId().equals(job.getRecruiter().getId()))
   return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Map.of("message","You can only update applicants for your own jobs"));

  if(status!=ApplicationStatus.APPROVED_FOR_REFERRAL&&status!=ApplicationStatus.NOT_SELECTED&&status!=ApplicationStatus.REVIEWING)
   return bad("Unsupported application status");

  application.setStatus(status);
  return ResponseEntity.ok(apps.save(application));
 }

 @GetMapping("/candidate/{id}")
 public List<Application> candidate(@PathVariable Long id){return apps.findByCandidateIdOrderByAppliedAtDesc(id);}

 @GetMapping("/recruiter/{id}")
 public List<Application> recruiter(@PathVariable Long id){
  var recruiter=users.findById(id).orElse(null);
  if(recruiter==null||recruiter.getRole()!=Role.RECRUITER) return List.of();
  return apps.findByJobRecruiterIdOrderByAppliedAtDesc(id);
 }

 @GetMapping("/company/{id}")
 public List<Application> company(@PathVariable Long id){
  var company=users.findById(id).orElse(null);
  if(company==null||company.getRole()!=Role.COMPANY) return List.of();
  return apps.findByJobCompanyIgnoreCaseOrderByAppliedAtDesc(company.getCompanyName());
 }

 private boolean blank(String s){return s==null||s.trim().isBlank();}
 private String trim(String s){return s==null?"":s.trim();}
 private ResponseEntity<?> bad(String m){return ResponseEntity.badRequest().body(Map.of("message",m));}
}
