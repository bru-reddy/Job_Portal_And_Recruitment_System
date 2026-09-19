package com.jobsphere.dto;
import com.jobsphere.model.Role;
public record RegisterRequest(String name,String email,String password,Role role) {}
