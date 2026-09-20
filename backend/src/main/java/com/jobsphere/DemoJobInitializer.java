package com.jobsphere;

import com.jobsphere.repository.ApplicationRepository;
import com.jobsphere.repository.JobRepository;
import com.jobsphere.repository.SystemResetMarkerRepository;
import com.jobsphere.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DemoJobInitializer {
 @Bean
 CommandLineRunner initializeCleanStart(
   ApplicationRepository applications,
   JobRepository jobs,
   UserRepository users,
   SystemResetMarkerRepository markerRepository
 ){
  return args -> {
   if(markerRepository.existsById(2L)) return;

   applications.deleteAllInBatch();
   jobs.deleteAllInBatch();

   markerRepository.save(new com.jobsphere.model.SystemResetMarker(2L));
  };
 }
}
