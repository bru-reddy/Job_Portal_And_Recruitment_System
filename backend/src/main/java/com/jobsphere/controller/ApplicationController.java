package com.jobsphere.controller;

import com.jobsphere.model.*;
import com.jobsphere.repository.*;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/applications")
public class ApplicationController {
 private final ApplicationRepository apps;
 private final JobRepository jobs;
 private final UserRepository users;

 public ApplicationController(ApplicationRepository a,JobRepository j,UserRepository u){apps=a;jobs=j;users=u;}

 @PostMapping(consumes=MediaType.MULTIPART_FORM_DATA_VALUE)
 public ResponseEntity<?> apply(
   @RequestParam Long jobId,
   @RequestParam(required=false) Long candidateId,
   @RequestParam(required=false,defaultValue="") String candidateEmail,
   @RequestParam String applicantName,
   @RequestParam String applicantEmail,
   @RequestParam(required=false,defaultValue="") String applicantPhone,
   @RequestParam(required=false,defaultValue="") String education,
   @RequestParam(required=false,defaultValue="") String experience,
   @RequestParam(required=false,defaultValue="") String skills,
   @RequestParam(required=false,defaultValue="") String coverLetter,
   @RequestPart("resume") MultipartFile resume
 ) throws IOException {
  var job=jobs.findById(jobId).orElse(null);
  if(job==null) return ResponseEntity.badRequest().body(java.util.Map.of("message","The selected job could not be found. Please return to Find Jobs and select the role again."));

  // Prefer the stored user id, but fall back to the authenticated candidate email.
  // This also recovers gracefully when an older browser session has a user object
  // created before the current login response included the database id.
  var user=candidateId==null?null:users.findById(candidateId).orElse(null);
  if((user==null||user.getRole()!=Role.CANDIDATE)&&candidateEmail!=null&&!candidateEmail.isBlank()){
   user=users.findByEmail(candidateEmail.trim().toLowerCase()).orElse(null);
  }
  if(user==null||user.getRole()!=Role.CANDIDATE) return ResponseEntity.badRequest().body(java.util.Map.of("message","Valid candidate account required. Please sign out and sign in again before applying."));
  if(apps.existsByJobIdAndCandidateId(jobId,user.getId())) return ResponseEntity.badRequest().body(java.util.Map.of("message","You have already applied for this role"));

  if(resume==null||resume.isEmpty()) return ResponseEntity.badRequest().body(java.util.Map.of("message","Please upload your resume as a PDF"));
  String contentType=resume.getContentType()==null?"":resume.getContentType().toLowerCase();
  String filename=resume.getOriginalFilename()==null?"":resume.getOriginalFilename().toLowerCase();
  if(!"application/pdf".equals(contentType)&&!filename.endsWith(".pdf")) return ResponseEntity.badRequest().body(java.util.Map.of("message","Only PDF resumes are accepted"));
  if(resume.getSize()>5_000_000) return ResponseEntity.badRequest().body(java.util.Map.of("message","Resume must be 5 MB or smaller"));

  Application a=new Application();
  a.setJob(job);a.setCandidate(user);
  a.setApplicantName(applicantName.trim());a.setApplicantEmail(applicantEmail.trim());
  a.setApplicantPhone(applicantPhone.trim());a.setEducation(education.trim());a.setExperience(experience.trim());
  a.setSkills(skills.trim());a.setCoverLetter(coverLetter.trim());
  a.setResumeFileName(resume.getOriginalFilename());
  a.setResumeContentType("application/pdf");
  a.setResumeData(resume.getBytes());
  Application saved=apps.save(a);
  // Return a small response DTO instead of serializing the full JPA graph.
  // This avoids lazy-proxy/relationship serialization errors after the transaction closes.
  return ResponseEntity.status(HttpStatus.CREATED).body(Map.of(
    "id",saved.getId(),
    "message","Application submitted successfully",
    "status",saved.getStatus().name()
  ));
 }

 @GetMapping("/candidate/{id}")
 public List<Application> candidate(@PathVariable Long id){return apps.findByCandidateIdOrderByAppliedAtDesc(id);}

 @GetMapping("/recruiter/{id}")
 public List<Application> recruiter(@PathVariable Long id){
  var company=users.findById(id).orElse(null);
  if(company==null||company.getRole()!=Role.RECRUITER) return List.of();
  return apps.findByJobRecruiterIdOrJobCompanyIgnoreCaseOrderByAppliedAtDesc(id,company.getCompanyName());
 }

 @GetMapping("/{id}/resume")
 public ResponseEntity<byte[]> resume(@PathVariable Long id){
  return apps.findById(id).map(a->ResponseEntity.ok()
    .contentType(MediaType.APPLICATION_PDF)
    .header(HttpHeaders.CONTENT_DISPOSITION,"attachment; filename=\"" +(a.getResumeFileName()==null?"resume.pdf":a.getResumeFileName().replace("\"",""))+"\"")
    .body(a.getResumeData())
  ).orElse(ResponseEntity.notFound().build());
 }

 @PatchMapping("/{id}/status")
 public ResponseEntity<?> status(@PathVariable Long id,@RequestParam ApplicationStatus status){
  return apps.findById(id).map(a->{a.setStatus(status);return ResponseEntity.ok(apps.save(a));}).orElse(ResponseEntity.notFound().build());
 }
}
