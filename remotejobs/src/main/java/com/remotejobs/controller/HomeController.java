package com.remotejobs.controller;

import com.remotejobs.entity.Job;
import com.remotejobs.entity.User;
import com.remotejobs.service.JobService;
import com.remotejobs.service.NotificationService;
import com.remotejobs.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {

    @Autowired
    private JobService jobService;

    @Autowired
    private UserService userService;

    @Autowired
    private NotificationService notificationService;

    @GetMapping({"/", "/home"})
    public String home(Model model, Authentication auth) {
        model.addAttribute("featuredJobs", jobService.findFeaturedJobs());
        model.addAttribute("latestJobs", jobService.findLatestJobs(6));
        model.addAttribute("totalJobs", jobService.countActiveJobs());
        model.addAttribute("totalEmployers", userService.countByRole(User.Role.ROLE_EMPLOYER));
        model.addAttribute("totalSeekers", userService.countByRole(User.Role.ROLE_JOBSEEKER));
        model.addAttribute("categories", Job.Category.values());
        if (auth != null) {
            User user = userService.findByEmail(auth.getName());
            model.addAttribute("unreadCount", notificationService.countUnread(user));
        }
        return "home";
    }

    @GetMapping("/dashboard")
    public String dashboard(Authentication auth) {
        User user = userService.findByEmail(auth.getName());
        return switch (user.getRole()) {
            case ROLE_JOBSEEKER -> "redirect:/jobseeker/dashboard";
            case ROLE_EMPLOYER -> "redirect:/employer/dashboard";
            case ROLE_ADMIN -> "redirect:/admin/dashboard";
        };
    }

    @GetMapping("/access-denied")
    public String accessDenied(Model model) {
        model.addAttribute("error", "You don't have permission to access this page.");
        model.addAttribute("code", 403);
        return "error";
    }
}
