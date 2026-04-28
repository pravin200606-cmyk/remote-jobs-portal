package com.remotejobs.controller;

import com.remotejobs.entity.Application;
import com.remotejobs.entity.Job;
import com.remotejobs.entity.Notification;
import com.remotejobs.entity.User;
import com.remotejobs.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/jobseeker")
public class JobSeekerController {

    @Autowired private UserService userService;
    @Autowired private JobService jobService;
    @Autowired private ApplicationService applicationService;
    @Autowired private NotificationService notificationService;
    @Autowired private FileStorageService fileStorageService;

    private User getCurrentUser(Authentication auth) {
        return userService.findByEmail(auth.getName());
    }

    @GetMapping("/dashboard")
    public String dashboard(Model model, Authentication auth) {
        User user = getCurrentUser(auth);
        List<Application> applications = applicationService.findByApplicant(user);
        long unread = notificationService.countUnread(user);

        model.addAttribute("user", user);
        model.addAttribute("applications", applications);
        model.addAttribute("totalApplications", applications.size());
        model.addAttribute("shortlisted", applications.stream().filter(a -> a.getStatus() == Application.ApplicationStatus.SHORTLISTED).count());
        model.addAttribute("pending", applications.stream().filter(a -> a.getStatus() == Application.ApplicationStatus.PENDING).count());
        model.addAttribute("unreadCount", unread);
        model.addAttribute("latestJobs", jobService.findLatestJobs(5));
        return "jobseeker/dashboard";
    }

    @GetMapping("/profile")
    public String profile(Model model, Authentication auth) {
        User user = getCurrentUser(auth);
        model.addAttribute("user", user);
        model.addAttribute("unreadCount", notificationService.countUnread(user));
        return "jobseeker/profile";
    }

    @PostMapping("/profile/update")
    public String updateProfile(@ModelAttribute User updatedUser,
                                @RequestParam(required = false) MultipartFile resumeFile,
                                Authentication auth,
                                RedirectAttributes redirectAttrs) {
        User user = getCurrentUser(auth);
        user.setFullName(updatedUser.getFullName());
        user.setPhone(updatedUser.getPhone());
        user.setProfileSummary(updatedUser.getProfileSummary());
        user.setSkills(updatedUser.getSkills());
        user.setLinkedinUrl(updatedUser.getLinkedinUrl());
        user.setTimezone(updatedUser.getTimezone());
        user.setYearsExperience(updatedUser.getYearsExperience());
        user.setExpectedSalary(updatedUser.getExpectedSalary());

        if (resumeFile != null && !resumeFile.isEmpty()) {
            try {
                if (user.getResumeFilename() != null) {
                    fileStorageService.deleteFile(user.getResumeFilename());
                }
                String filename = fileStorageService.storeFile(resumeFile);
                user.setResumeFilename(filename);
                user.setResumeOriginalName(resumeFile.getOriginalFilename());
            } catch (Exception e) {
                redirectAttrs.addFlashAttribute("error", "Resume upload failed: " + e.getMessage());
                return "redirect:/jobseeker/profile";
            }
        }

        userService.updateUser(user);
        redirectAttrs.addFlashAttribute("success", "Profile updated successfully!");
        return "redirect:/jobseeker/profile";
    }

    @GetMapping("/apply/{jobId}")
    public String applyForm(@PathVariable Long jobId, Model model, Authentication auth) {
        User user = getCurrentUser(auth);
        Job job = jobService.findById(jobId);

        if (applicationService.hasApplied(job, user)) {
            model.addAttribute("error", "You have already applied for this job.");
            return "redirect:/jobs/" + jobId;
        }

        model.addAttribute("job", job);
        model.addAttribute("user", user);
        model.addAttribute("unreadCount", notificationService.countUnread(user));
        return "jobseeker/apply";
    }

    @PostMapping("/apply/{jobId}")
    public String submitApplication(@PathVariable Long jobId,
                                    @RequestParam(required = false) String coverLetter,
                                    @RequestParam(required = false) MultipartFile resumeFile,
                                    Authentication auth,
                                    RedirectAttributes redirectAttrs) {
        User user = getCurrentUser(auth);
        Job job = jobService.findById(jobId);

        Application application = new Application();
        application.setJob(job);
        application.setApplicant(user);
        application.setCoverLetter(coverLetter);

        if (resumeFile != null && !resumeFile.isEmpty()) {
            try {
                String filename = fileStorageService.storeFile(resumeFile);
                application.setResumeFilename(filename);
                application.setResumeOriginalName(resumeFile.getOriginalFilename());
            } catch (Exception e) {
                redirectAttrs.addFlashAttribute("error", "Resume upload failed: " + e.getMessage());
                return "redirect:/jobseeker/apply/" + jobId;
            }
        } else if (user.getResumeFilename() != null) {
            application.setResumeFilename(user.getResumeFilename());
            application.setResumeOriginalName(user.getResumeOriginalName());
        }

        try {
            applicationService.apply(application);
            redirectAttrs.addFlashAttribute("success", "Application submitted successfully!");
        } catch (IllegalArgumentException e) {
            redirectAttrs.addFlashAttribute("error", e.getMessage());
        }

        return "redirect:/jobseeker/applications";
    }

    @GetMapping("/applications")
    public String myApplications(Model model, Authentication auth) {
        User user = getCurrentUser(auth);
        model.addAttribute("applications", applicationService.findByApplicant(user));
        model.addAttribute("user", user);
        model.addAttribute("unreadCount", notificationService.countUnread(user));
        return "jobseeker/applications";
    }

    @GetMapping("/notifications")
    public String notifications(Model model, Authentication auth) {
        User user = getCurrentUser(auth);
        List<Notification> notifications = notificationService.findByUser(user);
        notificationService.markAllRead(user);
        model.addAttribute("notifications", notifications);
        model.addAttribute("user", user);
        model.addAttribute("unreadCount", 0);
        return "jobseeker/notifications";
    }
}
