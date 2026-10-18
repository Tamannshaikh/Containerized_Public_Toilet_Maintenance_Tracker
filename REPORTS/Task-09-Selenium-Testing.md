# Task 09: Selenium Automated UI Testing

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
- **Current Task:** Task 09 – Selenium Automated UI Testing
- **Active Branch:** `feature/maintenance-ticket-api`
- **Document Version:** 1.0.0
- **Status:** Completed & Verified (5/5 Critical User Journeys Passing, 27/27 Total Test Suite Passing, Local Headless Chrome Execution, Visual Checkpoints & Automated Failure Screenshot Watcher Implemented)

---

## 1. Aim and Objectives

### 1.1 Aim
To design, implement, execute, and verify automated end-to-end browser test suites using **Selenium WebDriver** for the **Containerized Public Toilet Maintenance Tracker**, validating critical citizen and municipal operator workflows against the live responsive Single Page Application (SPA), frontend DOM components, and RESTful APIs under headless browser execution on Windows.

### 1.2 Objectives
1. **Application & UI Architecture Inspection:** Analyze the actual Spring Boot web layer, frontend resources (`src/main/resources/static/index.html`), REST controllers, and data models to formulate realistic automated test scenarios.
2. **Critical User Journey Formulation:** Identify and automate 5 distinct, high-impact user journeys representing citizen defect reporting, facility discovery, operational analytics, ticket lifecycle transitions, and sanitation shift logging.
3. **Headless Browser & Driver Management:** Configure Selenium 4.19.1 with automated driver resolution via Selenium Manager, utilizing Google Chrome (`--headless=new`) with fallback to Microsoft Edge on Windows 11.
4. **Robust Synchronization & Assertions:** Eliminate brittle fixed sleep delays by implementing explicit wait strategies (`WebDriverWait` and `ExpectedConditions`), verifying DOM element visibility, text presence, and modal state transitions.
5. **Automated Failure & Checkpoint Screenshots:** Implement a custom JUnit 5 `AfterTestExecutionCallback` extension to automatically capture full-page viewport screenshots on test failures into `docs/screenshots/Task-09/`, while simultaneously recording visual evidence checkpoints for successful journeys.
6. **Local Execution Autonomy:** Ensure the Selenium test suite can execute independently via standard Maven commands (`mvn test -Dtest=PublicToiletSeleniumTest`) without requiring Jenkins or destructive database operations.
7. **Academic Documentation:** Document all test cases, input datasets, assertions, execution logs, and identified limitations for the college submission and viva evaluation.

---

## 2. Software, Tools, and Environment

| Tool / Technology | Version / Specification | Role in Task 09 |
| :--- | :--- | :--- |
| **Selenium WebDriver** | 4.19.1 (`selenium-java`) | Core browser automation framework for DOM manipulation and user interaction |
| **Google Chrome** | 155.0.8059.84 (win64) | Primary test execution browser (`C:\Program Files\Google\Chrome\Application\chrome.exe`) |
| **Microsoft Edge** | 155.0.4283.62 (win64) | Secondary fallback execution browser (`C:\Program Files (x86)\Microsoft\Edge\Application\msedge.exe`) |
| **ChromeDriver** | 155.0.8059.84 (`~/.cache/selenium/chromedriver`) | Browser driver managed automatically by Selenium Manager |
| **JUnit 5 (Jupiter)** | 5.10.2 (`junit-jupiter`) | Unit testing framework providing `@Test`, `@Order`, `@DisplayName`, and extension hooks |
| **Apache Maven** | 3.9.16 / Maven Surefire 3.2.5 | Build automation and test runner orchestrating compilation and test execution |
| **Java JDK** | OpenJDK 21 / Oracle JDK 26 | Test execution runtime environment |
| **Spring Boot** | 3.2.4 (Port 8081) | Live running backend application hosting REST APIs and serving `index.html` |
| **Operating System** | Windows 11 Pro 64-bit | Local host operating system |

---

## 3. Architecture & Test Design Strategy

