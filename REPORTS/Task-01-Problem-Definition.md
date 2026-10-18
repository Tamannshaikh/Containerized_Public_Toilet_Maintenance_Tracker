# Task 01: Problem Definition & Scope Specification

---

## Academic Project Metadata

- **Project Title:** Containerized Public Toilet Maintenance Tracker
- **Student Name:** Tamanna Shaikh
- **Roll Number:** 24102A2006
- **Department:** Computer Engineering
- **Course:** DevOps Laboratory
- **Academic Year:** 2026–2027
- **Project Directory:** `C:\Users\Sajiya\Desktop\Containerized_Public_Toilet_Maintenance_Tracker`
- **Current Task:** Task 01 – Problem Definition & Scope Specification
- **Document Version:** 1.0.0
- **Status:** Baseline Established (Pending Screenshot Evidence Capture)

---

## 1. Aim

To define a formal engineering baseline, problem definition, stakeholder scope, system requirements, measurable acceptance criteria, and DevOps lifecycle roadmap for the **Containerized Public Toilet Maintenance Tracker** web application and automated CI/CD infrastructure.

---

## 2. Problem Statement

Public sanitation infrastructure in urban hubs, transit stations, and municipal wards frequently deteriorates due to the absence of real-time monitoring, opaque maintenance schedules, and delayed fault escalation. Traditional maintenance systems rely heavily on manual paper logs, informal complaints, and periodic unscheduled inspections. Consequently:

1. **Hygiene Failures Go Unreported:** Critical deficiencies such as water supply disruption, broken fixtures, odor threshold violations, and lack of consumables (soap, paper) persist for days before municipal intervention.
2. **Lack of Maintenance Accountability:** Sanitation staff work without automated job dispatches, leading to irregular cleaning intervals and unverifiable service completion.
3. **No Operational Transparency:** Citizens and city administrators lack a single, real-time interface to inspect facility cleanliness scores, open incident tickets, or historical maintenance records.
4. **DevOps and Deployment Bottlenecks:** Traditional municipal software deployments are manual, brittle, tightly coupled to host environments, and vulnerable to downtime during updates, lacking automated testing, containerized packaging, and continuous delivery pipelines.

---

## 3. Background and Motivation

The "Clean Public Sanitation" initiative requires transparent, data-driven municipal administration. With rapid urbanization, municipal corporations oversee hundreds of public toilet blocks across multiple administrative wards. 

Transitioning from reactive maintenance to proactive, automated maintenance tracking requires:
- A modern, lightweight web-based tracker for facility health and maintenance requests.
- A fully automated DevOps pipeline that enforces continuous integration, automated quality assurance (unit and Selenium end-to-end testing), multi-stage containerization with Docker, and automated deployment with Docker Compose and Jenkins.

This project bridges software engineering and modern DevOps methodologies to deliver a reliable, portable, and continuously validated public health utility.

---

## 4. Target Users

1. **Municipal Sanitation Officers / Ward Administrators:** Oversee facility performance, monitor cleaning compliance, review alert thresholds, and audit SLA metrics.
2. **Facility Maintenance Staff / Field Cleaners:** Log completed cleaning rounds, record supply replenishments, and update repair statuses.
3. **Citizen Commuters / Public Users:** Access facility status dashboards, check hygiene ratings, and report urgent maintenance issues (e.g., plumbing leaks, foul odor).
4. **DevOps Engineers / System Operators:** Manage containerized environments, build pipelines, test suites, and ensure high system reliability and zero-downtime rollouts.

---

## 5. Stakeholder Analysis

| Stakeholder Role | Primary Responsibilities | Key Expectations & Interests | Impact on System Design |
|---|---|---|---|
| **Municipal Administration** | Budget allocation, ward hygiene compliance, municipal policy enforcement | Real-time reporting, accountability audit trails, SLA compliance data | Dashboard summary indicators, exportable reports, uptime |
| **Field Maintenance Supervisors** | Assigning daily cleaning routes, verifying completed tickets, supply tracking | Fast issue triage, mobile-accessible web UI, clear facility drill-down | Searchable dashboard, priority flags, status drill-down views |
| **Sanitation Cleaning Staff** | Executing physical cleaning, logging completion events, flagging major repairs | Simple, frictionless data entry without complex forms | Minimalist maintenance event entry form, rapid status toggle |
| **Citizens / General Public** | Reporting sanitation breakdown, finding clean nearby facilities | Transparent cleanliness score, quick fault submission | Public status dashboard, instant complaint submission |
| **DevOps & QA Engineers** | Maintaining CI/CD pipeline, Docker containers, automated tests | Repeatable builds, automated regression testing, zero configuration drift | Multi-stage Dockerfile, Jenkins pipeline, Selenium tests, Ansible automation |

