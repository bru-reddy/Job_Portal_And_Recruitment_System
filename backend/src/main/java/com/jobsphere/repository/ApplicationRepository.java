package com.jobsphere.repository;
import com.jobsphere.model.Application;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
public interface ApplicationRepository extends JpaRepository<Application,Long>{
 List<Application> findByCandidateIdOrderByAppliedAtDesc(Long id);
 List<Application> findByJobRecruiterIdOrderByAppliedAtDesc(Long id);
 boolean existsByJobIdAndCandidateId(Long jobId,Long candidateId);
}
