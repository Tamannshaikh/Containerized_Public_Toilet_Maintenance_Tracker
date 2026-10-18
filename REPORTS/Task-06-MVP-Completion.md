# Task 06: Minimum Viable Product (MVP) Completion

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
- **Current Task:** Task 06 – MVP Completion
- **Current Active Branch:** `feature/maintenance-ticket-api` (Planned PR merge to `develop`, preparing `feature/mvp-dashboard-summary`)
- **Document Version:** 1.0.0
- **Status:** MVP Implemented & Verified with 22 Automated Tests (Awaiting User Review & Screenshot Evidence Capture)

---

## 1. Objectives & Executive Summary

The primary objective of **Task 06** is to complete and verify the full Minimum Viable Product (MVP) functional release for the municipal sanitation monitoring platform. This milestone brings together the 5 foundational MVP epics planned in Task 02:

1. **Event Entry:** Implementing digital capture of cleaning shift inspection checklists (`POST /api/v1/cleaning-logs`) and citizen defect reports (`POST /api/v1/tickets`) with strict input validation.
2. **Searchable Dashboard:** Providing real-time, multi-attribute keyword and ward filtering across public convenience facilities and maintenance tickets.
3. **Summary Indicators:** Dynamically calculating 4 core operational KPI summary cards (Total Facilities, Active Incidents, SLA Breaches, Average Cleanliness Index) derived from actual database records.
4. **Status Drill-Down:** Allowing operators to inspect complete facility ticket timelines and enforcing legitimate lifecycle transitions (`OPEN` $\rightarrow$ `IN_PROGRESS` $\rightarrow$ `RESOLVED` $\rightarrow$ `CLOSED`) while preventing illegal state jumps.
5. **Alert & Exception Management:** Programmatically detecting sanitary emergencies (water supply outages, hygiene ratings $< 60\%$) and SLA overdue violations ($> 4\text{ hours}$ for critical/high tickets).
6. **Frontend Integration:** Delivering a responsive, modern web dashboard in `src/main/resources/static/index.html` (mirrored in `frontend/index.html`) communicating directly with backend REST endpoints.
7. **Comprehensive Verification:** Authoring and passing 22 automated unit and integration tests with zero failures (`BUILD SUCCESS`).

---

## 2. MVP Features Implementation Details

### 2.1 Feature 1: Event Entry (Cleaning Logs & Maintenance Tickets)
- **Sanitation Shift Logging (`POST /api/v1/cleaning-logs`):**
  - Sanitation crew members record completed cleaning events specifying staff name, running water availability, soap stock status, hygiene rating ($1\text{ to }5\text{ stars}$), and operational remarks.
  - Submitting a cleaning shift automatically synchronizes the parent facility's operational status: if water is supplied and rating $\ge 3$, the facility is marked `OPERATIONAL`; if water is depleted or rating $< 3$, status switches to `MAINTENANCE_REQUIRED` and the facility cleanliness score is recalculated.
- **Maintenance Incident Logging (`POST /api/v1/tickets`):**
  - Citizens and inspectors log maintenance faults against specific facility IDs. Required fields (`facilityId`, `title`, `description`) are strictly enforced, returning `400 Bad Request` if blank or `404 Not Found` if the target facility does not exist.
  - Automatically generates a non-colliding tracking code: `TKT-YYYYMMDD-XXXXXX`.

### 2.2 Feature 2: Searchable Dashboard
- **Facility Inventory Search (`GET /api/v1/facilities`):**
  - Supports query parameters `search` (keyword search across facility name, facility code, address), `ward` (e.g., `G/North`, `H/West`, `K/East`), and `status` (`OPERATIONAL`, `MAINTENANCE_REQUIRED`).
- **Ticket Search & Filter (`GET /api/v1/tickets`):**
  - Supports multi-parameter filtering: `search` (keyword across ticket number, title, description, facility name), `status` (`OPEN`, `IN_PROGRESS`, `RESOLVED`, `CLOSED`), `priority` (`LOW`, `MEDIUM`, `HIGH`, `CRITICAL`), and `facilityId`.