---

## 6. Existing Maintenance-Related Pain Points

1. **Paper-Based Register Lag:** Manual logbooks maintained at facility entrances are prone to manipulation, physical damage, and cannot provide real-time alerts to municipal managers.
2. **High Mean Time to Repair (MTTR):** Citizens have no direct digital channel to escalate plumbing or hygiene failures; issues often take 48–72 hours to reach maintenance teams.
3. **Absence of Unified Metrics:** Supervisors have no centralized dashboard to compare facility cleanliness across wards or identify chronic failure points.
4. **Manual & Unreliable Deployments:** System upgrades traditionally require manual file copying, manual database migrations, and manual server restarts, introducing human error and unpredictable downtime.
5. **Lack of Automated Testing:** Code changes to municipal portals often break existing features due to absent regression and end-to-end acceptance tests.

---

## 7. Functional Requirements

### Essential MVP Features (Proposed & Planned)

The Minimum Viable Product (MVP) encompasses five core functional modules:

1. **Maintenance Data / Event Entry (MVP Feature 1):**
   - Provide a clean form to record cleaning events and maintenance incidents.
   - Captures Facility ID, Ward Number, Incident Type (Plumbing, Electrical, Sanitation, Consumables, Structural), Severity Level (Low, Medium, High, Critical), Logged By (Staff ID / Citizen), and Description.
   - Initial ticket status set to `OPEN`.

2. **Searchable Dashboard (MVP Feature 2):**
   - Central table displaying all registered public toilet facilities and active maintenance tickets.
   - Multi-criteria real-time search and filtering by Facility Name, Ward/Location, Status (`OPEN`, `IN_PROGRESS`, `RESOLVED`), and Incident Severity.

3. **Summary Indicators (MVP Feature 3):**
   - Top-level operational KPI cards displaying:
     - Total Monitored Facilities
     - Total Active / Open Complaints
     - Critical Facilities Pending Action (> 4 Hours)
     - Average Hygiene Rating across the municipality

4. **Status Drill-Down (MVP Feature 4):**
   - Detailed modal/view for each facility or maintenance ticket.
   - Displays inspection timeline, assigned cleaning crew, incident history, last cleaned timestamp, and resolution notes.
   - Allows maintenance staff to update status to `IN_PROGRESS` and `RESOLVED`.

5. **Alert / Exception View (MVP Feature 5):**
   - Filtered view highlighting facilities requiring immediate administrative intervention:
     - Water supply failure alerts.
     - Critical sanitary hazard flags.
     - Tickets exceeding the standard 4-hour SLA without resolution.

---

## 8. Non-Functional Requirements

| Category | Requirement Specification | Metric / Target |
|---|---|---|
| **Portability & Containerization** | Application and database must run identically across development, CI, and production environments using Docker and Docker Compose. | 100% containerized execution; single-command startup (`docker compose up`). |
| **Performance & Latency** | Web interface page load and REST API data retrieval must execute swiftly on local networks. | API response time $< 200\text{ ms}$ for standard queries; page load $< 1.5\text{ s}$. |
| **Availability & Reliability** | The container stack must feature automated health checks and restart policies to survive process crashes. | Docker container healthcheck passes; `restart: always` configured. |
| **Maintainability & Code Quality** | Clean, modular Spring Boot architecture with clear separation of Controller, Service, Repository, and Model layers. | Zero circular dependencies; full Maven build without compilation warnings. |
| **Automated Testing** | Comprehensive test coverage encompassing unit tests (JUnit 5, Mockito) and UI acceptance tests (Selenium WebDriver). | 100% test pass rate in CI pipeline prior to deployment packaging. |
| **Zero Host Port Collision** | Application host port must not collide with existing host services (e.g., Jenkins running on port 8080). | Application configured on Host Port `8081` mapped to Container Port `8080`. |

---

## 9. Project Objectives