```
+---------------------------------------------------------------------------------------+
|                                    TEST RUNNER                                        |
|                          mvn test -Dtest=PublicToiletSeleniumTest                     |
+---------------------------------------------------------------------------------------+
                                           |
                                           v
+---------------------------------------------------------------------------------------+
|                           PublicToiletSeleniumTest (JUnit 5)                          |
|  - @BeforeAll: Port discovery (8081 / 8082) & HTTP health verification (/api/v1/health)|
|  - @BeforeEach: Headless Chrome Options (--headless=new, --window-size=1920,1080)     |
|  - @AfterEach: Clean driver teardown                                                  |
|  - Extension: ScreenshotOnFailureExtension (Captures PNG on assertion failure)        |
+---------------------------------------------------------------------------------------+
                                           |
                    +----------------------+----------------------+
                    |                      |                      |
                    v                      v                      v
          [Chrome Browser (Headless)] [Explicit Waits]   [Local Storage & APIs]
                    |                      |                      |
                    +----------------------+----------------------+
                                           |
                                           v
+---------------------------------------------------------------------------------------+
|               LIVE WEB APPLICATION (http://localhost:8081/index.html)                 |
|  [Header & Attribution] [KPI Metrics Grid] [Tab Bar Navigation] [Interactive Modals]  |
|  [Tickets Table Body]   [Facilities Table] [Cleaning Logs Table] [Toast Alerts]       |
+---------------------------------------------------------------------------------------+
                                           |
                                           v
+---------------------------------------------------------------------------------------+
|                             ARTIFACT GENERATION & EVIDENCE                            |
|  - Checkpoint PNGs: docs/screenshots/Task-09/task09_journey*.png                      |
|  - Surefire XML & TXT Reports: target/surefire-reports/                               |
+---------------------------------------------------------------------------------------+
```

### 3.1 Headless Configuration for Windows
To ensure tests execute reliably in headless mode on Windows without requiring a graphical desktop display, the following Chrome flags were configured:
- `--headless=new`: Utilizes the modernized native headless implementation in Chromium 155.
- `--disable-gpu`: Disables hardware acceleration preventing Windows Direct3D rendering conflicts.
- `--no-sandbox` & `--disable-dev-shm-usage`: Prevents shared memory partition crashes.
- `--window-size=1920,1080`: Forces a standardized desktop 1080p viewport so all responsive CSS grid columns and modal components render consistently.
- `--remote-allow-origins=*`: Ensures WebSocket communication between ChromeDriver and browser remains unblocked.

### 3.2 Dual-Tier Port Discovery
The test suite dynamically probes target application ports:
1. Reads `System.getProperty("test.server.port")` (defaults to `8081`).
2. Performs an HTTP probe to `http://localhost:8081/api/v1/health`.
3. If port 8081 is unavailable, it automatically falls back to staging port `8082`.
4. Uses JUnit 5 `Assumptions.assumeTrue(...)` to ensure tests only execute when the server is healthy, preventing false failures.

---

## 4. Critical User Journeys & Test Scenarios

The suite models 5 primary user workflows supported by the live application:

### Journey 1: Operational KPI Dashboard Load & Summary Rendering
- **Objective:** Verify page metadata, candidate academic attribution, real-time KPI counter cards, and default table loading.
- **Workflow:**
  1. Navigate to `http://localhost:8081/index.html`.
  2. Assert title equals `"Containerized Public Toilet Maintenance Tracker"`.
  3. Assert academic header contains candidate name `"Tamanna Shaikh"` and roll number `"24102A2006"`.
  4. Wait for `#kpiTotalFacilities` to load numeric metric $\ge 3$.
  5. Assert `#kpiActiveTickets` is loaded and `#kpiCleanlinessScore` contains `"%"`.
  6. Assert initial tickets table `#ticketsTableBody` is populated with seeded records.
- **Evidence Checkpoint:** `docs/screenshots/Task-09/task09_journey1_dashboard_kpis.png`

### Journey 2: Tab Navigation & Facility Directory Search / Ward Filtering
- **Objective:** Verify client-side SPA view switching, live text filtering, and ward select filtering.
- **Workflow:**
  1. Click `"Facility Directory"` tab button.
  2. Verify `#facilitiesView` receives `.active` class.
  3. Verify `#facilitiesTableBody` renders 3 municipal facilities.
  4. Enter `"Dadar"` in `#facilitySearchInput` and trigger keyup filter.
  5. Assert table displays only Dadar facility (`FAC-MUM-001`).
  6. Clear search input, select `"K/East"` in `#facilityWardFilter`.
  7. Assert table displays only Andheri East facility (`FAC-MUM-003`).
- **Evidence Checkpoint:** `docs/screenshots/Task-09/task09_journey2_facility_search.png`

