package com.jobsphere.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity @Table(name="jobs")
public class Job {
 @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Long id;
 @Column(nullable=false) private String title;
 @Column(nullable=false) private String company;
 @Column(nullable=false) private String location;
 private String type, level, salary;
 private String sourceName, sourceUrl;
 @Column(columnDefinition="TEXT") private String description;
 @Column(columnDefinition="TEXT") private String skills;
 @Column(columnDefinition="TEXT") private String eligibilityCriteria;
 @ManyToOne(optional=true) private User recruiter;
 private boolean active=true;
 private LocalDateTime createdAt=LocalDateTime.now();

 public Job() {}
 public Long getId(){return id;} public String getTitle(){return title;} public void setTitle(String v){title=v;}
 public String getCompany(){return company;} public void setCompany(String v){company=v;}
 public String getLocation(){return location;} public void setLocation(String v){location=v;}
 public String getType(){return type;} public void setType(String v){type=v;}
 public String getLevel(){return level;} public void setLevel(String v){level=v;}
 public String getSalary(){return salary;} public void setSalary(String v){salary=v;}
 public String getSourceName(){return sourceName;} public void setSourceName(String v){sourceName=v;}
 public String getSourceUrl(){return sourceUrl;} public void setSourceUrl(String v){sourceUrl=v;}
 public String getDescription(){return description;} public void setDescription(String v){description=v;}
 public String getSkills(){return skills;} public void setSkills(String v){skills=v;}
 public String getEligibilityCriteria(){return eligibilityCriteria;} public void setEligibilityCriteria(String v){eligibilityCriteria=v;}
 public User getRecruiter(){return recruiter;} public void setRecruiter(User v){recruiter=v;}
 public boolean isActive(){return active;} public void setActive(boolean v){active=v;}
 public LocalDateTime getCreatedAt(){return createdAt;}
}