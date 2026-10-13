package com.jobsphere.controller;

import com.jobsphere.dto.ApplicationRequest;
import com.jobsphere.model.*;
import com.jobsphere.repository.*;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;
import org.springframework.transaction.annotation.Transactional;
import jakarta.servlet.http.HttpSession;

@RestController
@RequestMapping("/api/applications")
public class ApplicationController {
 private final ApplicationRepository apps;
 private final JobRepository jobs;
 private final UserRepository users;

 public ApplicationController(ApplicationRepository a, JobRepository j, UserRepository u) {
  apps = a; jobs = j; users = u;
 }

 @PostMapping
 public ResponseEntity<?> apply(@RequestBody ApplicationRequest r, HttpSession session) {
  Long signedInUserId = (Long) session.getAttribute("jobsphereUserId");
  if (signedInUserId == null) {
   return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
    .body(Map.of("message", "Please sign in with a candidate account before applying."));
  }
  var candidate = users.findById(signedInUserId).orElse(null);
  if (candidate == null || candidate.getRole() != Role.CANDIDATE) {
   return ResponseEntity.status(HttpStatus.FORBIDDEN)
    .body(Map.of("message", "Only signed-in candidate accounts can apply."));
  }

  if (r.jobId() == null) return bad("A valid job is required");
  var job = jobs.findById(r.jobId()).orElse(null);
  if (job == null) return bad("The selected job could not be found");

  if (apps.existsByJobIdAndCandidateId(job.getId(), candidate.getId()))
   return bad("You have already applied for this role");
  if (blank(r.applicantName()) || blank(r.applicantEmail()) || blank(r.education()) || blank(r.skills()))
   return bad("Please complete all required application fields");

  Application a = new Application();
  a.setJob(job);
  a.setCandidate(candidate);
  a.setApplicantName(r.applicantName().trim());
  a.setApplicantEmail(r.applicantEmail().trim());
  a.setApplicantPhone(trim(r.applicantPhone()));
  a.setEducation(trim(r.education()));
  a.setExperience(trim(r.experience()));
  a.setSkills(trim(r.skills()));
  a.setCoverLetter(trim(r.coverLetter()));
  Application saved = apps.save(a);
  return ResponseEntity.status(HttpStatus.CREATED).body(Map.of(
   "id", saved.getId(), "message", "Application submitted successfully",
   "status", saved.getStatus().name()
  ));
 }

 @Transactional
 @PatchMapping("/{id}/status")
 public ResponseEntity<?> updateStatus(@PathVariable Long id,
                                       @RequestParam ApplicationStatus status,
                                       @RequestParam Long recruiterId) {
  var recruiter = users.findById(recruiterId).orElse(null);
  if (recruiter == null || recruiter.getRole() != Role.RECRUITER) return bad("Valid recruiter account required");
  var application = apps.findById(id).orElse(null);
  if (application == null) return ResponseEntity.notFound().build();
  var job = application.getJob();
  if (job.getRecruiter() == null || !recruiter.getId().equals(job.getRecruiter().getId()))
   return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Map.of("message", "You can only update applicants for your own jobs"));
  if (status != ApplicationStatus.APPROVED_FOR_REFERRAL && status != ApplicationStatus.NOT_SELECTED && status != ApplicationStatus.REVIEWING)
   return bad("Unsupported application status");
  int updated = apps.updateStatus(id, status);
  if (updated != 1) return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("message", "Application status could not be updated"));
  return ResponseEntity.ok(Map.of("id", id, "status", status.name(), "message", "Application status updated"));
 }

 @GetMapping("/candidate/{id}")
 public ResponseEntity<?> candidate(@PathVariable Long id, HttpSession session) {
  Long signedInUserId = (Long) session.getAttribute("jobsphereUserId");
  if (signedInUserId == null) return unauthorized();
  var viewer = users.findById(signedInUserId).orElse(null);
  if (viewer == null || (!viewer.getId().equals(id) && viewer.getRole() != Role.RECRUITER))
   return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Map.of("message", "You cannot view another candidate's applications"));
  if (viewer.getRole() != Role.CANDIDATE && viewer.getRole() != Role.RECRUITER)
   return forbidden();
  return ResponseEntity.ok(apps.findByCandidateIdOrderByAppliedAtDesc(id));
 }

 @GetMapping("/recruiter/{id}")
 public ResponseEntity<?> recruiter(@PathVariable Long id, HttpSession session) {
  Long signedInUserId = (Long) session.getAttribute("jobsphereUserId");
  if (signedInUserId == null) return unauthorized();
  var viewer = users.findById(signedInUserId).orElse(null);
  if (viewer == null || !viewer.getId().equals(id) || viewer.getRole() != Role.RECRUITER) return forbidden();
  return ResponseEntity.ok(apps.findByJobRecruiterIdOrderByAppliedAtDesc(id));
 }

 @GetMapping("/company/{id}")
 public ResponseEntity<?> company(@PathVariable Long id, HttpSession session) {
  Long signedInUserId = (Long) session.getAttribute("jobsphereUserId");
  if (signedInUserId == null) return unauthorized();
  var viewer = users.findById(signedInUserId).orElse(null);
  if (viewer == null || viewer.getRole() != Role.COMPANY || !viewer.getId().equals(id)) return forbidden();
  return ResponseEntity.ok(apps.findByJobCompanyIgnoreCaseOrderByAppliedAtDesc(viewer.getCompanyName()));
 }

 private ResponseEntity<?> unauthorized() {
  return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("message", "Please sign in to continue."));
 }
 private ResponseEntity<?> forbidden() {
  return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Map.of("message", "You do not have permission to access this resource."));
 }
 private boolean blank(String s) { return s == null || s.trim().isBlank(); }
 private String trim(String s) { return s == null ? "" : s.trim(); }
 private ResponseEntity<?> bad(String m) { return ResponseEntity.badRequest().body(Map.of("message", m)); }
}