### 2.3 Feature 3: Summary Indicators
- **Operational KPI Aggregator (`GET /api/v1/dashboard/summary`):**
  - Computes real-time metrics aggregated across all relational records:
    - `totalFacilities`: Total convenience blocks registered.
    - `operationalFacilities` vs. `maintenanceRequiredFacilities`: High-level operational readiness split.
    - `totalTickets`, `openTickets`, `inProgressTickets`, `resolvedTickets`, `closedTickets`: Ticket lifecycle distribution.
    - `criticalTickets`: Unresolved high-severity defects.
    - `overdueTickets`: Tickets currently exceeding SLA response thresholds.
    - `averageCleanlinessScore`: Average hygiene percentage across all active facilities.

### 2.4 Feature 4: Status Drill-Down & Valid Lifecycle State Machine
- **Ticket Inspection (`GET /api/v1/tickets/{id}`):**
  - Returns complete metadata including facility details, reporter identity, assigned technicians, creation timestamp, update timestamp, and resolution timestamp.
- **State Transition Enforcement (`PATCH /api/v1/tickets/{id}/status?status=...`):**
  - Enforces strict lifecycle rules:
    - `OPEN` $\rightarrow$ `IN_PROGRESS` or `RESOLVED` (Permitted)
    - `IN_PROGRESS` $\rightarrow$ `RESOLVED` (Permitted)
    - `RESOLVED` $\rightarrow$ `CLOSED` (Permitted)
    - `RESOLVED` $\rightarrow$ `OPEN` (Permitted - Reopening)
    - `OPEN` $\rightarrow$ `CLOSED` (**Rejected with 400 Bad Request**: *"Ticket must be resolved before it can be closed"*).
    - `CLOSED` $\rightarrow$ `IN_PROGRESS` / `RESOLVED` (**Rejected with 400 Bad Request**: *"Closed tickets cannot be transitioned directly; they must be reopened to OPEN first"*).

### 2.5 Feature 5: Alert and Exception View
- **Exception Engine (`GET /api/v1/alerts/exceptions`):**
  - **SLA Breach Rule:** Any unresolved ticket (`status != RESOLVED` and `status != CLOSED`) with priority `CRITICAL` or `HIGH` pending for $\ge 4\text{ hours}$, or priority `MEDIUM` or `LOW` pending for $\ge 24\text{ hours}$, is categorized as `SLA OVERDUE`.
  - **Water Supply Hazard:** Any facility where `hasRunningWater == false` is flagged as an active `WATER OUTAGE`.
  - **Hygiene Score Deficit:** Any facility whose score drops below $60\%$ is flagged as `LOW HYGIENE`.
  - Aggregates active alerts into `totalAlertsCount` and displays an emergency banner on the web interface.

---

## 3. Architecture & Relevant Files

```text
src/main/
├── java/com/tracker/toilet/
│   ├── config/
│   │   └── DataInitializer.java              # Seed data for facilities & baseline tickets
│   ├── controller/
│   │   ├── CleaningLogController.java        # Shift log REST endpoints
│   │   ├── DashboardController.java          # Summary KPIs & exception alerts REST endpoints
│   │   ├── FacilityController.java           # Facility inventory and search endpoints
│   │   ├── HealthController.java             # Operational probe (/api/v1/health)
│   │   └── MaintenanceTicketController.java  # Ticket management & status transition endpoints
│   ├── dto/
│   │   ├── AlertsResponse.java               # Active SLA & hazard exceptions payload
│   │   ├── CleaningLogResponse.java          # Outbound cleaning log response
│   │   ├── CreateCleaningLogRequest.java     # Inbound cleaning shift submission
│   │   ├── CreateFacilityRequest.java        # Facility registration request
│   │   ├── CreateTicketRequest.java          # Inbound defect ticket submission
│   │   ├── ErrorResponse.java                # Standardized JSON error response
│   │   ├── FacilityResponse.java             # Facility summary response
│   │   ├── SummaryIndicatorsResponse.java    # 4 core KPI summary card payload
│   │   └── TicketResponse.java               # Ticket detail response with facility metadata
│   ├── exception/
│   │   ├── BadRequestException.java          # 400 Bad Request
│   │   ├── GlobalExceptionHandler.java       # Centralized @RestControllerAdvice
│   │   └── ResourceNotFoundException.java    # 404 Not Found
│   ├── model/
│   │   ├── CleaningLog.java                  # JPA Entity: cleaning_logs
│   │   ├── MaintenanceTicket.java            # JPA Entity: maintenance_tickets
│   │   ├── StaffMember.java                  # JPA Entity: staff_members
│   │   ├── TicketPriority.java               # Enum: LOW, MEDIUM, HIGH, CRITICAL
│   │   ├── TicketStatus.java                 # Enum: OPEN, IN_PROGRESS, RESOLVED, CLOSED
│   │   └── ToiletFacility.java               # JPA Entity: toilet_facilities
│   ├── repository/
│   │   ├── CleaningLogRepository.java        # Spring Data JPA repository
│   │   ├── MaintenanceTicketRepository.java  # Spring Data JPA repository
│   │   ├── StaffMemberRepository.java        # Spring Data JPA repository
│   │   └── ToiletFacilityRepository.java     # Spring Data JPA repository
│   └── service/
│   │   ├── CleaningLogService.java           # Shift validation & facility score sync
│   │   ├── DashboardSummaryService.java      # Dynamic KPI & SLA exception calculations
│   │   ├── FacilityService.java              # Facility search, ward filter, & CRUD
│   │   └── MaintenanceTicketService.java     # Ticket search, ID generator, & state machine
│   └── ToiletMaintenanceApplication.java     # Spring Boot entry point
└── resources/
    ├── application.properties                # Configured server.port=8081 & H2 runtime
    └── static/
        └── index.html                        # Modern responsive Single-Page Dashboard
frontend/
└── index.html                                # Mirrored web dashboard asset
```

