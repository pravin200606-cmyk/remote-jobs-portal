package com.remotejobs.controller;

import com.remotejobs.dto.UserRegistrationDto;
import com.remotejobs.entity.User;
import com.remotejobs.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class AuthController {

    @Autowired
    private UserService userService;

    @GetMapping("/login")
    public String loginPage(@RequestParam(required = false) String error,
                            @RequestParam(required = false) String logout,
                            Model model) {
        if (error != null) model.addAttribute("error", "Invalid email or password.");
        if (logout != null) model.addAttribute("message", "You have been logged out.");
        return "auth/login";
    }

    @GetMapping("/register")
    public String registerPage(Model model) {
        model.addAttribute("user", new UserRegistrationDto());
        return "auth/register";
    }

    @GetMapping("/register/jobseeker")
    public String registerJobseeker(Model model) {
        UserRegistrationDto dto = new UserRegistrationDto();
        dto.setRole(User.Role.ROLE_JOBSEEKER);
        model.addAttribute("user", dto);
        model.addAttribute("registerType", "jobseeker");
        return "auth/register-jobseeker";
    }

    @GetMapping("/register/employer")
    public String registerEmployer(Model model) {
        UserRegistrationDto dto = new UserRegistrationDto();
        dto.setRole(User.Role.ROLE_EMPLOYER);
        model.addAttribute("user", dto);
        model.addAttribute("registerType", "employer");
        return "auth/register-employer";
    }

    @PostMapping("/register/jobseeker")
    public String processJobseekerRegistration(@Valid @ModelAttribute("user") UserRegistrationDto dto,
                                               BindingResult result,
                                               RedirectAttributes redirectAttrs,
                                               Model model) {
        dto.setRole(User.Role.ROLE_JOBSEEKER);
        if (result.hasErrors()) {
            model.addAttribute("registerType", "jobseeker");
            return "auth/register-jobseeker";
        }
        try {
            userService.registerUser(dto);
            redirectAttrs.addFlashAttribute("success", "Account created! Please login.");
            return "redirect:/login";
        } catch (IllegalArgumentException e) {
            model.addAttribute("error", e.getMessage());
            model.addAttribute("registerType", "jobseeker");
            return "auth/register-jobseeker";
        }
    }

    @PostMapping("/register/employer")
    public String processEmployerRegistration(@Valid @ModelAttribute("user") UserRegistrationDto dto,
                                              BindingResult result,
                                              RedirectAttributes redirectAttrs,
                                              Model model) {
        dto.setRole(User.Role.ROLE_EMPLOYER);
        if (result.hasErrors()) {
            model.addAttribute("registerType", "employer");
            return "auth/register-employer";
        }
        try {
            userService.registerUser(dto);
            redirectAttrs.addFlashAttribute("success", "Employer account created! Please login.");
            return "redirect:/login";
        } catch (IllegalArgumentException e) {
            model.addAttribute("error", e.getMessage());
            model.addAttribute("registerType", "employer");
            return "auth/register-employer";
        }
    }
}
