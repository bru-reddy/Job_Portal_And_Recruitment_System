package com.jobsphere.model;
import jakarta.persistence.*;
import java.time.LocalDateTime;
@Entity @Table(name="applications", uniqueConstraints=@UniqueConstraint(columnNames={"job_id","candidate_id"}))
public class Application {
 @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Long id;
 @ManyToOne(optional=false) private Job job;
 @ManyToOne(optional=false) private User candidate;
 @Enumerated(EnumType.STRING) private ApplicationStatus status=ApplicationStatus.APPLIED;
 private LocalDateTime appliedAt=LocalDateTime.now();
 public Application() {}
 public Long getId(){return id;} public Job getJob(){return job;} public void setJob(Job v){job=v;}
 public User getCandidate(){return candidate;} public void setCandidate(User v){candidate=v;}
 public ApplicationStatus getStatus(){return status;} public void setStatus(ApplicationStatus v){status=v;}
 public LocalDateTime getAppliedAt(){return appliedAt;}
}
