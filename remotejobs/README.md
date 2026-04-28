# ⚡ RemoteWork Hub — Remote Job Portal

A full-stack Spring Boot remote job portal with Job Seeker, Employer, and Admin roles.

---

## 🚀 Quick Start

### Prerequisites
- Java 17+
- Maven 3.8+
- MySQL 8.x (running on localhost:3306)
- IntelliJ IDEA or VS Code

---

## 📦 Setup Steps

### Step 1 — Create the Database
Open MySQL Workbench and run:
```sql
CREATE DATABASE IF NOT EXISTS remotejobs_db;
```
Or just run the included `database-setup.sql` file.

### Step 2 — Verify DB Credentials
Check `src/main/resources/application.properties`:
```
spring.datasource.username=root
spring.datasource.password=pra12@vin
```

### Step 3 — Run the Application
```bash
cd remote-job-portal
mvn spring-boot:run
```

Or in IntelliJ: Open project → Run `RemoteJobPortalApplication.java`

### Step 4 — Open in Browser
```
http://localhost:8080
```

---

## 🔑 Default Login Accounts

| Role       | Email                        | Password     |
|------------|------------------------------|--------------|
| Admin      | admin@remotejobs.com         | admin123     |
| Employer   | employer@techcorp.com        | employer123  |
| Job Seeker | seeker@example.com           | seeker123    |

---

## 📱 Mobile Support
The app is fully responsive and works on all screen sizes.

---

## 🗂️ Project Structure

```
src/main/java/com/remotejobs/
├── config/          SecurityConfig, FileStorageConfig, DataInitializer
├── controller/      HomeController, AuthController, JobController,
│                    JobSeekerController, EmployerController, AdminController
├── dto/             UserRegistrationDto
├── entity/          User, Job, Application, Notification
├── exception/       GlobalExceptionHandler, ResourceNotFoundException
├── repository/      UserRepository, JobRepository, ApplicationRepository, NotificationRepository
├── security/        CustomUserDetailsService
└── service/         UserService, JobService, ApplicationService,
                     NotificationService, FileStorageService

src/main/resources/
├── templates/
│   ├── auth/        login, register, register-jobseeker, register-employer
│   ├── jobs/        list, detail
│   ├── jobseeker/   dashboard, profile, apply, applications, notifications
│   ├── employer/    dashboard, jobs, job-form, applicants, profile, notifications
│   ├── admin/       dashboard, users, jobs
│   └── fragments/   layout (navbar, footer, head)
└── static/
    ├── css/style.css
    └── js/main.js
```

---

## ✅ Features

### Job Seeker
- Register / Login
- Browse & filter remote jobs (keyword, category, experience, type)
- View job details
- Apply with cover letter + resume upload
- Track application status (Pending → Shortlisted → Hired)
- Notifications on status changes
- Edit profile with resume upload

### Employer
- Register / Login
- Post / Edit / Delete job listings
- View all applicants per job
- Update application status with notes
- Shortlist / Reject / Hire candidates
- Auto-notification sent to applicant on status change

### Admin
- View platform stats (users, jobs, applications)
- Manage all users (activate/ban/delete)
- Manage all jobs (toggle active/featured/delete)

---

## 🛠️ Tech Stack

| Layer      | Technology                        |
|------------|-----------------------------------|
| Backend    | Spring Boot 3.2, Spring MVC       |
| Security   | Spring Security 6, BCrypt         |
| Database   | MySQL 8, Spring Data JPA          |
| Frontend   | Thymeleaf, HTML5, CSS3, JS ES6    |
| Fonts      | Syne + DM Sans (Google Fonts)     |
| Icons      | FontAwesome 6                     |
| File Upload| Spring MultipartFile              |

---

## 📧 Email Notifications (Optional)
To enable email alerts, update `application.properties`:
```
spring.mail.username=your-gmail@gmail.com
spring.mail.password=your-app-password
```
Generate an App Password at: https://myaccount.google.com/apppasswords