1. **System Objective:** Design and develop a robust, lightweight Spring Boot web application for tracking public toilet maintenance, providing the 5 essential MVP features.
2. **DevOps CI Objective:** Implement Continuous Integration using Jenkins to automatically check out code, compile, execute JUnit tests, package JAR artifacts, and record test results upon every Git push.
3. **Automated Testing Objective:** Implement automated Selenium WebDriver end-to-end tests to validate UI workflows (incident creation, status filtering, drill-down modal) within the headless CI build.
4. **Containerization Objective:** Package the application into a lightweight, secure, multi-stage Docker container utilizing an unprivileged non-root user (`appuser`).
5. **Continuous Deployment Objective:** Automate multi-container deployment (Spring Boot App + PostgreSQL Database) using Docker Compose orchestrated directly through the Jenkins pipeline.
6. **Infrastructure as Code Objective:** Demonstrate automated environment provisioning and configuration management using Ansible / Puppet playbooks.
7. **Reliability Objective:** Implement container health check probes, restart policies, and automated PostgreSQL backup/restore procedures.

---

## 10. Technical and Operational Constraints

1. **Host Environment:** Single local physical development machine running Windows 11 with PowerShell 7 / Windows PowerShell.
2. **Active Port Allocation:** Jenkins is already installed as a host Windows Service occupying Port `8080`. The Spring Boot application must be mapped to Port `8081` to avoid binding collisions.
3. **Docker Daemon Dependency:** Docker Desktop must be running for container builds and Docker Compose orchestration.
4. **Ansible Execution Environment:** Ansible requires a POSIX-compliant environment, necessitating execution through WSL 2 (Ubuntu) on Windows.
5. **Offline / Academic Scope:** Cloud hosting costs are restricted; all CI/CD, database instances, and application containers operate on a local virtualized loopback network (`localhost`).

---

## 11. Assumptions

1. Initial test datasets and public toilet facility locations will be pre-loaded via seed scripts representing municipal wards.
2. User authentication and role-based access control (RBAC) are simulated or kept minimal for MVP velocity to prioritize DevOps pipeline automation over complex multi-user permission systems.
3. Physical IoT odor/water sensors are simulated via REST API JSON payload injections.
4. System testing is executed against Google Chrome / Microsoft Edge WebDriver in headless mode for Jenkins CI compatibility.

---

## 12. Measurable Success & Acceptance Criteria

| Criteria ID | Requirement / Target | Measurement Method | Expected Acceptance Threshold | Verification Method | Current Status |
|---|---|---|---|---|---|
| **AC-01** | Architecture & Scaffolding | Filesystem audit of directory structure and build configs | Maven POM, Dockerfile, Compose, Jenkinsfile present and syntax-valid | Read-only CLI check & Maven syntax validation | Verified Ready (Scaffold exists) |
| **AC-02** | Maintenance Entry (MVP 1) | Form submission test with valid incident payload | Ticket created with HTTP 201 Created and persisted in DB | REST API POST test and UI form submit test | Proposed (Planned Task 5) |
| **AC-03** | Searchable Dashboard (MVP 2) | UI filter and search query execution | Results update dynamically in $< 300\text{ ms}$; filters match status exactly | Selenium WebDriver UI assertion | Proposed (Planned Task 6 & 9) |
| **AC-04** | Summary KPI Indicators (MVP 3) | Counter calculations against database records | Total, Open, Critical counts exactly equal active DB state | Unit test assertion & UI DOM value check | Proposed (Planned Task 5 & 6) |
| **AC-05** | Status Drill-Down (MVP 4) | Modal expansion upon facility row selection | Full facility history, cleaner ID, and timestamp rendered | Selenium WebDriver modal visibility check | Proposed (Planned Task 6 & 9) |
| **AC-06** | Alert / Exception View (MVP 5) | Query for tickets with severity `CRITICAL` or age $> 4\text{ h}$ | 100% of breached tickets displayed with alert badge | Automated query test & UI test | Proposed (Planned Task 5 & 6) |
| **AC-07** | Unit Test Quality Gate | Execution of `mvn test` in Maven lifecycle | $\ge 80\%$ statement coverage on service/controller logic; 0 failures | Maven Surefire XML report generated | Proposed (Planned Task 5 & 8) |
| **AC-08** | Automated UI Acceptance Gate | Headless Selenium test execution during CI | 100% passing automated UI test scenarios; 0 broken selectors | Jenkins JUnit test result archive | Proposed (Planned Task 9 & 10) |
| **AC-09** | Secure Container Packaging | Multi-stage Docker image build | Final image size $< 250\text{ MB}$; process runs under non-root `appuser` | `docker inspect` & security audit | Proposed (Planned Task 11) |
| **AC-10** | Continuous Deployment Health | Multi-container stack startup via Docker Compose | Database healthcheck returns healthy; app responds HTTP 200 on port 8081 | `curl -f http://localhost:8081/actuator/health` | Proposed (Planned Task 12 & 14) |

