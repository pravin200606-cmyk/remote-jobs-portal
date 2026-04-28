package com.remotejobs.service;

import com.remotejobs.entity.Job;
import com.remotejobs.entity.User;
import com.remotejobs.exception.ResourceNotFoundException;
import com.remotejobs.repository.JobRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class JobService {

    @Autowired
    private JobRepository jobRepository;

    public Job createJob(Job job) {
        return jobRepository.save(job);
    }

    public Job findById(Long id) {
        return jobRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Job not found with id: " + id));
    }

    public Job updateJob(Job job) {
        return jobRepository.save(job);
    }

    public void deleteJob(Long id) {
        jobRepository.deleteById(id);
    }

    public List<Job> findActiveJobs() {
        return jobRepository.findByActiveTrue();
    }

    public Page<Job> findActiveJobsPaged(int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        return jobRepository.findByActiveTrue(pageable);
    }

    public Page<Job> searchJobs(Job.Category category, Job.ExperienceLevel experienceLevel,
                                 Job.JobType jobType, String keyword, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        return jobRepository.searchJobs(category, experienceLevel, jobType, keyword, pageable);
    }

    public List<Job> findByEmployer(User employer) {
        return jobRepository.findByEmployer(employer);
    }

    public List<Job> findActiveByEmployer(User employer) {
        return jobRepository.findByEmployerAndActiveTrue(employer);
    }

    public List<Job> findFeaturedJobs() {
        return jobRepository.findByFeaturedTrueAndActiveTrue();
    }

    public List<Job> findLatestJobs(int count) {
        Pageable pageable = PageRequest.of(0, count);
        return jobRepository.findLatestJobs(pageable);
    }

    public long countActiveJobs() {
        return jobRepository.countByActiveTrue();
    }

    public void incrementViews(Long jobId) {
        Job job = findById(jobId);
        job.setViewsCount(job.getViewsCount() + 1);
        jobRepository.save(job);
    }

    public List<Job> findAllJobs() {
        return jobRepository.findAll(Sort.by("createdAt").descending());
    }
}
