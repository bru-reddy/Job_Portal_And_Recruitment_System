package com.jobsphere.repository;

import com.jobsphere.model.Application;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;

public interface ApplicationRepository extends JpaRepository<Application,Long>{
 List<Application> findByCandidateIdOrderByAppliedAtDesc(Long id);
 List<Application> findByJobRecruiterIdOrderByAppliedAtDesc(Long id);
 List<Application> findByJobCompanyIgnoreCaseOrderByAppliedAtDesc(String company);
 boolean existsByJobIdAndCandidateId(Long jobId,Long candidateId);
 @Modifying
 @Query("update Application a set a.status = :status where a.id = :id")
 int updateStatus(@Param("id") Long id,@Param("status") ApplicationStatus status);
}
