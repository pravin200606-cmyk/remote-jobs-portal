package com.remotejobs.controller;

import com.remotejobs.entity.Application;
import com.remotejobs.entity.Job;
import com.remotejobs.entity.User;
import com.remotejobs.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/admin")
public class AdminController {

    @Autowired private UserService userService;
    @Autowired private JobService jobService;
    @Autowired private ApplicationService applicationService;
    @Autowired private NotificationService notificationService;

    private User getCurrentUser(Authentication auth) {
        return userService.findByEmail(auth.getName());
    }

    @GetMapping("/dashboard")
    public String dashboard(Model model, Authentication auth) {
        model.addAttribute("user", getCurrentUser(auth));
        model.addAttribute("totalUsers", userService.findAllUsers().size());
        model.addAttribute("totalJobs", jobService.countActiveJobs());
        model.addAttribute("totalEmployers", userService.countByRole(User.Role.ROLE_EMPLOYER));
        model.addAttribute("totalSeekers", userService.countByRole(User.Role.ROLE_JOBSEEKER));
        model.addAttribute("recentJobs", jobService.findLatestJobs(5));
        model.addAttribute("recentUsers", userService.findAllUsers().stream().limit(5).toList());
        return "admin/dashboard";
    }

    @GetMapping("/users")
    public String listUsers(Model model, Authentication auth) {
        model.addAttribute("user", getCurrentUser(auth));
        model.addAttribute("users", userService.findAllUsers());
        return "admin/users";
    }

    @PostMapping("/users/{id}/toggle")
    public String toggleUser(@PathVariable Long id, RedirectAttributes redirectAttrs) {
        User user = userService.findById(id);
        user.setEnabled(!user.isEnabled());
        userService.updateUser(user);
        redirectAttrs.addFlashAttribute("success", "User status updated.");
        return "redirect:/admin/users";
    }

    @PostMapping("/users/{id}/delete")
    public String deleteUser(@PathVariable Long id, RedirectAttributes redirectAttrs) {
        userService.deleteUser(id);
        redirectAttrs.addFlashAttribute("success", "User deleted.");
        return "redirect:/admin/users";
    }

    @GetMapping("/jobs")
    public String listJobs(Model model, Authentication auth) {
        model.addAttribute("user", getCurrentUser(auth));
        model.addAttribute("jobs", jobService.findAllJobs());
        return "admin/jobs";
    }

    @PostMapping("/jobs/{id}/toggle")
    public String toggleJob(@PathVariable Long id, RedirectAttributes redirectAttrs) {
        Job job = jobService.findById(id);
        job.setActive(!job.isActive());
        jobService.updateJob(job);
        redirectAttrs.addFlashAttribute("success", "Job status updated.");
        return "redirect:/admin/jobs";
    }

    @PostMapping("/jobs/{id}/featured")
    public String toggleFeatured(@PathVariable Long id, RedirectAttributes redirectAttrs) {
        Job job = jobService.findById(id);
        job.setFeatured(!job.isFeatured());
        jobService.updateJob(job);
        redirectAttrs.addFlashAttribute("success", "Featured status updated.");
        return "redirect:/admin/jobs";
    }

    @PostMapping("/jobs/{id}/delete")
    public String deleteJob(@PathVariable Long id, RedirectAttributes redirectAttrs) {
        jobService.deleteJob(id);
        redirectAttrs.addFlashAttribute("success", "Job deleted.");
        return "redirect:/admin/jobs";
    }
}
