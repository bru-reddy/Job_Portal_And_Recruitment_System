package com.jobsphere.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name="applications", uniqueConstraints=@UniqueConstraint(columnNames={"job_id","candidate_id"}))
public class Application {
 @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Long id;
 @ManyToOne(optional=false) private Job job;
 @ManyToOne(optional=false) private User candidate;
 @Enumerated(EnumType.STRING) private ApplicationStatus status=ApplicationStatus.APPLIED;
 private LocalDateTime appliedAt=LocalDateTime.now();
 private String applicantName;
 private String applicantEmail;
 private String applicantPhone;
 private String education;
 private String experience;
 @Column(length=3000) private String skills;
 @Column(length=6000) private String coverLetter;

 public Application() {}
 public Long getId(){return id;}
 public Job getJob(){return job;} public void setJob(Job v){job=v;}
 public User getCandidate(){return candidate;} public void setCandidate(User v){candidate=v;}
 public ApplicationStatus getStatus(){return status;} public void setStatus(ApplicationStatus v){status=v;}
 public LocalDateTime getAppliedAt(){return appliedAt;}
 public String getApplicantName(){return applicantName;} public void setApplicantName(String v){applicantName=v;}
 public String getApplicantEmail(){return applicantEmail;} public void setApplicantEmail(String v){applicantEmail=v;}
 public String getApplicantPhone(){return applicantPhone;} public void setApplicantPhone(String v){applicantPhone=v;}
 public String getEducation(){return education;} public void setEducation(String v){education=v;}
 public String getExperience(){return experience;} public void setExperience(String v){experience=v;}
 public String getSkills(){return skills;} public void setSkills(String v){skills=v;}
 public String getCoverLetter(){return coverLetter;} public void setCoverLetter(String v){coverLetter=v;}
}
