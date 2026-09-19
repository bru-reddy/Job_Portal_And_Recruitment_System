package com.jobsphere.model;

import jakarta.persistence.*;
import com.fasterxml.jackson.annotation.JsonIgnore;
import java.time.LocalDateTime;

@Entity
@Table(name="users")
public class User {
 @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Long id;
 @Column(nullable=false) private String name;
 @Column(nullable=false,unique=true) private String email;
 @JsonIgnore @Column(nullable=false) private String password;
 @Enumerated(EnumType.STRING) @Column(nullable=false) private Role role;
 private String phone;
 private String resumeUrl;
 private String companyName;
 private String companyWebsite;
 private String companyIndustry;
 @Column(length=4000) private String companyDescription;
 private LocalDateTime createdAt=LocalDateTime.now();

 public User() {}
 public Long getId(){return id;}
 public String getName(){return name;} public void setName(String v){name=v;}
 public String getEmail(){return email;} public void setEmail(String v){email=v;}
 public String getPassword(){return password;} public void setPassword(String v){password=v;}
 public Role getRole(){return role;} public void setRole(Role v){role=v;}
 public String getPhone(){return phone;} public void setPhone(String v){phone=v;}
 public String getResumeUrl(){return resumeUrl;} public void setResumeUrl(String v){resumeUrl=v;}
 public String getCompanyName(){return companyName;} public void setCompanyName(String v){companyName=v;}
 public String getCompanyWebsite(){return companyWebsite;} public void setCompanyWebsite(String v){companyWebsite=v;}
 public String getCompanyIndustry(){return companyIndustry;} public void setCompanyIndustry(String v){companyIndustry=v;}
 public String getCompanyDescription(){return companyDescription;} public void setCompanyDescription(String v){companyDescription=v;}
 public LocalDateTime getCreatedAt(){return createdAt;}
}
