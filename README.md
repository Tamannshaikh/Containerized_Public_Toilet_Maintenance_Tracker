# Containerized Public Toilet Maintenance Tracker

An end-to-end DevOps automated public toilet maintenance and hygiene tracking system, integrating Agile planning, continuous integration with Jenkins, automated Selenium UI testing, multi-stage Docker containerization, and continuous delivery.

---

## Academic Project Details

- **Student Name:** Tamanna Shaikh
- **Roll Number:** 24102A2006
- **Department:** Computer Engineering
- **Institute:** Vidyalankar Institute of Technology (VIT Mumbai)
- **Course:** DevOps Laboratory
- **Academic Year:** 2026–2027

---

## 1. Project Purpose & Problem Statement

### Purpose
The **Containerized Public Toilet Maintenance Tracker** delivers a lightweight, reliable, and continuously validated municipal sanitation monitoring platform. It eliminates paper-based inspection records, provides real-time facility visibility, expedites maintenance defect resolution, and showcases an automated 8-phase DevOps continuous delivery pipeline.

### Problem Statement
Urban public sanitation facilities suffer from untracked hygiene failures (water outages, fixture damage, foul odor) and delayed municipal responses. Traditional maintenance relies on informal complaints with zero operational accountability. Citizens lack transparent facility ratings, while municipal supervisors lack unified dashboards to monitor cleaning compliance and enforce 4-hour SLA remediation windows.

---

## 2. Core Functional Scope: The Five MVP Features

| Feature Module | Scope Description | Implementation Status | Target Task |
|---|---|:---:|:---:|
| **MVP 1: Maintenance Data / Event Entry** | Digital logging of completed cleaning shifts with checklists; citizen defect reporting form. | **Planned** | Task 05 |
| **MVP 2: Searchable Dashboard** | Real-time tabular interface with multi-parameter filtering across Wards, Status, and Severity. | **Planned** | Task 05–06 |
| **MVP 3: Summary Indicators** | 4 operational KPI summary cards (Total Facilities, Active Tickets, SLA Breaches, Avg Cleanliness). | **Planned** | Task 05–06 |
| **MVP 4: Status Drill-Down** | Detailed facility inspection modal displaying historical timeline and status transitions (`OPEN` $\rightarrow$ `IN_PROGRESS` $\rightarrow$ `RESOLVED`). | **Planned** | Task 05–06 |
| **MVP 5: Alert / Exception View** | Automated flagging of critical hazards (zero water supply, hygiene score $< 40\%$) and SLA overdue escalation. | **Planned** | Task 05–06 |

---

## 3. Technology Stack

- **Backend Framework:** Spring Boot 3.2.4 (Java 17 LTS / JDK 26 runtime)
- **Data Persistence:** Spring Data JPA / Hibernate; In-Memory H2 (Unit Testing); PostgreSQL 15 Alpine (Production Runtime)
- **Build & Package Tool:** Apache Maven 3.9.16
- **Testing Frameworks:** JUnit 5, Mockito, Selenium WebDriver 4.19.1
- **Container Engine:** Docker 29.8.2 (Multi-stage build, unprivileged user `appuser`)
- **Container Orchestration:** Docker Compose 3.8
- **Continuous Integration (CI):** Jenkins (Declarative Pipeline)
- **Version Control:** Git 2.52.0 & GitHub (`origin/main`)

---

## 4. Current Project Directory Structure

```text
Containerized_Public_Toilet_Maintenance_Tracker/
├── .github/
│   └── ISSUE_TEMPLATE/
│       ├── bug_report.md             # Issue template for defect reporting
│       └── feature_request.md        # Issue template for feature proposals
├── .gitignore                        # Git ignore configuration
├── .vscode/
│   └── settings.json                 # IDE Java compilation settings
├── Dockerfile                        # Multi-stage container definition
├── docker-compose.yml                # Multi-service stack (App + PostgreSQL)
├── Jenkinsfile                       # Declarative CI/CD pipeline script
├── pom.xml                           # Maven dependencies & build lifecycle
├── README.md                         # Project documentation and setup guide
├── datasets/
│   └── .gitkeep                      # Municipal ward and facility seed datasets
├── docs/
│   ├── architecture/
│   │   └── .gitkeep                  # System architecture specifications
│   ├── diagrams/
│   │   ├── devops-lifecycle.svg      # 8-phase DevOps feedback loop vector diagram
│   │   ├── system-architecture.svg   # 4-tier system architecture vector diagram
│   │   └── use-case-diagram.svg      # 5-actor system use-case vector diagram
│   └── screenshots/
│       ├── Task-01/ through Task-15/ # Verified task execution evidence
├── frontend/
│   └── .gitkeep                      # Single-page web dashboard assets
├── REPORTS/
│   ├── Task-01-Problem-Definition.md # Comprehensive Task 01 report
│   ├── Task-02-Agile-Planning.md     # Comprehensive Task 02 report
│   ├── Task-03-Architecture-Setup.md # Comprehensive Task 03 report
│   └── Task-04-Git-GitHub.md through Task-15-Final-Release.md
└── src/
    ├── main/
    │   ├── java/com/tracker/toilet/
    │   │   ├── HealthController.java # Operational health status REST probe
    │   │   └── ToiletMaintenanceApplication.java # Spring Boot entry point
    │   └── resources/
    │       └── application.properties # Spring Boot H2 test configuration
    └── test/
        └── java/com/tracker/toilet/
            └── ToiletMaintenanceApplicationTests.java # Context load test
```

