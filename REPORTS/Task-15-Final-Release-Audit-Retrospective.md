# Task 15: Final Release, Audit & Retrospective

---

## Academic Project Metadata

- **Project Title:** Containerized Public Toilet Maintenance Tracker
- **Student Name:** Tamanna Shaikh
- **Roll Number:** 24102A2006
- **Department:** Computer Engineering
- **Institute:** Vidyalankar Institute of Technology (VIT Mumbai)
- **Course:** DevOps Laboratory
- **Academic Year:** 2026–2027
- **Project Directory:** `C:\Users\Sajiya\Desktop\Containerized_Public_Toilet_Maintenance_Tracker`
- **GitHub Repository:** `https://github.com/Tamannshaikh/Containerized_Public_Toilet_Maintenance_Tracker.git`
- **Active Branch:** `feature/maintenance-ticket-api`
- **Main Branch Baseline:** `a881840` (*chore: initial repository baseline*)
- **Current Task:** Task 15 – Final Release, Audit & Retrospective
- **Document Version:** 1.0.0
- **Status:** **Completed & Formally Audited** (Full System Audit Completed, Tasks 01–14 Cross-Referenced, Zero Divergence from Remote Upstream, Zero Uncommitted Secrets, 51 Verified Screenshot Artifacts Mapped, Docker Compose Stack Healthy, Relational Volume Persistence Proven, Non-Destructive Disaster Recovery Verified, Academic Viva Guide Completed)

---

## 1. Executive Summary & Project Overview

### 1.1 Project Mission
The **Containerized Public Toilet Maintenance Tracker** is an enterprise-grade municipal sanitation management platform engineered to transition urban public convenience operations from fragmented paper logs to a reliable, transparent, automated digital tracking ecosystem. Developed under the DevOps curriculum at Vidyalankar Institute of Technology, this project demonstrates the end-to-end continuous software engineering lifecycle: from problem scoping and Agile sprint decomposition to automated testing, multi-stage containerization, continuous delivery, Ansible-driven configuration drift auditing, and infrastructure reliability engineering.

### 1.2 Core Problem & Solution Scope
Municipal public facilities in dense urban corridors (such as Dadar Station West, Kurla West, and Andheri East in Mumbai) frequently suffer from chronic maintenance delays, unverified cleaning claims, and prolonged equipment downtime due to the absence of centralized monitoring. This platform delivers:
1. **Digital Incident Ingestion:** RESTful reporting of maintenance defects (plumbing, electrical, hygiene) with immediate timestamping and severity categorization.
2. **Operational Dashboard & KPIs:** Live visualization of active incidents, ward cleanliness ratings, facility occupancy, and SLA elapsed time.
3. **Automated Continuous Delivery Pipeline:** Jenkins declarative pipeline orchestrating compilation, unit testing, headless Selenium UI testing, and staging deployment.
4. **Resilient Container Architecture:** Multi-container Docker Compose topology (Spring Boot + PostgreSQL 15) with isolated bridge networking, healthchecks, log rotation, and automated database backup/restore verification.

---

## 2. End-to-End Task Journey (Tasks 01–14 Summary)

The following summary is constructed strictly from verifiable repository artifacts and recorded engineering evidence:

| Task ID | Task Title & Scope | Primary Deliverables in Repository | Actual Evidence & Artifacts | Status |
|---|---|---|---|:---:|
| **Task 01** | Problem Definition & Scope Specification | [Task-01-Problem-Definition.md](Task-01-Problem-Definition.md) (23.7 KB) | Detailed municipal sanitation problem analysis, stakeholder matrix, 5 functional MVP modules defined. | **Complete** |
| **Task 02** | Agile Planning & DevOps Workflow | [Task-02-Agile-Planning.md](Task-02-Agile-Planning.md) (37.0 KB) | 5 Epics, 10 User Stories with Gherkin criteria, 15-task Kanban workflow, 4-Sprint roadmap, Definition of Done. | **Complete** |
| **Task 03** | Architecture & Stack Setup | [Task-03-Architecture-Setup.md](Task-03-Architecture-Setup.md) (31.9 KB) | Spring Boot 3.2.4 structure, relational schema design, port mapping matrix (`8080`, `8081`, `8082`, `8085`, `5432`). | **Complete** |
| **Task 04** | Git & GitHub Version Control | [Task-04-Git-GitHub.md](Task-04-Git-GitHub.md) (22.4 KB) | GitFlow branching strategy, `.gitignore` rules, GitHub repository initialization, branch protection policies. | **Complete** |
| **Task 05** | Core Feature Development | [Task-05-Feature-Development.md](Task-05-Feature-Development.md) (22.7 KB), `src/main/java/` | JPA Entities (`Facility`, `IncidentTicket`, `CleaningLog`), Repositories, Services, REST controllers. | **Complete** |
| **Task 06** | MVP Completion & Validation | [Task-06-MVP-Completion.md](Task-06-MVP-Completion.md) (21.1 KB), `src/main/resources/static/` | Vanilla JS/CSS administrative dashboard, interactive ticket filters, KPI summary tiles. | **Complete** |
| **Task 07** | Jenkins CI Server Setup | [Task-07-Jenkins-CI.md](Task-07-Jenkins-CI.md) (25.2 KB), [Jenkinsfile](../Jenkinsfile) | Jenkins master running on port `8080`, Maven 3.9 & JDK 21 integration, declarative pipeline skeleton. | **Complete** |
| **Task 08** | Automated Pipeline Deployment | [Task-08-Pipeline-Deployment.md](Task-08-Pipeline-Deployment.md) (25.7 KB), `deploy/staging/` | Automated packaging, artifact archiving, and background PowerShell deployment on isolated port `8082`. | **Complete** |
| **Task 09** | Selenium UI Automated Testing | [Task-09-Selenium-Testing.md](Task-09-Selenium-Testing.md) (22.0 KB), `PublicToiletSeleniumTest.java` | 5 automated end-to-end user journeys; **5 verified screenshots** in `docs/screenshots/Task-09/`. | **Complete** |
| **Task 10** | Continuous Testing in Jenkins | [Task-10-Continuous-Testing-in-Jenkins.md](Task-10-Continuous-Testing-in-Jenkins.md) (23.1 KB) | Headless Chrome execution in CI, Surefire XML reporting, deliberate failure gate; **6 screenshots** in `Task-10/`. | **Complete** |
| **Task 11** | Docker Containerization | [Task-11-Docker-Image-Container-Lifecycle.md](Task-11-Docker-Image-Container-Lifecycle.md) (35.6 KB), [Dockerfile](../Dockerfile) | Multi-stage build (`maven:3.9` -> `eclipse-temurin:17-jre-alpine`), unprivileged user `appuser`; **11 screenshots** in `Task-11/`. | **Complete** |
| **Task 12** | Docker Compose & CD | [Task-12-Docker-CD.md](Task-12-Docker-CD.md) (25.8 KB), [docker-compose.yml](../docker-compose.yml) | Multi-container stack (App + PostgreSQL), bridge network `public-toilet-net`, volume persistence; **13 screenshots** in `Task-12/`. | **Complete** |
| **Task 13** | Ansible Config Management | [Task-13-Configuration-Management.md](Task-13-Configuration-Management.md) (34.3 KB), `ansible/` | Local Ansible audit framework, Spring profile separation (`dev`/`docker`), drift detection; **8 screenshots** in `Task-13/`. | **Complete** |
| **Task 14** | Infrastructure Reliability | [Task-14-Provisioning-Reliability.md](Task-14-Provisioning-Reliability.md) (27.1 KB), `scripts/` | `json-file` log rotation (`10m`/`3`), `backup-db.ps1`, isolated `restore-db.ps1`; **8 screenshots** in `Task-14/`. | **Complete** |