### Journey 3: Citizen Defect Ticket Submission Workflow
- **Objective:** Validate interactive modal form submission for reporting maintenance defects.
- **Workflow:**
  1. Click `"+ Log New Defect"` action button in header tab bar.
  2. Verify modal `#createTicketModal` opens with `display: flex`.
  3. Select target facility from `#createTicketFacility` dropdown.
  4. Fill in Issue Title, Description, Priority (`HIGH`), and Reporter Name.
  5. Submit form via `#createTicketForm button[type='submit']`.
  6. Assert toast notification confirms `"Maintenance Ticket logged successfully!"`.
  7. Verify modal closes and new ticket title appears in `#ticketsTableBody`.
- **Evidence Checkpoint:** `docs/screenshots/Task-09/task09_journey3_ticket_created.png`

### Journey 4: Status Drill-Down & Ticket Lifecycle Transition
- **Objective:** Verify detailed ticket inspection and municipal lifecycle state transitions (`OPEN` $\rightarrow$ `IN_PROGRESS` $\rightarrow$ `RESOLVED`).
- **Workflow:**
  1. Locate target ticket in `#ticketsTableBody` and click `"Drill Down"`.
  2. Verify `#ticketDetailModal` displays ticket number starting with `TKT-`.
  3. Click `"▶ Start Work"` button.
  4. Wait for toast notification `"Ticket status updated to IN_PROGRESS"`.
  5. Reopen modal and click `"✓ Mark Resolved"` button.
  6. Wait for toast notification `"Ticket status updated to RESOLVED"`.
  7. Verify modal closes and `#ticketsTableBody` renders updated green `RESOLVED` badge.
- **Evidence Checkpoint:** `docs/screenshots/Task-09/task09_journey4_ticket_resolved.png`

### Journey 5: Sanitation Cleaning Shift Log Submission
- **Objective:** Validate sanitary crew checklist recording, hygiene score calculation, and shift log table verification.
- **Workflow:**
  1. Click `"+ Log Cleaning Shift"` tab button.
  2. Verify modal `#createCleaningModal` opens.
  3. Select facility from `#cleanFacility`.
  4. Input Sanitarian Staff Name, verify Running Water and Soap checkmarks are active.
  5. Select Hygiene Rating = `"5"` (Spotless & Sanitized) and enter shift remarks.
  6. Submit form and verify toast notification `"Cleaning shift recorded!"`.
  7. Navigate to `"Cleaning Shift Logs"` tab view.
  8. Assert new shift record appears in `#cleaningTableBody` with cleaner name, rating `5/5`, and remarks.
- **Evidence Checkpoint:** `docs/screenshots/Task-09/task09_journey5_cleaning_shift_logged.png`

---

## 5. Test Implementation Details

### 5.1 Test Suite Class: `PublicToiletSeleniumTest.java`
Located at: `src/test/java/com/tracker/toilet/selenium/PublicToiletSeleniumTest.java`
- Package: `com.tracker.toilet.selenium`
- Annotations:
  - `@ExtendWith(ScreenshotOnFailureExtension.class)`: Registers automated screenshot capture on test failure.
  - `@TestMethodOrder(MethodOrderer.OrderAnnotation.class)`: Guarantees deterministic sequential journey execution (`@Order(1)` through `@Order(5)`).
- Synchronization: All element interactions utilize `WebDriverWait(driver, Duration.ofSeconds(10))` with `ExpectedConditions` (`visibilityOfElementLocated`, `textToBePresentInElementLocated`, `invisibilityOfElementLocated`, `elementToBeClickable`).

### 5.2 Failure Screenshot Extension: `ScreenshotOnFailureExtension.java`
Located at: `src/test/java/com/tracker/toilet/selenium/ScreenshotOnFailureExtension.java`
- Implements `org.junit.jupiter.api.extension.AfterTestExecutionCallback` and `TestWatcher`.
- Uses `AfterTestExecutionCallback.afterTestExecution(context)` to capture viewport screenshots immediately when an assertion or runtime exception occurs, **before** `@AfterEach` teardown destroys the browser session.
- Output path format: `docs/screenshots/Task-09/failure_<TestClass>_<TestMethod>_<yyyyMMdd_HHmmss>.png`.

---

## 6. Test Cases and Assertions Matrix