---

## 5. Local Setup & Quick Start

### 5.1 Prerequisites
- **JDK 17 LTS or higher** (`java --version`)
- **Apache Maven 3.8+** (`mvn --version`)
- **Git 2.40+** (`git --version`)
- **Docker Desktop** (Running for containerized execution)

### 5.2 Build & Test
1. Clone the repository and navigate to the root directory:
   ```powershell
   git clone https://github.com/Tamannshaikh/Containerized_Public_Toilet_Maintenance_Tracker.git
   cd Containerized_Public_Toilet_Maintenance_Tracker
   ```
2. Run unit tests against the in-memory H2 database:
   ```powershell
   mvn clean test
   ```
3. Compile and package the executable JAR artifact:
   ```powershell
   mvn clean package -DskipTests
   ```
   *Generated output:* `target/public-toilet-maintenance-tracker-1.0.0-SNAPSHOT.jar`

---

## 6. Available REST API Endpoints

### Currently Implemented & Verified
- `GET /api/v1/health`: Returns system operational status.
  ```json
  {
    "service": "Containerized Public Toilet Maintenance Tracker",
    "status": "UP",
    "version": "1.0.0",
    "timestamp": 1729248309618
  }
  ```

### Planned Domain Endpoints (Task 05)
- `GET /api/v1/facilities`: Search and filter facilities by Ward, Status, and Keyword.
- `GET /api/v1/facilities/{id}`: Detailed inspection profile of a single facility.
- `POST /api/v1/facilities`: Register a new facility.
- `GET /api/v1/tickets`: List complaints filtered by status and severity.
- `POST /api/v1/tickets`: Log citizen defect complaint.
- `PATCH /api/v1/tickets/{id}/status`: Update ticket lifecycle state (`OPEN` $\rightarrow$ `IN_PROGRESS` $\rightarrow$ `RESOLVED`).
- `POST /api/v1/cleaning-logs`: Record completed cleaning shift checklist.
- `GET /api/v1/kpis/summary`: Operational metric cards.
- `GET /api/v1/alerts/exceptions`: Overdue SLA tickets ($> 4\text{ hours}$) and water supply hazard flags.

---

## 7. Docker & Jenkins CI/CD Workflow

### Multi-Stage Container Packaging
[`Dockerfile`](./Dockerfile) executes a 2-stage build:
1. **Builder Stage (`maven:3.9-eclipse-temurin-17`):** Resolves dependencies offline, compiles Java source code, and packages the JAR.
2. **Runtime Stage (`eclipse-temurin:17-jre-alpine`):** Copies only the built JAR, sets up an unprivileged user `appuser` (UID 1000) for security, and exposes port `8080`.

### CI/CD Pipeline
[`Jenkinsfile`](./Jenkinsfile) orchestrates 6 declarative stages:
1. **Checkout:** Checks out source code from Git repository.
2. **Code Compile & Unit Test:** Executes `mvn clean test`.
3. **Package Application:** Packages JAR artifact and archives it.
4. **Selenium UI Testing:** Executes automated browser tests.
5. **Docker Build:** Builds container image `public-toilet-maintenance-tracker:${BUILD_NUMBER}`.
6. **Docker CD & Deployment:** Executes `docker compose up -d` on target environment.

---

## 8. Current Limitations & Planned Work

- **Host Port 8080 Notice:** Jenkins runs as a Windows Service on host port `8080`. Running Docker Compose on the host requires remapping the application host port to `8081` (`8081:8080`) to avoid port binding conflicts.
- **Data Model:** Full Spring Data JPA entities and repositories are scheduled for implementation in **Task 05**.
- **Web Frontend:** Dashboard UI assets in `frontend/` are scheduled for **Task 06**.
- **Testing:** Selenium UI test suite is scheduled for **Task 09**.