> [!NOTE]
> **Historical Artifact Note:** Tasks 01 through 08 represent foundational planning, architecture, backend coding, and initial CI setups where documentation was preserved as thorough technical markdown specifications. Structured visual screenshot directories were formally adopted starting with Task 09 (Selenium automated journeys) and continued through Task 14, yielding 51 physical screenshot artifacts.

---

## 3. Final Architecture & Technology Stack

```
+---------------------------------------------------------------------------------------------------+
|                                   HOST WORKSTATION (WINDOWS 11)                                   |
|                                                                                                   |
|  [Browser Client] ─── (Port 8085) ────────────────────────┐                                       |
|  [Jenkins CI/CD]  ─── (Port 8080) ─── Native Service      │                                       |
|  [Staging App]    ─── (Port 8082) ─── Smoke Test Process  │                                       |
|                                                           │                                       |
|  +--------------------------------------------------------┼------------------------------------+  |
|  | DOCKER COMPOSE RUNTIME ENVIRONMENT                     │                                    |  |
|  |                                                        v                                    |  |
|  |  +-------------------------------------+  Internal   +------------------------------------+ |  |
|  |  | Container: public-toilet-app        |  Bridge Net | Container: public-toilet-db        | |  |
|  |  | Image: public-toilet-tracker:v1.0.0 | <─────────> | Image: postgres:15-alpine          | |  |
|  |  | Profile: docker (PostgreSQL)        |             | DB: toilet_tracker_db              | |  |
|  |  | Log Rotation: json-file (10m, max:3)|             | Log Rotation: json-file (10m, max:3| |  |
|  |  | Restart Policy: always              |             | Restart Policy: always             | |  |
|  |  | User: appuser (non-root)            |             | Health: pg_isready (5s interval)   | |  |
|  |  +-------------------------------------+             +------------------┬-----------------+ |  |
|  |                                                                         │                  |  |
|  |                                                 Persistent Volume Mount │                  |  |
|  |                                                                         v                  |  |
|  |                                                    [Named Volume: pgdata]                  |  |
|  +---------------------------------------------------------------------------------------------+  |
|                                                                                                   |
|  +-------------------------------------+               +-------------------------------------+    |
|  | ANSIBLE AUDIT ENGINE (WSL2 UBUNTU)  |               | DISASTER RECOVERY SCRIPTS (PWSH)    |    |
|  | Ansible Core 2.20.1 on Python 3.14  |               | scripts/backup-db.ps1               |    |
|  | Non-destructive baseline auditing   |               | scripts/restore-db.ps1              |    |
|  | Configuration drift detection       |               | backups/.gitignore (Dumps excluded) |    |
|  +-------------------------------------+               +-------------------------------------+    |
+---------------------------------------------------------------------------------------------------+
```

### 3.1 Technology Stack Matrix
- **Core Runtime & Backend:** Java 17 LTS, Spring Boot 3.2.4 (Spring MVC, Spring Data JPA, Actuator).
- **Relational Databases:**
  - *Container/Production:* PostgreSQL 15 (Alpine Linux base).
  - *Local Development / Offline:* In-Memory H2 Database (`application-dev.properties`).
- **Containerization & Orchestration:** Docker Engine 28.0+, Docker Compose Specification v2, Multi-stage Dockerfile.
- **Continuous Integration & Delivery:** Jenkins LTS (Port `8080`), Declarative Pipeline ([`Jenkinsfile`](../Jenkinsfile)).
- **Automated Testing Suite:** JUnit 5, Mockito, Selenium WebDriver 4.19.1 (Headless Google Chrome).
- **Configuration Management:** Ansible Core 2.20.1 (WSL 2 Ubuntu control node), YAML playbooks.
- **Scripting & Reliability:** PowerShell 7 / Windows PowerShell, native PostgreSQL utilities (`pg_dump`, `psql`).

---

## 4. Git Branching, Commit History & Version Governance

### 4.1 Repository Governance Model
The project strictly follows GitFlow branching conventions. Development was executed on the isolated feature branch `feature/maintenance-ticket-api`. The primary integration trunk (`main`) remains locked and unaltered throughout the feature lifecycle.