---

## 4. REST API Endpoints Specification

| Method | Endpoint Path | Query Parameters | Response Code | Description |
|---|---|---|:---:|---|
| `GET` | `/api/v1/health` | None | `200 OK` | System operational health check probe. |
| `GET` | `/api/v1/dashboard/summary` | None | `200 OK` | Returns 4 core summary KPI indicators and totals. |
| `GET` | `/api/v1/alerts/exceptions` | None | `200 OK` | Lists SLA overdue tickets, water outages, and low hygiene flags. |
| `GET` | `/api/v1/facilities` | `search`, `ward`, `status` | `200 OK` | Filterable public toilet facility inventory. |
| `GET` | `/api/v1/facilities/{id}` | None | `200 OK` / `404` | Retrieves facility details by ID. |
| `POST` | `/api/v1/facilities` | None | `201 Created` | Registers a new public convenience block. |
| `GET` | `/api/v1/tickets` | `search`, `status`, `priority`, `facilityId` | `200 OK` | Multi-filter maintenance ticket search. |
| `GET` | `/api/v1/tickets/{id}` | None | `200 OK` / `404` | Retrieves single ticket details for status drill-down. |
| `POST` | `/api/v1/tickets` | None | `201 Created` / `400` / `404` | Logs a new maintenance defect against an existing facility. |
| `PATCH` | `/api/v1/tickets/{id}/status` | `status` | `200 OK` / `400` | Transitions ticket status with lifecycle validation. |
| `GET` | `/api/v1/cleaning-logs` | `facilityId` | `200 OK` | Lists completed sanitation shift logs. |
| `POST` | `/api/v1/cleaning-logs` | None | `201 Created` / `400` | Records cleaning shift checklist and updates facility score. |

---

## 5. Web Dashboard Functions

The responsive Single-Page Application (SPA) delivered in [`src/main/resources/static/index.html`](../src/main/resources/static/index.html) and [`frontend/index.html`](../frontend/index.html) includes:
1. **Header & Metadata:** Displays project branding, municipal operational scope, and student academic identification (`Tamanna Shaikh | Roll: 24102A2006 | VIT Mumbai`).
2. **KPI Summary Cards:** Live visualization of Total Facilities, Active Tickets, SLA Overdue breaches, and Cleanliness Percentage.
3. **Emergency Alert Banner:** Automatically reveals a high-visibility warning when unresolved SLA breaches or water outages are detected.
4. **Maintenance Tickets View:** Searchable table with instant client-side keyword filtering and status/priority dropdown selectors.
5. **Facility Directory View:** Overview of cabins, operational statuses, and live running water indicators across municipal wards.
6. **Cleaning Shift Logs View:** Historical log of sanitization crew entries and cleanliness ratings.
7. **Interactive Modals:**
   - *Status Drill-Down Modal:* Detailed ticket breakdown with 1-click status transition buttons (`Start Work`, `Mark Resolved`, `Close Ticket`, `Reopen`).
   - *Log Defect Ticket Modal:* Form with facility selector, issue title, description, and priority level.
   - *Record Cleaning Shift Modal:* Form with water/soap restock checkboxes, 5-star hygiene rating, and cleaner name.

