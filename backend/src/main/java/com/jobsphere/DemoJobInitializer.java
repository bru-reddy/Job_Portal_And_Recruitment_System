package com.jobsphere;

import com.jobsphere.model.Job;
import com.jobsphere.repository.JobRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Set;

@Configuration
public class DemoJobInitializer {
 @Bean
 CommandLineRunner seedDemoJobs(JobRepository jobs){
  return args -> {
   deactivateLegacyDemoJobs(jobs);

   addIfMissing(jobs,"Java Full Stack Developer","Microsoft","Hyderabad, India","Full-time","Entry-level","₹8–14 LPA","Java, Spring Boot, React, SQL, DSA","B.Tech/B.E. CSE/IT or related degree; 0–2 years.","Build full-stack web applications using Java and modern frontend technologies.","JobSphere Demo","https://careers.microsoft.com/");
   addIfMissing(jobs,"Backend Software Engineer","Amazon","Hyderabad, India","Full-time","Entry-level","₹9–16 LPA","Java, Spring Boot, AWS, SQL, REST API","Bachelor's degree in computer science or equivalent; 0–2 years.","Develop scalable backend services and APIs for cloud applications.","JobSphere Demo","https://www.amazon.jobs/");
   addIfMissing(jobs,"Software Engineer","Google","Bengaluru, India","Full-time","Early career","₹12–22 LPA","Java, Python, DSA, Git, Cloud","Strong programming, algorithms and problem-solving fundamentals.","Design, implement and test software used at scale.","JobSphere Demo","https://careers.google.com/");
   addIfMissing(jobs,"Associate Software Engineer","Deloitte","Hyderabad, India","Full-time","Entry-level","₹6–10 LPA","Java, SQL, Spring Boot, Git","B.Tech/B.E. or equivalent; 0–2 years.","Develop and support enterprise applications and services.","JobSphere Demo","https://www.deloitte.com/careers");
   addIfMissing(jobs,"Frontend Developer","Infosys","Hyderabad, India","Full-time","Entry-level","₹5–9 LPA","JavaScript, React, HTML, CSS, Git","B.Tech/B.E. or equivalent; strong web development fundamentals.","Build responsive interfaces and reusable frontend components.","JobSphere Demo","https://www.infosys.com/careers.html");
  };
 }

 private void deactivateLegacyDemoJobs(JobRepository jobs){
  Set<String> legacyCompanies=Set.of("JP Morgan Chase","Microsoft","Amazon","Google","Deloitte","Accenture","Oracle","Infosys","TCS","IBM","Wipro","Adobe","NVIDIA","SAP","Capgemini");
  jobs.findAll().forEach(j -> {
   if(legacyCompanies.contains(j.getCompany()) && j.getSourceName()!=null && !j.getSourceName().equalsIgnoreCase("JobSphere Demo")){
    j.setActive(false);
    jobs.save(j);
   }
  });
 }

 private void addIfMissing(JobRepository jobs,String title,String company,String location,String type,String level,String salary,String skills,String eligibility,String description,String sourceName,String sourceUrl){
  Job existing=jobs.findAll().stream()
    .filter(j -> title.equalsIgnoreCase(j.getTitle()) && company.equalsIgnoreCase(j.getCompany()) && "JobSphere Demo".equalsIgnoreCase(j.getSourceName()))
    .findFirst().orElse(null);
  Job j=existing==null?new Job():existing;
  j.setTitle(title);j.setCompany(company);j.setLocation(location);j.setType(type);j.setLevel(level);j.setSalary(salary);
  j.setSkills(skills);j.setEligibilityCriteria(eligibility);j.setDescription(description);j.setSourceName(sourceName);j.setSourceUrl(sourceUrl);
  j.setActive(true);
  jobs.save(j);
 }
}
