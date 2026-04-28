package com.remotejobs.repository;

import com.remotejobs.entity.Job;
import com.remotejobs.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface JobRepository extends JpaRepository<Job, Long> {

    List<Job> findByActiveTrue();

    Page<Job> findByActiveTrue(Pageable pageable);

    List<Job> findByEmployer(User employer);

    List<Job> findByEmployerAndActiveTrue(User employer);

    @Query("SELECT j FROM Job j WHERE j.active = true AND " +
           "(:category IS NULL OR j.category = :category) AND " +
           "(:experienceLevel IS NULL OR j.experienceLevel = :experienceLevel) AND " +
           "(:jobType IS NULL OR j.jobType = :jobType) AND " +
           "(:keyword IS NULL OR LOWER(j.title) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "LOWER(j.skillsRequired) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "LOWER(j.companyName) LIKE LOWER(CONCAT('%', :keyword, '%')))")
    Page<Job> searchJobs(@Param("category") Job.Category category,
                         @Param("experienceLevel") Job.ExperienceLevel experienceLevel,
                         @Param("jobType") Job.JobType jobType,
                         @Param("keyword") String keyword,
                         Pageable pageable);

    List<Job> findByFeaturedTrueAndActiveTrue();

    long countByActiveTrue();

    @Query("SELECT j FROM Job j WHERE j.active = true ORDER BY j.createdAt DESC")
    List<Job> findLatestJobs(Pageable pageable);
}