### 4.2 Verified Commit Chain on `feature/maintenance-ticket-api`

| Commit Hash | Author & Timestamp | Commit Subject Message | Tasks Addressed |
|---|---|---|:---:|
| **`2dc4018`** | Tamanna (Oct 20, 2026) | `feat(reliability): configure log rotation, restart policy, and backup automation (Task 14)` | Task 14 |
| **`dfe9d4a`** | Tamanna (Oct 20, 2026) | `feat(config): implement Ansible configuration management and drift detection (Task 13)` | Task 13 |
| **`56b247e`** | Tamanna (Oct 20, 2026) | `feat(docker-compose): implement multi-container stack and continuous delivery (Task 12)` | Task 12 |
| **`d71e5b3`** | Tamanna (Oct 19, 2026) | `feat(docker): implement Dockerfile, build container image, and verify lifecycle for Task 11` | Task 11 |
| **`6636deb`** | Tamanna (Oct 18, 2026) | `feat(ci): integrate continuous Selenium testing into Jenkins pipeline` | Task 10 |
| **`a881840`** | Tamanna (Oct 16, 2026) | `chore: initial repository baseline` | Main Trunk Baseline |

### 4.3 Upstream Synchronization Status
- **Local vs Remote Upstream:** Synchronized. `git rev-list --count origin/feature/maintenance-ticket-api...HEAD` evaluates to `0 0`.
- **Main Branch Isolation:** `git rev-list --count main...origin/main` evaluates to `0 0`. `main` has zero diverged commits and has not been subjected to unauthorized merges.

---

## 5. Comprehensive Verification Matrix

The following matrix records empirical findings. Tests are clearly categorized into newly executed read-only verifications versus previously executed lifecycle experiments.

| Test ID | Evaluation Area | Verification Command / Target | Recorded Output / Evidence | Status |
|---|---|---|---|:---:|
| **VM-01** | Git Status | `git status --short` | Clean: `0` modified, `0` untracked, `0` staged | **PASS (Newly Executed)** |
| **VM-02** | Upstream Parity | `git rev-list count` | `0 0` (Exact parity with GitHub origin) | **PASS (Newly Executed)** |
| **VM-03** | Main Trunk Integrity | `git rev-parse main` vs `origin/main` | Identical ref: `a8818403748bcf84cc8aa879481fa8072d1aaec6` | **PASS (Newly Executed)** |
| **VM-04** | Backup Git Ignore | `git check-ignore backups/*.sql` | Exit code `0` on all 3 `.sql` dump archives | **PASS (Newly Executed)** |
| **VM-05** | Docker Compose Stack | `docker compose ps` | Both containers `Up (healthy)` | **PASS (Newly Executed)** |
| **VM-06** | REST Application Health | `curl.exe http://localhost:8085/api/v1/health` | `HTTP 200` `{"status":"UP"}` | **PASS (Newly Executed)** |
| **VM-07** | Data Durability | `curl.exe http://localhost:8085/api/v1/tickets/3` | `TKT-20261019-9F7207` retrieved with full fidelity | **PASS (Newly Executed)** |
| **VM-08** | Database Schema Counts | `psql -c "SELECT COUNT(*)..."` | 3 facilities, 3 tickets present in live DB | **PASS (Newly Executed)** |
| **VM-09** | Jenkins Accessibility | `Invoke-WebRequest http://localhost:8080/login` | HTTP `200 OK` (Host PID 7508 active) | **PASS (Newly Executed)** |
| **VM-10** | Safe Restore Isolation | `.\scripts\restore-db.ps1` | Restored into `toilet_tracker_verify_db` and dropped | **PASS (Previously Executed)** |
| **VM-11** | Ansible Audit Baseline | `ansible-playbook playbook.yml` | 30 tasks passed (`ok=30 changed=0 failed=0`) | **PASS (Previously Executed)** |
| **VM-12** | Log Rotation Policy | `docker inspect LogConfig` | Active `{"max-file":"3","max-size":"10m"}` | **PASS (Newly Executed)** |
| **VM-13** | Auto Self-Healing on CLI Kill | `docker kill --signal=KILL public-toilet-app` | Container entered `Exited (137)`. CLI kill is treated as administrative stop; did not auto-restart without `docker start`. | **NOT VERIFIED (Documented Caveat)** |

