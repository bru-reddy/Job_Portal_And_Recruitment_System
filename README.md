# Job Portal & Recruitment System

JobSphere is a Java full-stack recruitment-system project with separate candidate and company workflows.

## Implemented workflow

- Candidate registration and login.
- Candidate dashboard with available jobs, applications, selected/not-selected counts and company responses.
- Structured job application form covering name, email, phone, education, experience, skills and cover letter.
- PDF resume upload with a 5 MB limit.
- Resume stored with the application and available to the matching company workspace.
- Company registration with company profile fields.
- Company authentication code required for company registration and login: **1029**.
- Company dashboard with applicant profiles, PDF resume download, Accept and Reject actions.
- Candidate receives the hiring decision in the dashboard:
  - Selected: "You are selected for this role. For more details, contact the company."
  - Rejected: "You are not selected for this role."
- Company job posting from the authenticated company workspace.
- Company portfolio pages for established companies including Microsoft, Google, Amazon, Apple, Meta, NVIDIA, IBM, Accenture, Infosys, TCS, Oracle and Adobe.
- Job listings are simulated for this project. Applying through JobSphere never submits an application to a real company.
- Company portfolio pages link to the companies' official websites/careers pages for real-world information.

## Architecture

- Frontend: React + Vite
- Backend: Spring Boot 3 + Java 17
- Database: PostgreSQL
- Security: Spring Security + BCrypt
- Resume handling: multipart PDF upload stored with the application
- Deployment target: Render frontend + backend + PostgreSQL as separate services

## Demo boundary

This repository is a recruitment-system simulation. The company portfolio content and seeded job listings represent public companies, but JobSphere is not affiliated with them and does not transmit candidate applications to them. The application/review/decision workflow is internal to this project.

## UI

The interface uses a modern blue visual system with white surfaces, subtle gradients, responsive cards, status badges, candidate/company dashboards and mobile layouts.
