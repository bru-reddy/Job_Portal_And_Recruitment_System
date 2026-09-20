package com.jobsphere.dto;

public record ApplicationRequest(
 Long jobId,
 Long candidateId,
 String candidateEmail,
 String applicantName,
 String applicantEmail,
 String applicantPhone,
 String education,
 String experience,
 String skills,
 String coverLetter
) {}
