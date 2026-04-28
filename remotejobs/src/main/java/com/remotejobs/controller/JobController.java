package com.remotejobs.controller;

import com.remotejobs.entity.Application;
import com.remotejobs.entity.Job;
import com.remotejobs.entity.User;
import com.remotejobs.service.ApplicationService;
import com.remotejobs.service.JobService;
import com.remotejobs.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/jobs")
public class JobController {

    @Autowired private JobService jobService;
    @Autowired private UserService userService;
    @Autowired private ApplicationService applicationService;

    @GetMapping
    public String listJobs(@RequestParam(defaultValue = "0") int page,
                           @RequestParam(defaultValue = "10") int size,
                           @RequestParam(required = false) String keyword,
                           @RequestParam(required = false) String category,
                           @RequestParam(required = false) String experienceLevel,
                           @RequestParam(required = false) String jobType,
                           Model model, Authentication auth) {

        Job.Category cat = parseEnum(Job.Category.class, category);
        Job.ExperienceLevel exp = parseEnum(Job.ExperienceLevel.class, experienceLevel);
        Job.JobType jt = parseEnum(Job.JobType.class, jobType);

        Page<Job> jobs = jobService.searchJobs(cat, exp, jt, keyword, page, size);

        model.addAttribute("jobs", jobs);
        model.addAttribute("categories", Job.Category.values());
        model.addAttribute("experienceLevels", Job.ExperienceLevel.values());
        model.addAttribute("jobTypes", Job.JobType.values());
        model.addAttribute("keyword", keyword);
        model.addAttribute("selectedCategory", category);
        model.addAttribute("selectedExp", experienceLevel);
        model.addAttribute("selectedType", jobType);
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", jobs.getTotalPages());

        if (auth != null) {
            User user = userService.findByEmail(auth.getName());
            model.addAttribute("currentUser", user);
        }
        return "jobs/list";
    }

    @GetMapping("/{id}")
    public String viewJob(@PathVariable Long id, Model model, Authentication auth) {
        Job job = jobService.findById(id);
        jobService.incrementViews(id);

        model.addAttribute("job", job);
        model.addAttribute("applicationCount", applicationService.countByJob(job));

        if (auth != null) {
            User user = userService.findByEmail(auth.getName());
            model.addAttribute("currentUser", user);
            model.addAttribute("hasApplied", applicationService.hasApplied(job, user));
        }
        return "jobs/detail";
    }

    private <T extends Enum<T>> T parseEnum(Class<T> enumClass, String value) {
        if (value == null || value.isEmpty()) return null;
        try { return Enum.valueOf(enumClass, value); }
        catch (IllegalArgumentException e) { return null; }
    }
}
