package com.jobsphere.dto;

public record LoginRequest(
 String email,
 String password,
 String role,
 String companyCode
) {}