*Note: The current status of all application features is clearly marked as **Proposed** until verified in their respective implementation tasks.*

---

## 13. Scope Baseline

### In-Scope Features
- Responsive web dashboard for public toilet maintenance tracking.
- The 5 core MVP functional features (Entry, Search/Filter, KPIs, Drill-Down, Alerts).
- Spring Boot 3.x backend exposing structured REST APIs.
- PostgreSQL 15 relational database for persistent storage.
- Multi-stage Docker containerization running with unprivileged user permissions.
- Docker Compose multi-container local orchestration (App + PostgreSQL).
- Jenkins declarative pipeline automating checkout, build, test, package, and container deployment.
- Headless Selenium WebDriver automated UI testing integrated into Jenkins.
- Ansible configuration management playbooks running via WSL.
- Automated database backup/recovery scripts and container restart reliability testing.
- Comprehensive task documentation and screenshot evidence for all 15 tasks.

### Out-of-Scope Features
- Commercial payment gateway integration for paid pay-and-use toilets.
- Native mobile application development (iOS / Android APKs); a responsive web app is utilized instead.
- Physical micro-controller / IoT sensor hardware assembly.
- Multi-region Kubernetes cluster deployment (single-node Docker orchestration fulfills college DevOps objectives).
- Public internet domain registration and external SSL/TLS certificate authority integration (localhost SSL/HTTP utilized).

---

## 14. Complete 15-Task DevOps Project Roadmap

| Task # | Task Title | Planned DevOps Focus & Implementation Milestone |
|---|---|---|
| **Task 01** | **Problem Definition & Scope** | Problem statement, stakeholder analysis, MVP scope definition, baseline environment audit. |
| **Task 02** | **Agile Planning & User Stories** | Epics, user stories, acceptance criteria, sprint planning, and Kanban backlog modeling. |
| **Task 03** | **Requirements & Architecture Setup** | 3-tier architecture design, tech stack specification, port mapping, and schema modeling. |
| **Task 04** | **Git & GitHub Repository Setup** | Local Git initialization, `.gitignore`, initial baseline commit, branching, and remote GitHub push. |
| **Task 05** | **Feature Development & Branching** | Spring Boot backend entities, JPA repositories, REST API controllers, and feature branches. |
| **Task 06** | **MVP Completion & Collaboration** | Web UI dashboard, form inputs, status drill-down, API integration, and branch merge into `main`. |
| **Task 07** | **Jenkins Installation & CI Setup** | Jenkins pipeline job configuration, Git integration, and automated Maven compile & test trigger. |
| **Task 08** | **Pipeline as Code & Artifact Archiving** | Enhanced `Jenkinsfile`, automated JAR packaging, fingerprinting, and artifact archiving. |
| **Task 09** | **Selenium Automated Testing** | Selenium WebDriver test design, UI workflow validation, and local headless browser execution. |
| **Task 10** | **Continuous Testing in Jenkins** | Pipeline integration of Selenium tests, automated Surefire reporting, and test trend graphs. |
| **Task 11** | **Docker Containerization Lifecycle** | Multi-stage `Dockerfile`, non-root user setup, image build, and container lifecycle validation. |
| **Task 12** | **Docker Compose Continuous Deployment** | Multi-container stack (App + PostgreSQL), health checks, networking, and automated pipeline deploy. |
| **Task 13** | **Configuration Management (Ansible)** | Ansible playbook authoring, automated prerequisites provisioning, and idempotent deployment. |
| **Task 14** | **Provisioning & Reliability Engineering** | Actuator health checks, automated PostgreSQL backup/restore, and crash self-healing tests. |
| **Task 15** | **Final Release, Documentation & Viva** | End-to-end pipeline execution, consolidated report delivery, screenshot archive, and viva prep. |

---

## 15. Expected Project Deliverables

1. **Working Software:** A fully functional, containerized Spring Boot 3 web application and PostgreSQL database with the 5 MVP maintenance tracking features.
2. **Infrastructure as Code & CI/CD Pipelines:**
   - Production multi-stage [`Dockerfile`](../Dockerfile).
   - Local multi-service orchestration [`docker-compose.yml`](../docker-compose.yml).
   - Automated declarative [`Jenkinsfile`](../Jenkinsfile).
   - Ansible configuration management playbooks.
3. **Automated Test Suites:**
   - JUnit 5 / Spring Boot unit and integration tests.
   - Selenium WebDriver automated browser acceptance tests.
