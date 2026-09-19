package com.jobsphere.controller;

import com.jobsphere.model.*;
import com.jobsphere.repository.*;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.util.List;

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
   @RequestParam Long candidateId,
   @RequestParam String applicantName,
   @RequestParam String applicantEmail,
   @RequestParam(required=false,defaultValue="") String applicantPhone,
   @RequestParam(required=false,defaultValue="") String education,
   @RequestParam(required=false,defaultValue="") String experience,
   @RequestParam(required=false,defaultValue="") String skills,
   @RequestParam(required=false,defaultValue="") String coverLetter,
   @RequestPart("resume") MultipartFile resume
 ) throws IOException {
  if(apps.existsByJobIdAndCandidateId(jobId,candidateId)) return ResponseEntity.badRequest().body(java.util.Map.of("message","You have already applied for this role"));
  var job=jobs.findById(jobId).orElse(null);
  var user=users.findById(candidateId).orElse(null);
  if(job==null||user==null||user.getRole()!=Role.CANDIDATE) return ResponseEntity.badRequest().body(java.util.Map.of("message","Valid job and candidate required"));
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
  return ResponseEntity.status(HttpStatus.CREATED).body(apps.save(a));
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
    .header(HttpHeaders.CONTENT_DISPOSITION,"attachment; filename=\""+(a.getResumeFileName()==null?"resume.pdf":a.getResumeFileName().replace(""",""))+"\"")
    .body(a.getResumeData())
  ).orElse(ResponseEntity.notFound().build());
 }

 @PatchMapping("/{id}/status")
 public ResponseEntity<?> status(@PathVariable Long id,@RequestParam ApplicationStatus status){
  return apps.findById(id).map(a->{a.setStatus(status);return ResponseEntity.ok(apps.save(a));}).orElse(ResponseEntity.notFound().build());
 }
}