| Test Case Name | Target Component | Input Test Data | Expected Assertion / Success Criteria | Status |
| :--- | :--- | :--- | :--- | :--- |
| `testOperationalKpiDashboardLoad` | Dashboard Page & Header | URL: `http://localhost:8081/index.html` | Page title matches; Student name & roll no present; Total facilities $\ge 3$; Cleanliness score ends with `%`; Table populated | **PASSED** (2.89s) |
| `testFacilityDirectoryNavigationAndSearch` | Facility Directory Tab | Query: `"Dadar"`, Ward: `"K/East"` | Tab active; Table size $\ge 3$; Search filters to `FAC-MUM-001`; Ward filter matches `FAC-MUM-003` | **PASSED** (2.73s) |
| `testCitizenDefectTicketSubmission` | Defect Modal Form | Title: `Leaking Flush Cistern Cabin A`, Priority: `HIGH`, Reporter: `Tamanna Shaikh` | Modal opens; Toast confirms creation; Modal closes; Ticket title appears in table with `HIGH` priority badge | **PASSED** (3.11s) |
| `testTicketStatusLifecycleTransition` | Ticket Detail Modal | Status actions: `IN_PROGRESS`, `RESOLVED` | Modal displays `TKT-` prefix; Toasts confirm `IN_PROGRESS` and `RESOLVED`; Table reflects `RESOLVED` badge | **PASSED** (4.16s) |
| `testCleaningShiftLogSubmission` | Cleaning Log Modal & Tab | Cleaner: `Lead Sanitarian Suresh`, Rating: `5`, Water: `true`, Soap: `true` | Toast confirms shift logged; Cleaning tab displays staff name, `5/5` rating, and remarks | **PASSED** (3.78s) |

---

## 7. Execution Commands & Actual Results

### 7.1 Dedicated Selenium Test Execution Command
```powershell
mvn test -Dtest=PublicToiletSeleniumTest
```

### 7.2 Actual Surefire Test Console Output
```text
[INFO] Scanning for projects...
[INFO] 
[INFO] ----< com.tracker.toilet.devops:public-toilet-maintenance-tracker >-----
[INFO] Building Containerized_Public_Toilet_Maintenance_Tracker 1.0.0-SNAPSHOT
[INFO]   from pom.xml
[INFO] --------------------------------[ jar ]---------------------------------
[INFO] 
[INFO] --- resources:3.4.0:resources (default-resources) @ public-toilet-maintenance-tracker ---
[INFO] Copying 2 resources from src\main\resources to target\classes
[INFO] 
[INFO] --- compiler:3.15.0:compile (default-compile) @ public-toilet-maintenance-tracker ---
[INFO] Nothing to compile - all classes are up to date.
[INFO] 
[INFO] --- resources:3.4.0:testResources (default-testResources) @ public-toilet-maintenance-tracker ---
[INFO] skip non existing resourceDirectory C:\Users\Sajiya\Desktop\Containerized_Public_Toilet_Maintenance_Tracker\src\test\resources
[INFO] 
[INFO] --- compiler:3.15.0:testCompile (default-testCompile) @ public-toilet-maintenance-tracker ---
[INFO] Compiling 8 source files with javac [debug parameters target 17] to target\test-classes
[INFO] 
[INFO] --- surefire:3.2.5:test (default-test) @ public-toilet-maintenance-tracker ---
[INFO] Using auto detected provider org.apache.maven.surefire.junitplatform.JUnitPlatformProvider
[INFO] 
[INFO] -------------------------------------------------------
[INFO]  T E S T S
[INFO] -------------------------------------------------------
[INFO] Running com.tracker.toilet.selenium.PublicToiletSeleniumTest
[SELENIUM] Verified target application running at: http://localhost:8081
[SELENIUM EVIDENCE] Captured checkpoint screenshot: docs\screenshots\Task-09\task09_journey1_dashboard_kpis.png
[SELENIUM] Test PASSED: Journey 1: Operational KPI Dashboard Load & Metric Rendering
[SELENIUM EVIDENCE] Captured checkpoint screenshot: docs\screenshots\Task-09\task09_journey2_facility_search.png
[SELENIUM] Test PASSED: Journey 2: Tab Navigation & Facility Directory Search and Ward Filter
[SELENIUM EVIDENCE] Captured checkpoint screenshot: docs\screenshots\Task-09\task09_journey3_ticket_created.png
[SELENIUM] Test PASSED: Journey 3: Citizen Defect Ticket Submission Workflow via Modal
[SELENIUM EVIDENCE] Captured checkpoint screenshot: docs\screenshots\Task-09\task09_journey4_ticket_resolved.png
[SELENIUM] Test PASSED: Journey 4: Ticket Status Drill-Down & Lifecycle State Transition
[SELENIUM EVIDENCE] Captured checkpoint screenshot: docs\screenshots\Task-09\task09_journey5_cleaning_shift_logged.png
[SELENIUM] Test PASSED: Journey 5: Sanitation Cleaning Shift Log Submission
[INFO] Tests run: 5, Failures: 0, Errors: 0, Skipped: 0, Time elapsed: 17.73 s -- in com.tracker.toilet.selenium.PublicToiletSeleniumTest
[INFO] 
[INFO] Results:
[INFO] 
[INFO] Tests run: 5, Failures: 0, Errors: 0, Skipped: 0
[INFO] 
[INFO] ------------------------------------------------------------------------
[INFO] BUILD SUCCESS
[INFO] ------------------------------------------------------------------------
[INFO] Total time:  24.181 s
[INFO] Finished at: 2026-10-19T01:01:08+05:30
[INFO] ------------------------------------------------------------------------
```