---

## 6. Test Suite Execution & Verification Results

Executed `mvn clean test` across the full test suite:

```text
[INFO] Scanning for projects...
[INFO] ----< com.tracker.toilet.devops:public-toilet-maintenance-tracker >-----
[INFO] Building Containerized_Public_Toilet_Maintenance_Tracker 1.0.0-SNAPSHOT
[INFO] -------------------------------------------------------
[INFO]  T E S T S
[INFO] -------------------------------------------------------
[INFO] Running com.tracker.toilet.CleaningLogControllerTest
[INFO] Tests run: 4, Failures: 0, Errors: 0, Skipped: 0 -- in com.tracker.toilet.CleaningLogControllerTest
[INFO] Running com.tracker.toilet.DashboardControllerTest
[INFO] Tests run: 2, Failures: 0, Errors: 0, Skipped: 0 -- in com.tracker.toilet.DashboardControllerTest
[INFO] Running com.tracker.toilet.FacilityControllerTest
[INFO] Tests run: 5, Failures: 0, Errors: 0, Skipped: 0 -- in com.tracker.toilet.FacilityControllerTest
[INFO] Running com.tracker.toilet.HealthControllerTest
[INFO] Tests run: 1, Failures: 0, Errors: 0, Skipped: 0 -- in com.tracker.toilet.HealthControllerTest
[INFO] Running com.tracker.toilet.MaintenanceTicketControllerTest
[INFO] Tests run: 9, Failures: 0, Errors: 0, Skipped: 0 -- in com.tracker.toilet.MaintenanceTicketControllerTest
[INFO] Running com.tracker.toilet.ToiletMaintenanceApplicationTests
[INFO] Tests run: 1, Failures: 0, Errors: 0, Skipped: 0 -- in com.tracker.toilet.ToiletMaintenanceApplicationTests
[INFO] 
[INFO] Results:
[INFO] 
[INFO] Tests run: 22, Failures: 0, Errors: 0, Skipped: 0
[INFO] 
[INFO] ------------------------------------------------------------------------
[INFO] BUILD SUCCESS
[INFO] Total time:  12.015 s
[INFO] Finished at: 2026-10-18T19:04:17+05:30
[INFO] ------------------------------------------------------------------------
```

### Breakdown of 22 Automated Tests:
- `HealthControllerTest` (1 test): Health probe status UP verification.
- `FacilityControllerTest` (5 tests): List facilities, get facility by ID, 404 for unknown facility, create facility, ward filtering.
- `MaintenanceTicketControllerTest` (9 tests): List tickets, get ticket by ID, create valid ticket, reject missing title (400), reject non-existent facility (404), reject unknown ticket ID (404), update status to RESOLVED, reject invalid direct OPEN $\rightarrow$ CLOSED transition (400), keyword search filtering.
- `CleaningLogControllerTest` (4 tests): Create cleaning log, reject missing cleaner name (400), reject invalid hygiene rating $> 5$ (400), list cleaning logs.
- `DashboardControllerTest` (2 tests): Verify summary KPI indicators calculation, verify alerts and exceptions payload.
- `ToiletMaintenanceApplicationTests` (1 test): Spring Boot context loading.

---

## 7. Git Workflow, Branching & Release Readiness

### Current Branch Hierarchy:
- `main`: Permanent production branch tracking `origin/main` at commit `92a874b`.
- `develop`: Integration branch created from `main`.
- `feature/maintenance-ticket-api`: Active feature branch hosting Task 05 and Task 06 backend and UI implementations.
- Planned next feature branch: `feature/mvp-dashboard-summary`.

### Git Working-Tree & Release Status:
- **No commits, pushes, or branch switches have been executed**, ensuring zero data loss and strictly following user approval protocols.
- **Candidate Release Tag:** `v0.6.0-mvp` prepared for tagging upon formal review and merge into `develop`.

---

## 8. Screenshot Evidence Checklist

### Required Visual Deliverables

All visual artifacts for Task 06 must be saved in the directory:  
[`docs/screenshots/Task-06/`](../docs/screenshots/Task-06/)

