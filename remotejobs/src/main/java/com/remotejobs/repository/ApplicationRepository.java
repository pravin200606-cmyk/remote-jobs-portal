package com.remotejobs.repository;

import com.remotejobs.entity.Application;
import com.remotejobs.entity.Job;
import com.remotejobs.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ApplicationRepository extends JpaRepository<Application, Long> {

    List<Application> findByApplicant(User applicant);

    List<Application> findByJob(Job job);

    Optional<Application> findByJobAndApplicant(Job job, User applicant);

    boolean existsByJobAndApplicant(Job job, User applicant);

    List<Application> findByApplicantOrderByAppliedAtDesc(User applicant);

    List<Application> findByJobOrderByAppliedAtDesc(Job job);

    long countByApplicant(User applicant);

    long countByJob(Job job);

    @Query("SELECT COUNT(a) FROM Application a WHERE a.job.employer = :employer")
    long countByEmployer(@Param("employer") User employer);

    @Query("SELECT a FROM Application a WHERE a.job.employer = :employer ORDER BY a.appliedAt DESC")
    List<Application> findByEmployer(@Param("employer") User employer);

    @Query("SELECT COUNT(a) FROM Application a WHERE a.status = :status AND a.job.employer = :employer")
    long countByStatusAndEmployer(@Param("status") Application.ApplicationStatus status,
                                   @Param("employer") User employer);
}
