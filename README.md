# Job Portal & Recruitment System

JobSphere is a Java full-stack recruitment-system project with separate Candidate, Recruiter and Company workspaces.

## Core workflow

- Candidate registration/login and candidate-only dashboard.
- Candidate navigation: Find Jobs, Companies, Build Your Skills and Dashboard.
- Structured applications containing candidate details, education, experience, skills and additional information.
- No PDF/resume processing.
- Candidate applications are recorded inside JobSphere only.
- Recruiter registration/login and recruiter-only workspace.
- Recruiters post jobs, define required skills and eligibility criteria, and review applicants.
- Company registration/login and company-only read-only workspace.
- Companies can view recruiter-posted roles, eligibility criteria and applicants for their organization.
- Company selection, rejection, interviews and internal communication happen externally and are deliberately outside JobSphere.
- No fake in-portal hiring decisions are presented to candidates.

## Build Your Skills

The application includes a dedicated Build Your Skills navigation item for every workspace.

Users can choose:
- Learn
- Practice

Then they choose a field such as:
- Technology
- Science
- Business
- Design

Technology includes Java, Python, C++, Data Structures & Algorithms, Operating Systems, Computer Networks, DBMS & SQL, Compiler Design, Big Data, and Machine Learning & AI.

Each topic opens a curated external learning or practice resource in a new tab.

## Company directory

JobSphere includes informational profiles for established companies such as Microsoft, Google, Amazon, Apple, Meta, NVIDIA, IBM, Accenture, Infosys, TCS, Oracle and Adobe. Their public websites/careers pages are linked from the company portfolio pages.

The seeded jobs are simulated data. JobSphere does not submit applications to the real companies.

## Architecture

- Frontend: React + Vite
- Backend: Spring Boot 3 + Java 17
- Database: PostgreSQL
- Security: Spring Security + BCrypt
- Deployment target: Render frontend + backend + PostgreSQL as separate services

## Demo authentication

Recruiter and Company accounts require the organization code **1029** during registration and login.

## Demo boundary

This repository is a recruitment-system simulation. It does not perform external hiring decisions, interviews, company communication or submissions to real companies.