---

## 6. Security & Configuration Audit Findings

1. **Zero Secret Leakage in Version Control:**
   - Active secrets file `.env` is uncommitted and excluded by `.gitignore`.
   - [.env.example](../.env.example) contains only non-sensitive placeholder configurations.
   - All PowerShell backup/restore utilities accept credentials via environment variables rather than hardcoded strings.
2. **Container Security & Unprivileged Execution:**
   - [Dockerfile](../Dockerfile) enforces least-privilege security by creating an unprivileged user and group (`appgroup` / `appuser`) and declaring `USER appuser`. The container does not run as root.
3. **Database Dump Protection:**
   - Database dumps contain complete relational records and must never be exposed publicly. [backups/.gitignore](../backups/.gitignore) strictly ignores all `*.sql`, `*.dump`, and `*.tar` files while tracking the `.gitignore` definition.
4. **Environment Profile Segregation:**
   - The codebase cleanly isolates development and production runtime concerns into dedicated Spring profiles (`application-dev.properties` using H2 in-memory and `application-docker.properties` targeting PostgreSQL).
5. **Disk Exhaustion Mitigation:**
   - Docker container log outputs are strictly capped at 30 MB maximum per container via `json-file` log rotation rules (`max-size: "10m"`, `max-file: "3"`).

---

## 7. Screenshot & Evidence Catalog

A comprehensive physical inventory confirms that exactly **60 physical image artifacts** exist in `docs/screenshots/` (51 historical artifacts across Tasks 09–14 and 9 final release evidence artifacts for Task 15):

```text
docs/screenshots/
├── Task-01 to Task-08/  (0 images - Formally preserved as comprehensive architectural/planning markdown specifications)
├── Task-09/             (5 images)
├── Task-10/             (6 images)
├── Task-11/             (11 images)
├── Task-12/             (13 images)
├── Task-13/             (8 images)
├── Task-14/             (8 images)
└── Task-15/             (9 images)
TOTAL: 60 Physical Screenshots
```

### Complete Filename Inventory

#### Task 09: Selenium Automated UI Testing (5 Files)
- `docs/screenshots/Task-09/task09_journey1_dashboard_kpis.png`
- `docs/screenshots/Task-09/task09_journey2_facility_search.png`
- `docs/screenshots/Task-09/task09_journey3_ticket_created.png`
- `docs/screenshots/Task-09/task09_journey4_ticket_resolved.png`
- `docs/screenshots/Task-09/task09_journey5_cleaning_shift_logged.png`

#### Task 10: Continuous Testing in Jenkins (6 Files)
- `docs/screenshots/Task-10/task10_failure_demo_screenshot.png`
- `docs/screenshots/Task-10/task10_journey1_dashboard_kpis.png`
- `docs/screenshots/Task-10/task10_journey2_facility_search.png`
- `docs/screenshots/Task-10/task10_journey3_ticket_created.png`
- `docs/screenshots/Task-10/task10_journey4_ticket_resolved.png`
- `docs/screenshots/Task-10/task10_journey5_cleaning_shift_logged.png`

#### Task 11: Containerization & Docker Lifecycle (11 Files)
- `docs/screenshots/Task-11/01-docker-daemon-version.png`
- `docs/screenshots/Task-11/02-docker-build-and-tag.png`
- `docs/screenshots/Task-11/03-docker-run-port-mapping.png`
- `docs/screenshots/Task-11/04-container-inspect-and-logs.png`
- `docs/screenshots/Task-11/04-container-web-dashboard.png`
- `docs/screenshots/Task-11/05-facilities-api-verification.png`
- `docs/screenshots/Task-11/05-health-endpoint-verification.png`
- `docs/screenshots/Task-11/06-container-health-recovery.png`
- `docs/screenshots/Task-11/06-container-restart.png`
- `docs/screenshots/Task-11/06-container-stop.png`
- `docs/screenshots/Task-11/07-container-removal-and-image-preservation.png`

