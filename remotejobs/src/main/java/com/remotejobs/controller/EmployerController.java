package com.remotejobs.controller;

import com.remotejobs.entity.Application;
import com.remotejobs.entity.Job;
import com.remotejobs.entity.User;
import com.remotejobs.service.*;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/employer")
public class EmployerController {

    @Autowired private UserService userService;
    @Autowired private JobService jobService;
    @Autowired private ApplicationService applicationService;
    @Autowired private NotificationService notificationService;

    private User getCurrentUser(Authentication auth) {
        return userService.findByEmail(auth.getName());
    }

    @GetMapping("/dashboard")
    public String dashboard(Model model, Authentication auth) {
        User employer = getCurrentUser(auth);
        List<Job> jobs = jobService.findByEmployer(employer);
        long totalApps = applicationService.countByEmployer(employer);
        long shortlisted = applicationService.countByStatusAndEmployer(Application.ApplicationStatus.SHORTLISTED, employer);

        model.addAttribute("user", employer);
        model.addAttribute("jobs", jobs);
        model.addAttribute("totalJobs", jobs.size());
        model.addAttribute("totalApplications", totalApps);
        model.addAttribute("shortlisted", shortlisted);
        model.addAttribute("unreadCount", notificationService.countUnread(employer));
        model.addAttribute("recentApplications", applicationService.findByEmployer(employer).stream().limit(5).toList());
        return "employer/dashboard";
    }

    @GetMapping("/jobs/new")
    public String newJobForm(Model model, Authentication auth) {
        User employer = getCurrentUser(auth);
        model.addAttribute("job", new Job());
        model.addAttribute("categories", Job.Category.values());
        model.addAttribute("jobTypes", Job.JobType.values());
        model.addAttribute("experienceLevels", Job.ExperienceLevel.values());
        model.addAttribute("unreadCount", notificationService.countUnread(employer));
        return "employer/job-form";
    }

    @PostMapping("/jobs/new")
    public String createJob(@Valid @ModelAttribute("job") Job job,
                            BindingResult result,
                            Authentication auth,
                            RedirectAttributes redirectAttrs,
                            Model model) {
        if (result.hasErrors()) {
            model.addAttribute("categories", Job.Category.values());
            model.addAttribute("jobTypes", Job.JobType.values());
            model.addAttribute("experienceLevels", Job.ExperienceLevel.values());
            return "employer/job-form";
        }
        User employer = getCurrentUser(auth);
        job.setEmployer(employer);
        job.setCompanyName(employer.getCompanyName() != null ? employer.getCompanyName() : job.getCompanyName());
        jobService.createJob(job);
        redirectAttrs.addFlashAttribute("success", "Job posted successfully!");
        return "redirect:/employer/jobs";
    }

    @GetMapping("/jobs/{id}/edit")
    public String editJobForm(@PathVariable Long id, Model model, Authentication auth) {
        User employer = getCurrentUser(auth);
        Job job = jobService.findById(id);
        if (!job.getEmployer().getId().equals(employer.getId())) {
            return "redirect:/employer/jobs";
        }
        model.addAttribute("job", job);
        model.addAttribute("categories", Job.Category.values());
        model.addAttribute("jobTypes", Job.JobType.values());
        model.addAttribute("experienceLevels", Job.ExperienceLevel.values());
        model.addAttribute("unreadCount", notificationService.countUnread(employer));
        return "employer/job-form";
    }

