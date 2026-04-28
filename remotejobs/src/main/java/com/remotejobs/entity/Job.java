package com.remotejobs.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "jobs")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Job {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Job title is required")
    @Column(nullable = false)
    private String title;

    @NotBlank(message = "Job description is required")
    @Column(columnDefinition = "TEXT", nullable = false)
    private String description;

    @NotBlank(message = "Company name is required")
    @Column(name = "company_name", nullable = false)
    private String companyName;

    @Column(name = "company_logo")
    private String companyLogo;

    @NotBlank(message = "Skills required is mandatory")
    @Column(name = "skills_required", columnDefinition = "TEXT")
    private String skillsRequired;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Category category;

    @Enumerated(EnumType.STRING)
    @Column(name = "job_type")
    private JobType jobType;

    @Enumerated(EnumType.STRING)
    @Column(name = "experience_level")
    private ExperienceLevel experienceLevel;

    private String salary;

    @Column(name = "salary_currency")
    private String salaryCurrency = "USD";

    @Column(name = "timezone_preference")
    private String timezonePreference;

    @Column(name = "location_requirement")
    private String locationRequirement;

    @Column(name = "application_deadline")
    private LocalDateTime applicationDeadline;

    @Column(name = "is_active")
    private boolean active = true;

    @Column(name = "is_featured")
    private boolean featured = false;

    @Column(name = "views_count")
    private int viewsCount = 0;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "employer_id", nullable = false)
    private User employer;

    @OneToMany(mappedBy = "job", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Application> applications;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    public enum Category {
        SOFTWARE_DEVELOPMENT, DATA_SCIENCE, DESIGN, MARKETING,
        CONTENT_WRITING, CUSTOMER_SUPPORT, FINANCE, HR,
        SALES, PROJECT_MANAGEMENT, DEVOPS, CYBERSECURITY, OTHER
    }

    public enum JobType {
        FULL_TIME, PART_TIME, CONTRACT, FREELANCE, INTERNSHIP
    }

    public enum ExperienceLevel {
        ENTRY_LEVEL, MID_LEVEL, SENIOR_LEVEL, LEAD, MANAGER
    }
}
