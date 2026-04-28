package com.remotejobs.service;

import com.remotejobs.entity.Application;
import com.remotejobs.entity.Job;
import com.remotejobs.entity.User;
import com.remotejobs.exception.ResourceNotFoundException;
import com.remotejobs.repository.ApplicationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class ApplicationService {

    @Autowired
    private ApplicationRepository applicationRepository;

    @Autowired
    private NotificationService notificationService;

    public Application apply(Application application) {
        if (applicationRepository.existsByJobAndApplicant(application.getJob(), application.getApplicant())) {
            throw new IllegalArgumentException("You have already applied for this job");
        }
        Application saved = applicationRepository.save(application);
        // Notify employer
        notificationService.createNotification(
            application.getJob().getEmployer(),
            application.getApplicant().getFullName() + " applied for " + application.getJob().getTitle(),
            "NEW_APPLICATION",
            saved.getId()
        );
        return saved;
    }

    public Application findById(Long id) {
        return applicationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Application not found"));
    }

    public List<Application> findByApplicant(User applicant) {
        return applicationRepository.findByApplicantOrderByAppliedAtDesc(applicant);
    }

    public List<Application> findByJob(Job job) {
        return applicationRepository.findByJobOrderByAppliedAtDesc(job);
    }

    public List<Application> findByEmployer(User employer) {
        return applicationRepository.findByEmployer(employer);
    }

    public Application updateStatus(Long id, Application.ApplicationStatus status, String notes) {
        Application app = findById(id);
        app.setStatus(status);
        if (notes != null && !notes.isEmpty()) {
            app.setEmployerNotes(notes);
        }
        Application updated = applicationRepository.save(app);

        // Notify job seeker
        String message = switch (status) {
            case SHORTLISTED -> "Congratulations! You have been shortlisted for " + app.getJob().getTitle();
            case INTERVIEW_SCHEDULED -> "Interview scheduled for " + app.getJob().getTitle() + "!";
            case REJECTED -> "Your application for " + app.getJob().getTitle() + " was not selected";
            case HIRED -> "Congratulations! You got the job for " + app.getJob().getTitle() + "!";
            default -> "Your application for " + app.getJob().getTitle() + " status updated to " + status.name();
        };

        notificationService.createNotification(app.getApplicant(), message, "APPLICATION_UPDATE", id);
        return updated;
    }

    public boolean hasApplied(Job job, User applicant) {
        return applicationRepository.existsByJobAndApplicant(job, applicant);
    }

    public long countByApplicant(User applicant) {
        return applicationRepository.countByApplicant(applicant);
    }

    public long countByJob(Job job) {
        return applicationRepository.countByJob(job);
    }

    public long countByEmployer(User employer) {
        return applicationRepository.countByEmployer(employer);
    }

    public long countByStatusAndEmployer(Application.ApplicationStatus status, User employer) {
        return applicationRepository.countByStatusAndEmployer(status, employer);
    }
}
