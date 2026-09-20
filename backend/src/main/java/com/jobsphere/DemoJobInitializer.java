package com.jobsphere;

import com.jobsphere.model.Job;
import com.jobsphere.repository.JobRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DemoJobInitializer {
 @Bean
 CommandLineRunner seedDemoJobs(JobRepository jobs){
  return args -> {
   addIfMissing(jobs,"Software Engineer","JP Morgan Chase","Hyderabad, India","Full-time","Entry-level","₹8–14 LPA","Java, Spring Boot, SQL, DSA","B.Tech/B.E. CSE/IT or related degree; 0–2 years.","Build and maintain backend services for financial applications.","LinkedIn","https://www.linkedin.com/");
   addIfMissing(jobs,"Graduate Software Engineer","Microsoft","Hyderabad, India","Full-time","Early career","₹10–18 LPA","Java, C++, Azure, DSA","B.Tech/B.E. in CSE/IT or related discipline; strong programming fundamentals.","Work with engineering teams to develop scalable cloud software.","Microsoft Careers","https://careers.microsoft.com/");
   addIfMissing(jobs,"Software Development Engineer","Amazon","Hyderabad, India","Full-time","Entry-level","₹9–18 LPA","Java, AWS, SQL, DSA","Bachelor's degree in computer science or equivalent; 0–2 years.","Develop reliable services and customer-facing technology.","Amazon Jobs","https://www.amazon.jobs/");
   addIfMissing(jobs,"Software Engineer","Google","Bengaluru, India","Full-time","Early career","₹12–24 LPA","Java, Python, Go, DSA","Strong programming, algorithms and problem-solving fundamentals.","Design, implement and test software used at scale.","Google Careers","https://careers.google.com/");
   addIfMissing(jobs,"Associate Software Engineer","Deloitte","Hyderabad, India","Full-time","Entry-level","₹6–10 LPA","Java, SQL, Spring Boot, Git","B.Tech/B.E. or equivalent; 0–2 years; relevant programming skills.","Support application development and engineering delivery.","Deloitte Careers","https://www.deloitte.com/");
   addIfMissing(jobs,"Backend Developer","Accenture","Hyderabad, India","Full-time","Entry-level","₹6–11 LPA","Java, Spring Boot, REST API, SQL","Bachelor's degree; 0–2 years; backend development fundamentals.","Build APIs and backend components for enterprise applications.","Accenture Careers","https://www.accenture.com/in-en/careers");
   addIfMissing(jobs,"Software Engineer","Oracle","Hyderabad, India","Full-time","Early career","₹8–15 LPA","Java, SQL, Linux, Cloud","B.Tech/B.E. or related degree; strong Java and database fundamentals.","Develop cloud and database technology solutions.","Oracle Careers","https://www.oracle.com/in/careers/");
   addIfMissing(jobs,"Systems Engineer","Infosys","Pune, India","Full-time","Entry-level","₹4.5–7 LPA","Java, Python, SQL, Linux","Graduate in CSE/IT or related field; strong programming fundamentals.","Contribute to application development, testing and support.","Infosys Careers","https://www.infosys.com/careers.html");
   addIfMissing(jobs,"Assistant System Engineer","TCS","Hyderabad, India","Full-time","Entry-level","₹4–7 LPA","Java, SQL, Git, DSA","B.Tech/B.E. in CSE/IT or related field; 0–2 years.","Work on enterprise software development and maintenance.","TCS Careers","https://www.tcs.com/careers");
   addIfMissing(jobs,"Software Engineer","IBM","Bengaluru, India","Full-time","Early career","₹7–13 LPA","Java, Python, REST API, Cloud","Bachelor's degree in computer science or related field.","Develop software and cloud solutions with cross-functional teams.","IBM Careers","https://www.ibm.com/careers");
   addIfMissing(jobs,"Associate Software Developer","Wipro","Hyderabad, India","Full-time","Entry-level","₹4.5–8 LPA","Java, SQL, HTML, CSS","B.Tech/B.E. or equivalent; 0–2 years.","Develop, test and maintain business applications.","Wipro Careers","https://careers.wipro.com/");
   addIfMissing(jobs,"Frontend Engineer","Adobe","Noida, India","Full-time","Early career","₹8–16 LPA","JavaScript, React, HTML, CSS","Bachelor's degree with strong web development fundamentals.","Build responsive interfaces and reusable frontend components.","Adobe Careers","https://www.adobe.com/careers.html");
   addIfMissing(jobs,"AI Software Engineer","NVIDIA","Bengaluru, India","Full-time","Early career","₹12–24 LPA","Python, C++, AI/ML, CUDA","Strong programming fundamentals with exposure to AI/ML.","Develop software for accelerated computing and AI systems.","NVIDIA Careers","https://www.nvidia.com/en-in/about-nvidia/careers/");
   addIfMissing(jobs,"Software Engineer","SAP","Bengaluru, India","Full-time","Entry-level","₹7–14 LPA","Java, Spring, SQL, OOP","B.Tech/B.E. CSE/IT or related degree; 0–2 years.","Develop enterprise software and cloud services.","SAP Careers","https://www.sap.com/india/about/careers.html");
   addIfMissing(jobs,"Junior Software Developer","Capgemini","Hyderabad, India","Full-time","Entry-level","₹5–9 LPA","Java, SQL, Git, REST API","Bachelor's degree; 0–2 years; software development fundamentals.","Support development and integration of enterprise applications.","Capgemini Careers","https://www.capgemini.com/in-en/careers/");
  };
 }
 private void addIfMissing(JobRepository jobs,String title,String company,String location,String type,String level,String salary,String skills,String eligibility,String description,String sourceName,String sourceUrl ){
  Job j=new Job(); j.setTitle(title);j.setCompany(company);j.setLocation(location);j.setType(type);j.setLevel(level);j.setSalary(salary);j.setSkills(skills);j.setEligibilityCriteria(eligibility);j.setDescription(description);j.setSourceName(sourceName);j.setSourceUrl(sourceUrl);j.setActive(true);jobs.save(j);
 }
}
