package com.remotejobs.config;

import com.remotejobs.entity.Job;
import com.remotejobs.entity.User;
import com.remotejobs.repository.JobRepository;
import com.remotejobs.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class DataInitializer implements CommandLineRunner {

    @Autowired private UserRepository userRepository;
    @Autowired private JobRepository jobRepository;
    @Autowired private PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {
        // Create Admin
        if (!userRepository.existsByEmail("admin@remotejobs.com")) {
            User admin = new User();
            admin.setFullName("Admin");
            admin.setEmail("admin@remotejobs.com");
            admin.setPassword(passwordEncoder.encode("admin123"));
            admin.setRole(User.Role.ROLE_ADMIN);
            admin.setEnabled(true);
            userRepository.save(admin);
            System.out.println("✅ Admin created: admin@remotejobs.com / admin123");
        }

        // Create Sample Employer
        if (!userRepository.existsByEmail("employer@techcorp.com")) {
            User employer = new User();
            employer.setFullName("TechCorp HR");
            employer.setEmail("employer@techcorp.com");
            employer.setPassword(passwordEncoder.encode("employer123"));
            employer.setRole(User.Role.ROLE_EMPLOYER);
            employer.setCompanyName("TechCorp Solutions");
            employer.setCompanyWebsite("https://techcorp.com");
            employer.setEnabled(true);
            User savedEmployer = userRepository.save(employer);

            // Sample Jobs
            if (jobRepository.count() == 0) {
                createSampleJob(savedEmployer, "Senior Java Developer",
                    "We are looking for an experienced Java developer to join our remote team. You will work on enterprise-grade applications using Spring Boot, microservices, and cloud technologies.",
                    "Java, Spring Boot, Microservices, AWS, Docker, Kubernetes",
                    Job.Category.SOFTWARE_DEVELOPMENT, Job.JobType.FULL_TIME, Job.ExperienceLevel.SENIOR_LEVEL,
                    "$90,000 - $120,000/year", true);

                createSampleJob(savedEmployer, "React Frontend Developer",
                    "Join our growing frontend team. Build responsive, modern UIs using React, TypeScript and GraphQL for our SaaS platform.",
                    "React, TypeScript, GraphQL, CSS, HTML5, REST APIs",
                    Job.Category.SOFTWARE_DEVELOPMENT, Job.JobType.FULL_TIME, Job.ExperienceLevel.MID_LEVEL,
                    "$70,000 - $90,000/year", true);

                createSampleJob(savedEmployer, "Data Scientist - ML Engineer",
                    "Help us build AI-powered analytics for our customers. Experience with Python, TensorFlow and real-world ML projects required.",
                    "Python, TensorFlow, PyTorch, Machine Learning, SQL, Data Analysis",
                    Job.Category.DATA_SCIENCE, Job.JobType.FULL_TIME, Job.ExperienceLevel.SENIOR_LEVEL,
                    "$100,000 - $130,000/year", false);

                createSampleJob(savedEmployer, "UX/UI Designer",
                    "Design beautiful, user-centric interfaces for web and mobile products. Work closely with product and engineering teams.",
                    "Figma, Adobe XD, User Research, Prototyping, CSS",
                    Job.Category.DESIGN, Job.JobType.CONTRACT, Job.ExperienceLevel.MID_LEVEL,
                    "$60/hour", false);

                System.out.println("✅ Sample jobs created");
            }
            System.out.println("✅ Sample employer created: employer@techcorp.com / employer123");
        }

        // Create Sample Job Seeker
        if (!userRepository.existsByEmail("seeker@example.com")) {
            User seeker = new User();
            seeker.setFullName("Praveen Kumar");
            seeker.setEmail("seeker@example.com");
            seeker.setPassword(passwordEncoder.encode("seeker123"));
            seeker.setRole(User.Role.ROLE_JOBSEEKER);
            seeker.setSkills("Java, Spring Boot, React, MySQL");
            seeker.setTimezone("Asia/Kolkata");
            seeker.setYearsExperience(3);
            seeker.setEnabled(true);
            userRepository.save(seeker);
            System.out.println("✅ Sample seeker created: seeker@example.com / seeker123");
        }
    }

    private void createSampleJob(User employer, String title, String description,
                                  String skills, Job.Category category, Job.JobType jobType,
                                  Job.ExperienceLevel experience, String salary, boolean featured) {
        Job job = new Job();
        job.setTitle(title);
        job.setDescription(description);
        job.setCompanyName(employer.getCompanyName());
        job.setSkillsRequired(skills);
        job.setCategory(category);
        job.setJobType(jobType);
        job.setExperienceLevel(experience);
        job.setSalary(salary);
        job.setSalaryCurrency("USD");
        job.setTimezonePreference("Any");
        job.setLocationRequirement("Worldwide");
        job.setEmployer(employer);
        job.setActive(true);
        job.setFeatured(featured);
        jobRepository.save(job);
    }
}
