package com.remotejobs.controller;

import com.remotejobs.dto.UserRegistrationDto;
import com.remotejobs.entity.User;
import com.remotejobs.repository.UserRepository;
import com.remotejobs.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class MobileAuthController {

    @Autowired private UserService userService;
    @Autowired private UserRepository userRepository;
    @Autowired private AuthenticationManager authenticationManager;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Map<String, String> req, HttpServletRequest request) {
        try {
            Authentication auth = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(req.get("email"), req.get("password"))
            );
            SecurityContextHolder.getContext().setAuthentication(auth);
            // Store in session
            HttpSession session = request.getSession(true);
            session.setAttribute("SPRING_SECURITY_CONTEXT", SecurityContextHolder.getContext());

            User user = userRepository.findByEmail(req.get("email"))
                .orElseThrow(() -> new RuntimeException("User not found"));
            return ResponseEntity.ok(buildUserResponse(user));
        } catch (Exception e) {
            return ResponseEntity.status(401).body(Map.of("message", "Invalid email or password"));
        }
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody Map<String, String> req) {
        try {
            UserRegistrationDto dto = new UserRegistrationDto();
            dto.setFullName(req.getOrDefault("name", req.getOrDefault("fullName", "")));
            dto.setEmail(req.get("email"));
            dto.setPassword(req.get("password"));
            dto.setConfirmPassword(req.get("password"));

            String roleStr = req.getOrDefault("role", "JOBSEEKER").toUpperCase();
            if (roleStr.equals("EMPLOYER")) {
                dto.setRole(User.Role.ROLE_EMPLOYER);
            } else {
                dto.setRole(User.Role.ROLE_JOBSEEKER);
            }

            userService.registerUser(dto);
            User user = userRepository.findByEmail(req.get("email"))
                .orElseThrow(() -> new RuntimeException("Registration failed"));
            return ResponseEntity.ok(buildUserResponse(user));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of("message", "Registration failed: " + e.getMessage()));
        }
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout(HttpServletRequest request) {
        SecurityContextHolder.clearContext();
        HttpSession session = request.getSession(false);
        if (session != null) session.invalidate();
        return ResponseEntity.ok(Map.of("message", "Logged out"));
    }

    @GetMapping("/me")
    public ResponseEntity<?> me() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated() || auth.getPrincipal().equals("anonymousUser")) {
            return ResponseEntity.status(401).body(Map.of("message", "Not authenticated"));
        }
        String email = ((UserDetails) auth.getPrincipal()).getUsername();
        User user = userRepository.findByEmail(email).orElse(null);
        if (user == null) return ResponseEntity.status(404).body(Map.of("message", "User not found"));
        return ResponseEntity.ok(buildUserResponse(user));
    }

    private Map<String, Object> buildUserResponse(User user) {
        Map<String, Object> resp = new HashMap<>();
        resp.put("id", user.getId());
        resp.put("name", user.getFullName());
        resp.put("email", user.getEmail());
        resp.put("role", user.getRole().name().replace("ROLE_", "").toLowerCase());
        resp.put("phone", user.getPhone());
        resp.put("skills", user.getSkills());
        resp.put("bio", user.getProfileSummary());
        resp.put("linkedinUrl", user.getLinkedinUrl());
        resp.put("companyName", user.getCompanyName());
        return resp;
    }
}