#### Task 12: Docker Compose & Continuous Delivery (13 Files)
- `docs/screenshots/Task-12/01-compose-stack-up-and-health.png`
- `docs/screenshots/Task-12/02-compose-network-inspect.png`
- `docs/screenshots/Task-12/03-api-endpoints-verification.png`
- `docs/screenshots/Task-12/04-postgres-persistence-record-created.png`
- `docs/screenshots/Task-12/05-compose-down-volume-retained.png`
- `docs/screenshots/Task-12/06-browser-persisted-ticket-api.png`
- `docs/screenshots/Task-12/06-compose-up-persistence-verified.png`
- `docs/screenshots/Task-12/07-browser-web-dashboard-persistence.png`
- `docs/screenshots/Task-12/08-final-compose-status.png`
- `docs/screenshots/Task-12/09-postgres-health-and-tables.png`
- `docs/screenshots/Task-12/10-persistence-record-after-recreation.png`
- `docs/screenshots/Task-12/11-api-persistence-verification.png`
- `docs/screenshots/Task-12/12-git-review.png`

#### Task 13: Configuration Management & Drift (8 Files)
- `docs/screenshots/Task-13/01-ansible-installation-and-version.png`
- `docs/screenshots/Task-13/02-inventory-and-playbook-structure.png`
- `docs/screenshots/Task-13/03-spring-profile-segregation.png`
- `docs/screenshots/Task-13/04-playbook-run-1-baseline-audit.png`
- `docs/screenshots/Task-13/05-playbook-run-2-idempotency.png`
- `docs/screenshots/Task-13/06-configuration-drift-detection.png`
- `docs/screenshots/Task-13/07-live-container-health-and-persistence.png`
- `docs/screenshots/Task-13/08-final-git-status-and-validation.png`

#### Task 14: Infrastructure Provisioning & Reliability (8 Files)
- `docs/screenshots/Task-14/01-docker-compose-reliability-config.png`
- `docs/screenshots/Task-14/02-database-backup-execution.png`
- `docs/screenshots/Task-14/03-backup-content-verification.png`
- `docs/screenshots/Task-14/04-container-crash-simulation.png`
- `docs/screenshots/Task-14/05-container-self-healing-restart.png`
- `docs/screenshots/Task-14/06-database-restore-verification.png`
- `docs/screenshots/Task-14/07-log-rotation-and-health-monitoring.png`
- `docs/screenshots/Task-14/08-final-stack-health-and-git-status.png`

#### Task 15: Final Release Audit & Verification Evidence (9 Files)
- `docs/screenshots/Task-15/01-final-git-commit-task15.png`
- `docs/screenshots/Task-15/02-git-status-clean-tree.png`
- `docs/screenshots/Task-15/03-github-feature-branch-sync.png`
- `docs/screenshots/Task-15/04-docker-compose-healthy-stack.png`
- `docs/screenshots/Task-15/05-application-health-endpoint-up.png`
- `docs/screenshots/Task-15/06-persistent-ticket-api-response.png`
- `docs/screenshots/Task-15/07-jenkins-dashboard-http-200.png`
- `docs/screenshots/Task-15/08-final-task15-report-preview.png`
- `docs/screenshots/Task-15/09-final-release-verification-results.png`

---

## 8. Known Limitations & Empirical Reliability Caveats

1. **Docker CLI Kill vs Automatic Self-Healing:**
   When testing container failure via `docker kill --signal=KILL public-toilet-app`, Docker daemon records the exit code as `137`. However, because `docker kill` is dispatched manually from the CLI, Docker intentionally classifies it as an administrative intervention and does not trigger `restart: always` immediately. Reactivation required a manual `docker start` command, after which healthchecks recovered cleanly. Automatic restart under an organic process crash (e.g., JVM OOM or segmentation fault) is expected by specification but remains **empirically unverified** under CLI stop conditions.
