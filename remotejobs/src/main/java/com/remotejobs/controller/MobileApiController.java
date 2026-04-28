package com.remotejobs.controller;

import com.remotejobs.entity.Application;
import com.remotejobs.entity.Job;
import com.remotejobs.entity.User;
import com.remotejobs.repository.ApplicationRepository;
import com.remotejobs.repository.JobRepository;
import com.remotejobs.repository.UserRepository;
import com.remotejobs.service.JobService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api")
public class MobileApiController {

    @Autowired private JobService jobService;
    @Autowired private JobRepository jobRepository;
    @Autowired private UserRepository userRepository;
    @Autowired private ApplicationRepository applicationRepository;

    // ────────────────── JOBS ──────────────────

    @GetMapping("/jobs")
    public ResponseEntity<?> getJobs(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(required = false) String search,
            @RequestParam(required = false) String type,
            @RequestParam(required = false) String category) {
        try {
            Job.JobType jobType = parseEnum(Job.JobType.class, type);
            Job.Category cat = parseEnum(Job.Category.class, category);
            Page<Job> jobsPage = jobService.searchJobs(cat, null, jobType, search, page, size);
            List<Map<String, Object>> jobs = jobsPage.getContent().stream()
                .map(this::mapJob).collect(Collectors.toList());
            Map<String, Object> result = new HashMap<>();
            result.put("content", jobs);
            result.put("totalElements", jobsPage.getTotalElements());
            result.put("totalPages", jobsPage.getTotalPages());
            result.put("page", page);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of("message", e.getMessage()));
        }
    }

    @GetMapping("/jobs/{id}")
    public ResponseEntity<?> getJob(@PathVariable Long id) {
        try {
            Job job = jobService.findById(id);
            jobService.incrementViews(id);
            return ResponseEntity.ok(mapJobDetail(job));
        } catch (Exception e) {
            return ResponseEntity.status(404).body(Map.of("message", "Job not found"));
        }
    }

    @PostMapping("/jobs/{id}/apply")
    public ResponseEntity<?> applyForJob(@PathVariable Long id, @RequestBody Map<String, String> req) {
        User user = getCurrentUser();
        if (user == null) return ResponseEntity.status(401).body(Map.of("message", "Not authenticated"));
        if (user.getRole() != User.Role.ROLE_JOBSEEKER)
            return ResponseEntity.status(403).body(Map.of("message", "Only job seekers can apply"));
        try {
            Job job = jobService.findById(id);
            // Check duplicate
            boolean alreadyApplied = applicationRepository.existsByJobAndApplicant(job, user);
            if (alreadyApplied) return ResponseEntity.badRequest().body(Map.of("message", "Already applied to this job"));
            Application app = new Application();
            app.setJob(job);
            app.setApplicant(user);
            app.setCoverLetter(req.getOrDefault("coverLetter", ""));
            applicationRepository.save(app);
            return ResponseEntity.ok(Map.of("message", "Application submitted successfully"));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of("message", e.getMessage()));
        }
    }

    // ────────────────── JOB SEEKER ──────────────────

    @GetMapping("/jobseeker/applications")
    public ResponseEntity<?> myApplications() {
        User user = getCurrentUser();
        if (user == null) return ResponseEntity.status(401).body(Map.of("message", "Not authenticated"));
        List<Application> apps = applicationRepository.findByApplicantOrderByAppliedAtDesc(user);
        List<Map<String, Object>> result = apps.stream().map(a -> {
            Map<String, Object> m = new HashMap<>();
            m.put("id", a.getId());
            m.put("jobId", a.getJob().getId());
            m.put("jobTitle", a.getJob().getTitle());
            m.put("company", a.getJob().getCompanyName());
            m.put("status", a.getStatus().name());
            m.put("appliedDate", a.getAppliedAt());
            m.put("coverLetter", a.getCoverLetter());
            return m;
        }).collect(Collectors.toList());
        return ResponseEntity.ok(result);
    }

    @GetMapping("/jobseeker/profile")
    public ResponseEntity<?> getProfile() {
        User user = getCurrentUser();
        if (user == null) return ResponseEntity.status(401).body(Map.of("message", "Not authenticated"));
        return ResponseEntity.ok(mapUserProfile(user));
    }

    @PutMapping("/jobseeker/profile")
    public ResponseEntity<?> updateProfile(@RequestBody Map<String, String> req) {
        User user = getCurrentUser();
        if (user == null) return ResponseEntity.status(401).body(Map.of("message", "Not authenticated"));
        if (req.containsKey("name")) user.setFullName(req.get("name"));
        if (req.containsKey("phone")) user.setPhone(req.get("phone"));
        if (req.containsKey("bio")) user.setProfileSummary(req.get("bio"));
        if (req.containsKey("skills")) user.setSkills(req.get("skills"));
        if (req.containsKey("linkedinUrl")) user.setLinkedinUrl(req.get("linkedinUrl"));
        if (req.containsKey("timezone")) user.setTimezone(req.get("timezone"));
        userRepository.save(user);
        return ResponseEntity.ok(Map.of("message", "Profile updated"));
    }

    // ────────────────── EMPLOYER ──────────────────

    @GetMapping("/employer/jobs")
    public ResponseEntity<?> employerJobs() {
        User user = getCurrentUser();
        if (user == null) return ResponseEntity.status(401).body(Map.of("message", "Not authenticated"));
        List<Job> jobs = jobService.findByEmployer(user);
        return ResponseEntity.ok(jobs.stream().map(this::mapJob).collect(Collectors.toList()));
    }

    @PostMapping("/employer/jobs")
    public ResponseEntity<?> postJob(@RequestBody Map<String, Object> req) {
        User user = getCurrentUser();
        if (user == null) return ResponseEntity.status(401).body(Map.of("message", "Not authenticated"));
        if (user.getRole() != User.Role.ROLE_EMPLOYER)
            return ResponseEntity.status(403).body(Map.of("message", "Only employers can post jobs"));
        try {
            Job job = new Job();
            job.setTitle((String) req.getOrDefault("title", ""));
            job.setDescription((String) req.getOrDefault("description", ""));
            job.setCompanyName(user.getCompanyName() != null ? user.getCompanyName() : (String) req.getOrDefault("company", ""));
            job.setSkillsRequired((String) req.getOrDefault("requirements", (String) req.getOrDefault("skills", "N/A")));
            job.setSalary((String) req.getOrDefault("salary", "Competitive"));
            job.setLocationRequirement((String) req.getOrDefault("location", "Remote"));
            job.setEmployer(user);
            job.setActive(true);
            String typeStr = (String) req.getOrDefault("type", "FULL_TIME");
            job.setJobType(parseEnum(Job.JobType.class, typeStr));
            String catStr = (String) req.getOrDefault("category", "OTHER");
            job.setCategory(parseEnum(Job.Category.class, catStr));
            jobService.createJob(job);
            return ResponseEntity.ok(Map.of("message", "Job posted successfully", "id", job.getId()));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of("message", e.getMessage()));
        }
    }

    @DeleteMapping("/employer/jobs/{id}")
    public ResponseEntity<?> deleteJob(@PathVariable Long id) {
        User user = getCurrentUser();
        if (user == null) return ResponseEntity.status(401).body(Map.of("message", "Not authenticated"));
        try {
            Job job = jobService.findById(id);
            if (!job.getEmployer().getId().equals(user.getId()))
                return ResponseEntity.status(403).body(Map.of("message", "Not your job"));
            jobService.deleteJob(id);
            return ResponseEntity.ok(Map.of("message", "Job deleted"));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of("message", e.getMessage()));
        }
    }

    // ────────────────── ADMIN ──────────────────

    @GetMapping("/admin/stats")
    public ResponseEntity<?> adminStats() {
        Map<String, Object> stats = new HashMap<>();
        stats.put("totalJobs", jobRepository.count());
        stats.put("activeJobs", jobRepository.countByActiveTrue());
        stats.put("totalUsers", userRepository.count());
        stats.put("totalApplications", applicationRepository.count());
        return ResponseEntity.ok(stats);
    }

    @GetMapping("/admin/users")
    public ResponseEntity<?> adminUsers() {
        return ResponseEntity.ok(userRepository.findAll().stream().map(u -> {
            Map<String, Object> m = new HashMap<>();
            m.put("id", u.getId()); m.put("name", u.getFullName());
            m.put("email", u.getEmail()); m.put("role", u.getRole());
            m.put("active", u.isEnabled()); return m;
        }).collect(Collectors.toList()));
    }

    @GetMapping("/admin/jobs")
    public ResponseEntity<?> adminJobs() {
        return ResponseEntity.ok(jobRepository.findAll().stream().map(this::mapJob).collect(Collectors.toList()));
    }

    // ────────────────── HELPERS ──────────────────

    private User getCurrentUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated() || auth.getPrincipal().equals("anonymousUser")) return null;
        String email = ((UserDetails) auth.getPrincipal()).getUsername();
        return userRepository.findByEmail(email).orElse(null);
    }

    private Map<String, Object> mapJob(Job j) {
        Map<String, Object> m = new HashMap<>();
        m.put("id", j.getId());
        m.put("title", j.getTitle());
        m.put("company", j.getCompanyName());
        m.put("companyName", j.getCompanyName());
        m.put("type", j.getJobType() != null ? j.getJobType().name() : null);
        m.put("category", j.getCategory() != null ? j.getCategory().name() : null);
        m.put("salary", j.getSalary());
        m.put("location", j.getLocationRequirement());
        m.put("active", j.isActive());
        m.put("featured", j.isFeatured());
        m.put("postedDate", j.getCreatedAt());
        m.put("applicationCount", j.getApplications() != null ? j.getApplications().size() : 0);
        return m;
    }

    private Map<String, Object> mapJobDetail(Job j) {
        Map<String, Object> m = mapJob(j);
        m.put("description", j.getDescription());
        m.put("requirements", j.getSkillsRequired());
        m.put("experienceLevel", j.getExperienceLevel());
        m.put("timezonePreference", j.getTimezonePreference());
        m.put("deadline", j.getApplicationDeadline());
        return m;
    }

    private Map<String, Object> mapUserProfile(User u) {
        Map<String, Object> m = new HashMap<>();
        m.put("id", u.getId()); m.put("name", u.getFullName());
        m.put("email", u.getEmail()); m.put("phone", u.getPhone());
        m.put("bio", u.getProfileSummary()); m.put("skills", u.getSkills());
        m.put("linkedinUrl", u.getLinkedinUrl()); m.put("timezone", u.getTimezone());
        m.put("companyName", u.getCompanyName());
        m.put("role", u.getRole().name().replace("ROLE_", "").toLowerCase());
        return m;
    }

    private <T extends Enum<T>> T parseEnum(Class<T> clazz, String value) {
        if (value == null || value.isBlank()) return null;
        try { return Enum.valueOf(clazz, value.toUpperCase()); }
        catch (IllegalArgumentException e) { return null; }
    }
}