### 7.3 Complete Project Test Suite Execution
```powershell
mvn test
```
- **Total Test Classes Executed:** 7 (`CleaningLogControllerTest`, `DashboardControllerTest`, `FacilityControllerTest`, `HealthControllerTest`, `MaintenanceTicketControllerTest`, `PublicToiletSeleniumTest`, `ToiletMaintenanceApplicationTests`)
- **Total Tests Run:** 27
- **Passed:** 27
- **Failures:** 0
- **Errors:** 0
- **Skipped:** 0
- **Build Outcome:** `BUILD SUCCESS` (32.420 s total time)

---

## 8. Artifacts & Visual Evidence

All execution proof and automated visual checkpoints are stored in the dedicated directory: `docs/screenshots/Task-09/`

| Screenshot File | Resolution | File Size | Description |
| :--- | :--- | :--- | :--- |
| `docs/screenshots/Task-09/task09_journey1_dashboard_kpis.png` | 1920x1080 | 471 KB | Operational KPI summary cards, academic attribution banner, and seeded tickets table |
| `docs/screenshots/Task-09/task09_journey2_facility_search.png` | 1920x1080 | 454 KB | Facility Directory tab showing live keyword search (`Dadar`) and Ward filtering (`K/East`) |
| `docs/screenshots/Task-09/task09_journey3_ticket_created.png` | 1920x1080 | 485 KB | Citizen defect submission modal and updated ticket table displaying newly logged ticket |
| `docs/screenshots/Task-09/task09_journey4_ticket_resolved.png` | 1920x1080 | 486 KB | Ticket detail modal drill-down and status transition confirming `RESOLVED` lifecycle state |
| `docs/screenshots/Task-09/task09_journey5_cleaning_shift_logged.png` | 1920x1080 | 467 KB | Sanitation shift log modal checklist submission and verified record in shift table |

---

## 9. Limitations & Edge Cases Handled

1. **CDP Matching on Newer Chrome Versions:**
   - *Observation:* Chromium 155 emits standard Selenium CDP warnings (`Unable to find CDP implementation matching 155`).
   - *Resolution:* Selenium automatically falls back to W3C WebDriver BiDi / standard HTTP commands, executing all actions with zero degradation.
2. **Toast Notification Timing Overlaps:**
   - *Observation:* Rapid sequential modal actions (e.g., transitioning ticket to `IN_PROGRESS` followed immediately by `RESOLVED`) can cause assertions to evaluate against the previous toast message before the asynchronous `PATCH` fetch completes.
   - *Resolution:* Implemented `ExpectedConditions.textToBePresentInElementLocated(By.id("toast"), expectedText)` which reliably awaits the exact state transition text.
3. **JUnit 5 Lifecycle for Failure Screenshots:**
   - *Observation:* Using standard `TestWatcher.testFailed()` executes after `@AfterEach`, which has already called `driver.quit()`.
   - *Resolution:* Implemented `AfterTestExecutionCallback.afterTestExecution()` to capture screenshots immediately after an assertion failure while the browser window and driver instance are still alive.
4. **Non-Destructive Test Execution:**
   - Tests operate against the running application without wiping database state or dropping tables, adhering strictly to enterprise testing best practices.

---

## 10. Conclusion & Next Steps

Task 09 is completed. A production-grade, headless-compatible Selenium automated test suite has been designed, implemented, and verified locally using Maven against the live **Containerized Public Toilet Maintenance Tracker** application.

- All 5 critical user journeys execute with high fidelity.
- Automated failure screenshot capturing and visual checkpoint recording are active.
- Existing Task 08 Jenkinsfile, deployment scripts, and ports (8080, 8081, 8082) remain strictly preserved and unmodified.
- Ready for Task 10: Continuous Testing Integration into the Jenkins CI/CD Pipeline.
