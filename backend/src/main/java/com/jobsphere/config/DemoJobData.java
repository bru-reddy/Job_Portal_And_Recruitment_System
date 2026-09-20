package com.jobsphere.config;

import com.jobsphere.model.Job;
import com.jobsphere.repository.JobRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;

@Configuration
public class DemoJobData {
 @Bean
 CommandLineRunner seedCatalogJobs(JobRepository jobs, JdbcTemplate jdbc){
  return args -> {
   jdbc.execute("ALTER TABLE jobs ALTER COLUMN recruiter_id DROP NOT NULL");
   jdbc.execute("ALTER TABLE jobs ADD COLUMN IF NOT EXISTS eligibility_criteria varchar(255)");
   add(jobs,"Software Engineer","Microsoft","Hyderabad, India","Full-time","Early career","₹8–18 LPA","Software engineering role for the JobSphere demo.","Java, C++, Azure, DSA");
   add(jobs,"Software Engineer II","Google","Hyderabad, India","Full-time","Early career","₹12–24 LPA","Engineering role used for the JobSphere simulation.","Java, Python, Go, DSA");
   add(jobs,"Software Development Engineer","Amazon","Hyderabad, India","Full-time","Entry-level","₹9–20 LPA","Backend and distributed-systems role for the demo portal.","Java, AWS, SQL, DSA");
   add(jobs,"Software Engineer","Apple","Hyderabad, India","Full-time","Early career","₹10–22 LPA","Software engineering role for the simulated workflow.","Swift, Java, C++, Algorithms");
   add(jobs,"Software Engineer","Meta","Bengaluru, India","Full-time","Early career","₹14–28 LPA","Product engineering role for the demo pipeline.","React, Python, Java, Distributed Systems");
   add(jobs,"AI Software Engineer","NVIDIA","Bengaluru, India","Full-time","Early career","₹12–26 LPA","AI and accelerated-computing role for the simulated portal.","Python, C++, CUDA, AI/ML");
   add(jobs,"Associate Software Engineer","IBM","Bengaluru, India","Full-time","Entry-level","₹5–11 LPA","Entry-level technology role for the demo.","Java, Python, Cloud, SQL");
   add(jobs,"Application Developer","Accenture","Hyderabad, India","Full-time","Entry-level","₹5–10 LPA","Technology consulting role for the demo.","Java, Spring Boot, SQL, Cloud");
   add(jobs,"Systems Engineer","Infosys","Hyderabad, India","Full-time","Entry-level","₹4–9 LPA","Graduate technology role for the demo.","Java, Python, SQL, Cloud");
   add(jobs,"Assistant System Engineer","TCS","Hyderabad, India","Full-time","Entry-level","₹4–9 LPA","Technology services role for the demo.","Java, SQL, Python, Git");
   add(jobs,"Software Development Engineer","Oracle","Hyderabad, India","Full-time","Early career","₹8–18 LPA","Cloud and database engineering role for the demo.","Java, OCI, SQL, Cloud");
   add(jobs,"Software Engineer","Adobe","Noida, India","Full-time","Early career","₹10–22 LPA","Product engineering role for the demo.","Java, C++, JavaScript, Algorithms");
  };
 }
 private void add(JobRepository jobs,String title,String company,String location,String type,String level,String salary,String description,String skills){
  if(jobs.existsByCompanyIgnoreCaseAndTitleIgnoreCase(company,title)) return;
  Job j=new Job();j.setTitle(title);j.setCompany(company);j.setLocation(location);j.setType(type);j.setLevel(level);j.setSalary(salary);
  j.setDescription(description);j.setSkills(skills);j.setEligibilityCriteria(level+" candidates with relevant "+skills+" skills");j.setActive(true);jobs.save(j);
 }
}