4. **Documentation & Academic Reports:**
   - 15 individual task completion reports in [`REPORTS/`](./).
   - System architecture diagrams in [`docs/architecture/`](../docs/architecture/).
   - Screenshot catalog in [`docs/screenshots/`](../docs/screenshots/).
   - Project Viva Voce defense presentation guide.

---

## 16. Procedure Executed for Task 01

1. **Step 1 – Project Directory & Scaffolding Inspection:** Inspected existing repository structure, files, build configuration (`pom.xml`), and report placeholders.
2. **Step 2 – Installed Environment Verification:** Executed read-only PowerShell commands to verify Java (JDK 26), Maven (3.9.16), Git (2.52.0), Docker (29.8.2), and Jenkins service availability.
3. **Step 3 – Domain Realignment:** Formulated the formal problem statement, background, stakeholder matrix, and functional specifications for the *Containerized Public Toilet Maintenance Tracker*, resolving the previous template discrepancy.
4. **Step 4 – MVP Scope Formulation:** Identified and specified the five essential MVP features (Entry, Search/Filter, KPIs, Drill-Down, Alerts).
5. **Step 5 – Acceptance Criteria & Metric Definition:** Established measurable thresholds for all functional and non-functional requirements.
6. **Step 6 – Documentation Generation:** Authored the complete, academically compliant Task 01 report in [`REPORTS/Task-01-Problem-Definition.md`](./Task-01-Problem-Definition.md).

---

## 17. Problems Encountered and Engineering Solutions

| Problem Encountered | Technical Root Cause | Engineered Solution |
|---|---|---|
| **Domain Mismatch in Scaffold** | Existing boilerplate template referenced "Public Parking Management". | Systematically replaced all scope definitions with the *Containerized Public Toilet Maintenance Tracker* domain requirements. |
| **Port 8080 Collision Risk** | Jenkins is actively running on host port `8080`, conflicting with default Spring Boot port. | Formally planned application host port remapping to Port `8081` in architecture and Docker Compose specs. |
| **Misplaced Git Root** | Git was initialized in `C:\Users\Sajiya` instead of the project directory. | Documented the need to initialize an isolated `.git` repository inside the project folder in Task 04. |

---

## 18. Verification Status & Evidence Checklist

- [x] Project Title, Student Name, Roll Number, and Department clearly stated.
- [x] Aim and comprehensive Problem Statement established.
- [x] Background, Motivation, and Target Users documented.
- [x] Stakeholder Analysis table with roles and expectations defined.
- [x] All 5 MVP Application Features explicitly specified and marked as planned.
- [x] Non-functional requirements, technical constraints, and assumptions detailed.
- [x] Measurable acceptance criteria table created with measurement methods and thresholds.
- [x] Full 15-task project roadmap documented.
- [x] Expected project deliverables categorized.
- [ ] Screenshot 1: `docs/screenshots/Task-01/task01_env_audit.png` *(Awaiting manual/guided terminal capture)*.
- [ ] Screenshot 2: `docs/screenshots/Task-01/task01_report_preview.png` *(Awaiting preview capture)*.

---

## 19. Artifacts & Screenshots Specification

**Task 01 Screenshot Evidence (Verification Log)**



All visual evidence for Task 01 must be saved in [`docs/screenshots/Task-01/`](../docs/screenshots/Task-01):

| Screenshot Filename | Exact Screen to Capture | Required Visual Elements |
|---|---|---|
| `task01_env_audit.png` | Windows PowerShell Terminal | Current directory path `C:\Users\Sajiya\Desktop\Containerized_Public_Toilet_Maintenance_Tracker`, Java version, Maven version, Git version, Docker version, and Jenkins service running. |
| `task01_report_preview.png` | Markdown Editor / Preview | Rendered view of `REPORTS/Task-01-Problem-Definition.md` showing Academic Project Metadata, Aim, Problem Statement, and Stakeholder Table. |

---

## 20. Result and Conclusion

Task 01 has established an academic and engineering baseline for the **Containerized Public Toilet Maintenance Tracker**. The problem statement, stakeholder analysis, 5 MVP functional modules, non-functional constraints, and the complete 15-task DevOps roadmap are clearly articulated. All acceptance criteria have measurable targets and are distinguished from actual test results to preserve academic integrity.

Upon capturing the two required verification screenshots, Task 01 documentation will be fully verified and ready for handoff to **Task 02: Agile Planning and User Stories**.