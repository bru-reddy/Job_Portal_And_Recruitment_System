package com.jobsphere.model;

import jakarta.persistence.*;

@Entity
@Table(name="system_reset_marker")
public class SystemResetMarker {
 @Id
 private Long id=1L;

 public SystemResetMarker() {}
 public Long getId(){return id;}
}