    @PostMapping("/jobs/{id}/edit")
    public String updateJob(@PathVariable Long id,
                            @Valid @ModelAttribute("job") Job updatedJob,
                            BindingResult result,
                            Authentication auth,
                            RedirectAttributes redirectAttrs,
                            Model model) {
        if (result.hasErrors()) {
            model.addAttribute("categories", Job.Category.values());
            model.addAttribute("jobTypes", Job.JobType.values());
            model.addAttribute("experienceLevels", Job.ExperienceLevel.values());
            return "employer/job-form";
        }
        User employer = getCurrentUser(auth);
        Job job = jobService.findById(id);
        if (!job.getEmployer().getId().equals(employer.getId())) {
            return "redirect:/employer/jobs";
        }
        job.setTitle(updatedJob.getTitle());
        job.setDescription(updatedJob.getDescription());
        job.setSkillsRequired(updatedJob.getSkillsRequired());
        job.setCategory(updatedJob.getCategory());
        job.setJobType(updatedJob.getJobType());
        job.setExperienceLevel(updatedJob.getExperienceLevel());
        job.setSalary(updatedJob.getSalary());
        job.setTimezonePreference(updatedJob.getTimezonePreference());
        job.setLocationRequirement(updatedJob.getLocationRequirement());
        job.setActive(updatedJob.isActive());
        job.setFeatured(updatedJob.isFeatured());
        jobService.updateJob(job);
        redirectAttrs.addFlashAttribute("success", "Job updated successfully!");
        return "redirect:/employer/jobs";
    }

    @PostMapping("/jobs/{id}/delete")
    public String deleteJob(@PathVariable Long id, Authentication auth, RedirectAttributes redirectAttrs) {
        User employer = getCurrentUser(auth);
        Job job = jobService.findById(id);
        if (job.getEmployer().getId().equals(employer.getId())) {
            jobService.deleteJob(id);
            redirectAttrs.addFlashAttribute("success", "Job deleted.");
        }
        return "redirect:/employer/jobs";
    }

    @GetMapping("/jobs")
    public String myJobs(Model model, Authentication auth) {
        User employer = getCurrentUser(auth);
        model.addAttribute("jobs", jobService.findByEmployer(employer));
        model.addAttribute("user", employer);
        model.addAttribute("unreadCount", notificationService.countUnread(employer));
        return "employer/jobs";
    }

    @GetMapping("/jobs/{id}/applicants")
    public String viewApplicants(@PathVariable Long id, Model model, Authentication auth) {
        User employer = getCurrentUser(auth);
        Job job = jobService.findById(id);
        if (!job.getEmployer().getId().equals(employer.getId())) {
            return "redirect:/employer/jobs";
        }
        List<Application> applications = applicationService.findByJob(job);
        model.addAttribute("job", job);
        model.addAttribute("applications", applications);
        model.addAttribute("user", employer);
        model.addAttribute("statuses", Application.ApplicationStatus.values());
        model.addAttribute("unreadCount", notificationService.countUnread(employer));
        return "employer/applicants";
    }

    @PostMapping("/applications/{id}/update-status")
    public String updateApplicationStatus(@PathVariable Long id,
                                          @RequestParam String status,
                                          @RequestParam(required = false) String notes,
                                          RedirectAttributes redirectAttrs) {
        Application.ApplicationStatus appStatus = Application.ApplicationStatus.valueOf(status);
        Application app = applicationService.updateStatus(id, appStatus, notes);
        redirectAttrs.addFlashAttribute("success", "Application status updated to " + status);
        return "redirect:/employer/jobs/" + app.getJob().getId() + "/applicants";
    }

    @GetMapping("/profile")
    public String profile(Model model, Authentication auth) {
        User user = getCurrentUser(auth);
        model.addAttribute("user", user);
        model.addAttribute("unreadCount", notificationService.countUnread(user));
        return "employer/profile";
    }

    @PostMapping("/profile/update")
    public String updateProfile(@ModelAttribute User updatedUser, Authentication auth,
                                RedirectAttributes redirectAttrs) {
        User user = getCurrentUser(auth);
        user.setFullName(updatedUser.getFullName());
        user.setPhone(updatedUser.getPhone());
        user.setCompanyName(updatedUser.getCompanyName());
        user.setCompanyWebsite(updatedUser.getCompanyWebsite());
        user.setProfileSummary(updatedUser.getProfileSummary());
        userService.updateUser(user);
        redirectAttrs.addFlashAttribute("success", "Profile updated!");
        return "redirect:/employer/profile";
    }

    @GetMapping("/notifications")
    public String notifications(Model model, Authentication auth) {
        User user = getCurrentUser(auth);
        notificationService.markAllRead(user);
        model.addAttribute("notifications", notificationService.findByUser(user));
        model.addAttribute("user", user);
        model.addAttribute("unreadCount", 0);
        return "employer/notifications";
    }
}