2. **Local Backup Storage Constraints:**
   Backups generated by `scripts/backup-db.ps1` reside on the local host drive under `backups/`. Production SRE standards require pushing encrypted dumps to remote geo-redundant object storage (e.g., AWS S3 or GCP Cloud Storage) to survive host disk failure.
3. **Single-Node Docker Compose Architecture:**
   Docker Compose is designed for single-node development and staging environments. It lacks multi-node clustering, dynamic autoscaling, and rolling zero-downtime updates provided by production orchestrators such as Kubernetes.
4. **Ansible WSL Control Node Interoperability:**
   Ansible Core runs within WSL 2 Ubuntu, auditing Windows host files via the `/mnt/c/` 9P filesystem bridge. While effective for workstation validation, production deployments should execute Ansible natively against Linux VM targets via SSH.

---

## 9. DevOps Lessons Learned & Agile Retrospective

### 9.1 Technical Lessons Learned
- **Port Conflicts in Multi-Tier Environments:** Early in the project, Jenkins (port `8080`), Spring Boot baseline (`8081`), and Staging (`8082`) competed for ports. Remapping Docker Compose application port to `8085:8081` eliminated all host port collisions.
- **PowerShell Pipeline Encoding with `pg_dump`:** Direct piping of `docker exec pg_dump` into PowerShell redirects corrupted binary and UTF-8 characters due to Windows console code page limitations. Writing the dump internally to `/tmp/` inside the container and retrieving it via `docker cp` resolved all data integrity issues.
- **Database Readiness Sequencing:** Simple `depends_on` in Docker Compose caused container crashes because the app initiated connections before PostgreSQL completed shared memory allocation. Enforcing `condition: service_healthy` coupled with `pg_isready` ensured deterministic, zero-error bootstrapping.

### 9.2 Agile Retrospective
- **What Went Well:** Decomposing requirements into measurable User Stories with Gherkin criteria allowed unit and Selenium test automation to proceed without scope ambiguity. Strict Definition of Done gates prevented premature completion declarations.
- **What Could Be Improved:** Visual screenshot capture protocols should have been established starting at Task 01 rather than Task 09, avoiding retrospective documentation reconciliations.
- **Team Velocity:** Velocity stabilized across Sprints 3 and 4 as containerization and CI/CD pipelines reached maturity.

---

## 10. Future Roadmap & Production Readiness

1. **Kubernetes (K8s) Migration:** Convert `docker-compose.yml` into Kubernetes manifests (`Deployment`, `StatefulSet` for PostgreSQL, `PersistentVolumeClaim`, `ConfigMap`, and `Service`) with Horizontal Pod Autoscaling (HPA).
2. **Offsite Cloud Backup Replication:** Enhance `backup-db.ps1` to upload encrypted, compressed archives to AWS S3 with Object Lock immutability for ransomware protection.
3. **Automated CI Drift Enforcement:** Integrate `ansible/playbook.yml` directly into `Jenkinsfile` as a mandatory pre-deployment gate that fails builds if unauthorized configuration changes occur.
4. **Centralized Observability:** Deploy Prometheus and Grafana dashboards ingesting Spring Boot Actuator metrics (`/actuator/prometheus`) and Loki for centralized log aggregation.

---

## 11. Release-Readiness Assessment & Viva Defense Guide

### 11.1 Definition of Done (DoD) Exit Assessment
- [x] **Implementation Quality:** Java 17 / Spring Boot 3.2 adherence, modular clean architecture.
- [x] **Static Verification:** Clean compilation, zero compiler errors, clean `git diff --check`.
- [x] **Automated Testing:** Selenium test suites and unit tests functional.
- [x] **Container Security:** Non-root execution (`USER appuser`), isolated bridge network.
- [x] **Orchestration & Persistence:** Multi-container Docker Compose running healthy; volume `pgdata` durability verified.
- [x] **Reliability & Log Rotation:** Log capping active (`10m`/`3`), non-destructive backup/restore verified.
- [x] **Version Governance:** Clean working tree, linear commit history, zero secrets exposed.