| # | Screenshot Filename | Exact Window to Capture | Description / Content | Status |
|---|---|---|---|:---:|
| 1 | `task06_event_entry.png` | Web Browser / Postman | Modal or form displaying "Log Sanitation Maintenance Ticket" or "Record Completed Cleaning Shift" filled and submitted. | Pending Capture |
| 2 | `task06_searchable_dashboard.png` | Web Browser | Main dashboard showing the Maintenance Tickets table with search input filter applied (e.g. searching "flush"). | Pending Capture |
| 3 | `task06_summary_indicators.png` | Web Browser | The 4 KPI cards (Total Facilities, Active Tickets, SLA Overdue, Cleanliness Score) displaying live aggregated metrics. | Pending Capture |
| 4 | `task06_status_drilldown.png` | Web Browser | Status drill-down modal open for a selected ticket showing full timestamps, details, and lifecycle buttons. | Pending Capture |
| 5 | `task06_alert_exception_view.png` | Web Browser | "Alerts & Exceptions" tab or emergency banner displaying overdue tickets and water outage notices. | Pending Capture |
| 6 | `task06_maven_test_success.png` | PowerShell Terminal | Output of `mvn clean test` showing `Tests run: 22, Failures: 0, Errors: 0` and `BUILD SUCCESS`. | Pending Capture |
| 7 | `task06_git_workflow.png` | PowerShell Terminal | Output of `git branch -a; git status` displaying active feature branch and working tree state. | Pending Capture |
| 8 | `task06_report_preview.png` | IDE Markdown Preview | Rendered view of [`REPORTS/Task-06-MVP-Completion.md`](./Task-06-MVP-Completion.md) showing metadata and MVP sections. | Pending Capture |

### How the Student Should Capture These Screenshots:
1. **Start Application Locally:**
   In PowerShell, run:
   ```powershell
   mvn spring-boot:run
   ```
   Open `http://localhost:8081` in your browser.
2. **For `task06_event_entry.png`:** Click `+ Log New Defect` or `+ Log Cleaning Shift`, fill in the form, capture the modal, and save as `docs/screenshots/Task-06/task06_event_entry.png`.
3. **For `task06_searchable_dashboard.png`:** In the search bar, type "flush", capture the filtered ticket list, and save as `docs/screenshots/Task-06/task06_searchable_dashboard.png`.
4. **For `task06_summary_indicators.png`:** Capture the top section showing the 4 KPI cards, and save as `docs/screenshots/Task-06/task06_summary_indicators.png`.
5. **For `task06_status_drilldown.png`:** Click on any ticket row (or click `Drill Down`), capture the detailed inspection modal, and save as `docs/screenshots/Task-06/task06_status_drilldown.png`.
6. **For `task06_alert_exception_view.png`:** Click the `Alerts & Exceptions` tab, capture the active exceptions list, and save as `docs/screenshots/Task-06/task06_alert_exception_view.png`.
7. **For `task06_maven_test_success.png`:** In PowerShell, run `mvn test`, capture the `Tests run: 22, Failures: 0` output, and save as `docs/screenshots/Task-06/task06_maven_test_success.png`.
8. **For `task06_git_workflow.png`:** In PowerShell, run `git branch; git status`, capture the terminal, and save as `docs/screenshots/Task-06/task06_git_workflow.png`.
9. **For `task06_report_preview.png`:** Open this file in IDE Markdown Preview, capture the screen, and save as `docs/screenshots/Task-06/task06_report_preview.png`.

---

## 9. Conclusion & Next Steps

Task 06 successfully achieves full Minimum Viable Product (MVP) operational readiness:
- All 5 MVP modules (Event Entry, Searchable Dashboard, Summary Indicators, Status Drill-Down, Alert & Exception View) implemented and backed by real database data.
- Interactive, responsive web dashboard served at `http://localhost:8081` without host port 8080 collision.
- 22 automated tests authored and verified passing with zero regressions.
- Complete documentation compiled in `REPORTS/Task-06-MVP-Completion.md`.

### Handoff to Next Milestone:
Upon student review and approval of Task 06 deliverables, the project will advance to **Task 07: Containerization & Dockerfile Setup**, where the multi-stage Docker container image and service orchestration will be validated and hardened.
