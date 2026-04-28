-- ============================================
-- RemoteWork Hub - MySQL Database Setup
-- Run this before starting the application
-- ============================================

-- Create database (Spring Boot will auto-create tables via JPA)
CREATE DATABASE IF NOT EXISTS remotejobs_db
  CHARACTER SET utf8mb4
  COLLATE utf8mb4_unicode_ci;

USE remotejobs_db;

-- Verify connection (optional check)
SELECT 'Database remotejobs_db created successfully!' AS Status;

-- ============================================
-- NOTE: Tables are auto-created by Spring Boot
-- (spring.jpa.hibernate.ddl-auto=update)
-- Run this SQL first, then start the app.
-- Sample data (admin, employer, jobs) is also
-- auto-seeded via DataInitializer.java
-- ============================================

-- Default login credentials (created on first run):
-- Admin:    admin@remotejobs.com  / admin123
-- Employer: employer@techcorp.com / employer123
-- Seeker:   seeker@example.com    / seeker123