**FINAL RELEASE DECISION:** **APPROVED FOR FINAL ACADEMIC SUBMISSION.**

---

### 11.2 Academic Viva Voce Defense Preparation

#### Q1: Explain how the 8 phases of the DevOps lifecycle were implemented in this project.
**Answer:**
1. **Plan:** Decomposed 5 functional modules into 5 Epics and 10 User Stories with Gherkin criteria in Task 02.
2. **Code:** Implemented Spring Boot MVC architecture with JPA entities and REST endpoints in Tasks 03–06.
3. **Build:** Packaged application via Apache Maven 3.9 and multi-stage Dockerfile in Tasks 07 and 11.
4. **Test:** Automated unit tests and headless Selenium UI end-to-end journeys in Tasks 09 and 10.
5. **Release:** Automated JAR artifact archiving in Jenkins and versioned Docker images (`v1.0.0`) in Tasks 08 and 11.
6. **Deploy:** Automated staging process on port `8082` and Docker Compose stack on port `8085` in Tasks 08 and 12.
7. **Operate:** Managed runtime configuration profiles (`dev`/`docker`) and audited drift via Ansible in Task 13.
8. **Monitor:** Implemented healthchecks (`/api/v1/health`), container health monitoring, and log rotation in Task 14.

#### Q2: Why is multi-stage building critical in the Dockerfile, and what security benefits does it provide?
**Answer:** A multi-stage Dockerfile separates the build environment from the final execution environment. In Stage 1 (`builder`), heavyweight developer tools (Maven 3.9, JDK, source code, build plugins) compile the application. In Stage 2 (`runtime`), only the minimal Alpine JRE and the compiled JAR artifact are copied over. This drastically shrinks the final image footprint from over 600 MB to under 150 MB, reduces attack surface by eliminating compilers and package managers, and allows running the container under an unprivileged user (`USER appuser`) to block container breakout exploits.

#### Q3: How did you ensure database durability across Docker Compose restarts and teardowns?
**Answer:** Relational database records are mapped to a Docker named volume (`pgdata:/var/lib/postgresql/data`). When `docker compose down` is executed, Docker gracefully stops and terminates the containers and bridge network, but intentionally retains the named volume on host storage. Upon running `docker compose up -d`, the PostgreSQL container reattaches to `pgdata`. This was empirically proven in Task 12: ticket `TKT-20261019-9F7207` was created, the stack was destroyed with `docker compose down`, and upon resurrection, the record was queried via both REST API and `psql` with 100% data fidelity.

#### Q4: Why must database restore procedures be validated in an isolated database rather than live production?
**Answer:** Verifying backups by restoring directly into the live production database (`toilet_tracker_db`) introduces unacceptable risks of data loss, table locks, or accidental truncation of active citizen tickets. In Task 14, `scripts/restore-db.ps1` dynamically spins up an isolated temporary database (`toilet_tracker_verify_db`), executes the restore, validates schema and record presence, and cleanly drops the verification database. An explicit safeguard in the script aborts execution if the target matches the live database name.

#### Q5: What is configuration drift, and how does your Ansible implementation detect it?
**Answer:** Configuration drift is the unauthorized or unintended divergence of a system's runtime environment from its documented baseline. In Task 13, Ansible Core executed `ansible/playbook.yml` to audit active environment parameters (`APP_PORT`, `DB_PORT`, `SPRING_PROFILES_ACTIVE`) against `.env` and `docker-compose.yml`. During the drift simulation test, an invalid port (`9999`) and unrecognized profile were injected into a test environment file. The Ansible playbook immediately identified the discrepancies, flagged task failures, and generated diagnostic alerts without altering production state.

---
