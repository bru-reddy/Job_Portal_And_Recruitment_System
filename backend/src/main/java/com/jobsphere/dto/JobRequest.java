package com.jobsphere.dto;
public record JobRequest(
 String title,String company,String location,String type,String level,String salary,
 String description,String skills,String eligibilityCriteria,Long recruiterId
) {}
