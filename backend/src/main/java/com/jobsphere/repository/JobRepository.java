package com.jobsphere.repository;

import com.jobsphere.model.Job;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface JobRepository extends JpaRepository<Job,Long>{
 List<Job> findByActiveTrueOrderByCreatedAtDesc();
 List<Job> findByActiveTrueAndTitleContainingIgnoreCaseOrderByCreatedAtDesc(String title);
 boolean existsByCompanyIgnoreCaseAndTitleIgnoreCase(String company,String title);
}
