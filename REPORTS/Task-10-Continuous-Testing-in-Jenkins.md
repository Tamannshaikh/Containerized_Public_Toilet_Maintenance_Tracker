# Task 10: Continuous Testing in Jenkins

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
- **Current Task:** Task 10 – Continuous Testing in Jenkins
- **Active Branch:** `feature/maintenance-ticket-api`
- **Document Version:** 1.0.0
- **Status:** Verified & Operational (Continuous Testing Integrated into Jenkins Pipeline, Stage 4 Unit/Integration Tests Isolated, Staging Deployed on Port 8082, Stage 9 Continuous Selenium E2E Tests Executed, Controlled Failure & Recovery Cycle Demonstrated, 27/27 Tests Aggregated in JUnit Reports)

---

## 1. Aim and Objectives

### 1.1 Aim
To configure, integrate, verify, and document automated **Continuous Testing** within the Jenkins CI/CD pipeline for the **Containerized Public Toilet Maintenance Tracker**, establishing an automated test verification gate that executes fast-feedback unit and controller tests prior to packaging, deploys the verified artifact to an isolated staging environment (Port 8082), executes headless Selenium browser user journeys against the live staging application, aggregates all test results into Jenkins JUnit reports, and demonstrates regression capture through a controlled test failure and recovery cycle.

### 1.2 Objectives
1. **Pipeline Architecture Refinement:** Extend the 8-stage Task 08 Jenkins deployment pipeline to include a dedicated **Stage 9: Continuous Selenium UI Testing** stage following staging deployment and initial smoke test verification.
2. **Test Separation & Execution Order:** 
   - Restrict **Stage 4 (Run Automated Tests)** to fast-feedback unit and integration tests (`mvn test "-Dtest=!PublicToiletSeleniumTest"`) to validate business logic before compilation and artifact packaging.
   - Defer end-to-end browser tests to **Stage 9**, executing against the live staging environment on port `8082` after successful deployment.
3. **Headless Browser Execution in Jenkins:** Configure Selenium WebDriver to operate reliably under the Jenkins Windows Service (`LocalSystem` account) using Google Chrome in headless mode (`--headless=new`, `--no-sandbox`, `--disable-dev-shm-usage`, `--disable-gpu`).
4. **Offline Driver Resolution:** Provide automated driver resolution with direct fallback to the local cached ChromeDriver (`155.0.8059.84`), ensuring deterministic execution without runtime network dependency.
5. **Unified JUnit Test Reporting:** Configure the Jenkins `junit` publisher (`target/surefire-reports/*.xml`) in the `post { always { ... } }` block to aggregate all 27 tests (22 unit/integration tests + 5 Selenium user journeys), providing comprehensive pass/fail statistics and historical trends on the Jenkins dashboard.
6. **Controlled Failure & Recovery Demonstration:** Safely simulate a defect regression in an automated test, demonstrate pipeline termination at the testing gate with visual evidence capture via `ScreenshotOnFailureExtension`, and verify pipeline recovery after defect remediation.
7. **Port & System Protection:** Strictly preserve Jenkins on port `8080` and the active baseline application on port `8081` without disruption.

---

## 2. Software, Tools, and Environment

| Tool / Technology | Version / Specification | Role in Task 10 |
| :--- | :--- | :--- |
| **Jenkins Automation Server** | 2.568.2 (Windows Service, Port 8080) | Orchestrates the 9-stage declarative CI/CD and Continuous Testing pipeline |
| **Jenkins JUnit Plugin** | JUnit Plugin 1324.v0f0c051d91 | Aggregates Maven Surefire XML test reports and renders interactive test trends |
| **Apache Maven** | 3.9.16 / Surefire Plugin 3.2.5 | Executes Java unit tests, packages JAR artifacts, and executes Selenium suites |
| **Selenium WebDriver** | 4.19.1 (`selenium-java`) | Automates browser interaction, validates DOM elements, and asserts UI state |
| **Google Chrome** | 155.0.8059.84 (win64) | Headless test execution browser (`C:\Program Files\Google\Chrome\Application\chrome.exe`) |
| **ChromeDriver** | 155.0.8059.84 | WebDriver executable for Chrome communication |
| **JUnit 5 (Jupiter)** | 5.10.2 | Testing framework providing assertions, order control, and test execution callbacks |
| **Java JDK** | OpenJDK 21 / Oracle JDK 26 | Compilation and runtime environment for Spring Boot and test suites |
| **Spring Boot** | 3.2.4 (Spring Web, Data JPA, Actuator) | Backend application powering REST APIs and serving the responsive SPA |
| **PowerShell** | 5.1 / 7.x | Process management, staging startup script execution, and health probes |
| **Operating System** | Windows 11 Pro 64-bit | Local host operating system |

---

## 3. Architecture & Continuous Testing Lifecycle

```
+---------------------------------------------------------------------------------------------------+
|                                  Jenkins Continuous Testing Pipeline                              |
|                                                                                                   |
|  [Stage 1: Checkout SCM]                                                                          |
|       |                                                                                           |
|  [Stage 2: Verify Environment] (Java JDK 21, Maven, Chrome)                                       |
|       |                                                                                           |
|  [Stage 3: Compile]                                                                               |
|       |                                                                                           |
|  [Stage 4: Unit & Integration Tests] ----------------------------+                                |
|       |  mvn test "-Dtest=!PublicToiletSeleniumTest"             | Fast Feedback Gate             |
|       |  (22 Tests: Controllers, Health, Repositories)           | (Fails early if code broken)   |
|       v                                                          +--------------------------------+
|  [Stage 5: Package Application] (mvn package -DskipTests)                                         |
|       |                                                                                           |
|  [Stage 6: Archive Artifacts] (target/*.jar)                                                      |
|       |                                                                                           |
|  [Stage 7: Deploy Application to Staging] (Port 8082 via deploy-app.ps1)                          |
|       |                                                                                           |
|  [Stage 8: Smoke Test & Health Verification] (verify-health.ps1: HTTP 200, status: UP)            |
|       |                                                                                           |
|  [Stage 9: Continuous Selenium UI Testing] -----------------------+                               |
|       |  mvn test -Dtest=PublicToiletSeleniumTest                | Live Acceptance Gate           |
|       |           "-Dtest.server.port=8082"                      | (Validates real browser UI)    |
|       |           "-Dtest.screenshot.dir=docs/screenshots/Task-10|                                |
|       |  (5 Critical User Journeys against Port 8082)            |                                |
|       v                                                          +--------------------------------+
|  [Post-Action: Publish JUnit Test Reports]                                                        |
|       junit testResults: 'target/surefire-reports/*.xml'                                          |
|       (Aggregates all 27 tests, records trends, displays failure diffs)                           |
+---------------------------------------------------------------------------------------------------+
```

### 3.1 Separation of Testing Concerns
In modern Continuous Integration and Continuous Testing pipelines, running end-to-end browser tests before an application is packaged or deployed leads to fundamental architectural flaws (testing an old process or failing because no server is listening). 

Task 10 resolves this by splitting tests across the lifecycle:
1. **Pre-Build Gate (Stage 4):** Executes fast unit and controller mock tests (22 tests) in under 15 seconds. If business logic is defective, the pipeline fails immediately before spending time compiling the full package.
2. **Post-Deployment Gate (Stage 9):** Executes automated browser user journeys against the deployed, running staging artifact on port `8082`. This validates end-to-end integration: Spring Boot runtime, static resources, DOM bindings, and REST API communication.

---

## 4. Pipeline Configuration & Implementation

### 4.1 Refined `Jenkinsfile` Stages

The declarative pipeline integrates continuous testing across Stage 4 and Stage 9, concluding with JUnit report publication in the `post` block:

```groovy
// Pre-Deployment Unit & Integration Test Gate
stage('Run Automated Tests') {
    steps {
        echo '====================================================='
        echo 'STAGE 4: Executing automated unit & integration tests'
        echo '====================================================='
        bat 'mvn test "-Dtest=!PublicToiletSeleniumTest"'
    }
}

// Staging Deployment & Verification (Preserves Port 8081)
stage('Deploy Application') {
    when {
        expression {
            return params.AUTO_DEPLOY == true && params.DEPLOY_ENV != 'production'
        }
    }
    steps {
        script {
            def targetPort = params.DEPLOY_PORT ?: '8082'
            withEnv(["JENKINS_NODE_COOKIE=dontKillMe", "BUILD_ID=dontKillMe"]) {
                bat "powershell -ExecutionPolicy Bypass -File deploy\\staging\\deploy-app.ps1 -Port ${targetPort}"
            }
        }
    }
}

stage('Smoke Test and Health Verification') {
    when {
        expression {
            return params.AUTO_DEPLOY == true && params.DEPLOY_ENV != 'production'
        }
    }
    steps {
        script {
            def targetPort = env.ACTIVE_DEPLOY_PORT ?: params.DEPLOY_PORT
            bat "powershell -ExecutionPolicy Bypass -File deploy\\staging\\verify-health.ps1 -Port ${targetPort}"
        }
    }
}

// Continuous UI Testing Gate against Live Staging Port 8082
stage('Continuous Selenium UI Testing') {
    when {
        expression {
            return params.AUTO_DEPLOY == true && params.DEPLOY_ENV != 'production'
        }
    }
    steps {
        echo '====================================================='
        echo "STAGE 9: Executing Continuous Selenium E2E Tests on Port ${env.ACTIVE_DEPLOY_PORT ?: params.DEPLOY_PORT}"
        echo '====================================================='
        script {
            def targetPort = env.ACTIVE_DEPLOY_PORT ?: params.DEPLOY_PORT
            echo "Targeting Staging Application on Port: ${targetPort}"
            bat """
                @echo off
                echo [SELENIUM] Running automated browser tests against http://localhost:${targetPort}...
                mvn test -Dtest=PublicToiletSeleniumTest "-Dtest.server.port=${targetPort}" "-Dtest.screenshot.dir=docs/screenshots/Task-10"
            """
        }
    }
}

post {
    always {
        junit allowEmptyResults: true, testResults: 'target/surefire-reports/*.xml'
    }
}
```

### 4.2 Dynamic Driver Resolution & LocalSystem Compatibility

To ensure full portability across user accounts and the Jenkins Windows Service (`LocalSystem`), `PublicToiletSeleniumTest.java` relies natively on **Selenium Manager** (Selenium 4.19.1) without hardcoding user-specific file paths. 

Headless Chromium execution is configured with flags optimized for non-interactive service environments:

```java
ChromeOptions chromeOptions = new ChromeOptions();
chromeOptions.addArguments("--headless=new");
chromeOptions.addArguments("--disable-gpu");
chromeOptions.addArguments("--no-sandbox");
chromeOptions.addArguments("--disable-dev-shm-usage");
chromeOptions.addArguments("--window-size=1920,1080");
chromeOptions.addArguments("--remote-allow-origins=*");
chromeOptions.addArguments("--disable-extensions");
return new ChromeDriver(chromeOptions);
```

### 4.3 Backward Compatibility for Task 09 Screenshots

To ensure that Task 09 tests continue to function identically when run without pipeline properties, `getScreenshotDir()` defaults to `docs/screenshots/Task-09`. Task 10 routing to `docs/screenshots/Task-10` is activated dynamically only when the pipeline passes `-Dtest.screenshot.dir=docs/screenshots/Task-10`:

```java
public static String getScreenshotDir() {
    return System.getProperty("test.screenshot.dir", "docs/screenshots/Task-09");
}
```

---

## 5. Test Suite Verification & Execution Results

### 5.1 Stage 4: Pre-Deployment Unit & Integration Tests (22 Tests)

Execution Command:
```bash
mvn test "-Dtest=!PublicToiletSeleniumTest"
```

| Test Class | Tests Run | Failures | Errors | Skipped | Status |
| :--- | :---: | :---: | :---: | :---: | :---: |
| `CleaningLogControllerTest` | 4 | 0 | 0 | 0 | **PASSED** |
| `DashboardControllerTest` | 3 | 0 | 0 | 0 | **PASSED** |
| `FacilityControllerTest` | 5 | 0 | 0 | 0 | **PASSED** |
| `HealthControllerTest` | 3 | 0 | 0 | 0 | **PASSED** |
| `MaintenanceTicketControllerTest` | 6 | 0 | 0 | 0 | **PASSED** |
| `ToiletMaintenanceApplicationTests` | 1 | 0 | 0 | 0 | **PASSED** |
| **Total Unit/Integration Suite** | **22** | **0** | **0** | **0** | **100% PASSED** |

### 5.2 Stage 9: Continuous Selenium Browser User Journeys (5 Tests)

Execution Command:
```bash
mvn test -Dtest=PublicToiletSeleniumTest "-Dtest.server.port=8082" "-Dtest.screenshot.dir=docs/screenshots/Task-10"
```

| Order | Test Method | User Journey Description | Port Target | Checkpoint Screenshot | Status |
| :---: | :--- | :--- | :---: | :--- | :---: |
| 1 | `testOperationalKpiDashboardLoad` | Operational KPI Dashboard Load & Metric Rendering | `8082` | `task10_journey1_dashboard_kpis.png` | **PASSED** |
| 2 | `testFacilityDirectoryNavigationAndSearch` | Tab Navigation & Facility Directory Search / Ward Filter | `8082` | `task10_journey2_facility_search.png` | **PASSED** |
| 3 | `testCitizenTicketCreationWorkflow` | Citizen Defect Ticket Submission Workflow via Modal | `8082` | `task10_journey3_ticket_created.png` | **PASSED** |
| 4 | `testTicketStatusDrillDownAndLifecycle` | Ticket Status Drill-Down & Lifecycle State Transition | `8082` | `task10_journey4_ticket_resolved.png` | **PASSED** |
| 5 | `testCleaningShiftLogSubmission` | Sanitation Cleaning Shift Log Submission | `8082` | `task10_journey5_cleaning_shift_logged.png` | **PASSED** |
| **Total** | **5 User Journeys** | **End-to-End Automated Browser Testing** | **8082** | **5 Checkpoints Captured** | **100% PASSED** |

### 5.3 Consolidated Jenkins JUnit Metrics
- **Total Tests Aggregated:** 27
- **Passing Tests:** 27
- **Failures:** 0
- **Errors:** 0
- **Test Pass Rate:** 100.0%

---

## 6. Controlled Failure Demonstration & Regression Analysis

A core tenet of Continuous Testing is proving that the test automation harness actively detects regressions and halts the pipeline before defective code reaches production.

### 6.1 Intentional Defect Injection
To simulate a UI regression, an intentional assertion mismatch was injected into User Journey 1:

```java
// Simulating an unexpected title regression
assertEquals("Intentional Regression Defect - Nonexistent Title", driver.getTitle(),
        "Controlled continuous testing failure demonstration.");
```

### 6.2 Pipeline Failure Behavior
When the Continuous UI Testing stage executed:
1. **Assertion Failure Detected:** Selenium observed page title `"Containerized Public Toilet Maintenance Tracker"`, which failed the expectation `"Intentional Regression Defect - Nonexistent Title"`.
2. **Automated Screenshot Capture:** The `ScreenshotOnFailureExtension` immediately triggered before driver teardown, capturing `task10_failure_demo_screenshot.png` into `docs/screenshots/Task-10/`.
3. **Surefire Report Generation:** Maven generated `TEST-com.tracker.toilet.selenium.PublicToiletSeleniumTest.xml` containing the failure trace:
   ```xml
   <failure message="Controlled continuous testing failure demonstration. ==&gt; expected: &lt;Intentional Regression Defect - Nonexistent Title&gt; but was: &lt;Containerized Public Toilet Maintenance Tracker&gt;" type="org.opentest4j.AssertionFailedError">
   ```
4. **Build Termination:** The test stage returned exit code 1, immediately halting the pipeline.
5. **Report Publication:** The `post { always { junit ... } }` block published the test results, accurately marking 1 test failed and 26 tests passed on the Jenkins dashboard.

### 6.3 Defect Remediation & Pipeline Recovery
1. The intentional assertion defect was reverted back to the verified specification:
   ```java
   assertEquals("Containerized Public Toilet Maintenance Tracker", driver.getTitle(),
           "Page title should match project specification.");
   ```
2. The pipeline was re-executed.
3. All 5 Selenium tests passed, bringing the total suite to **27 passed, 0 failures, 0 errors**.
4. The pipeline completed with status **SUCCESS** (Green).

---

## 7. Port Preservation & Environmental Isolation

Throughout Task 10, strict port isolation was maintained across all services:

| Port | Service Identity | PID / Process | Isolation Status |
| :---: | :--- | :--- | :--- |
| **8080** | Jenkins Automation Server | PID 7880 (`jenkins.exe`) | Active, undamaged, continuous execution |
| **8081** | Active Application Baseline | PID 7836 (`java.exe`) | Preserved, untouched, zero downtime |
| **8082** | Staging Application Instance | PID 15232 (`java.exe`) | Deployed by Stage 7, verified by Stage 8, tested by Stage 9 |

---

## 8. Screenshot Checklist for Submission

All visual evidence has been saved in `docs/screenshots/Task-10/` with descriptive filenames:

| # | Filename | Description | Verified |
| :---: | :--- | :--- | :---: |
| 1 | `task10_journey1_dashboard_kpis.png` | Visual checkpoint of Operational KPI Dashboard running on port 8082 | [x] |
| 2 | `task10_journey2_facility_search.png` | Visual checkpoint of Facility Directory search and Ward filter | [x] |
| 3 | `task10_journey3_ticket_created.png` | Visual checkpoint of Citizen Defect Ticket modal submission | [x] |
| 4 | `task10_journey4_ticket_resolved.png` | Visual checkpoint of Ticket Lifecycle resolution drill-down | [x] |
| 5 | `task10_journey5_cleaning_shift_logged.png` | Visual checkpoint of Sanitation Cleaning Shift log submission | [x] |
| 6 | `task10_failure_demo_screenshot.png` | Automated failure screenshot captured by `ScreenshotOnFailureExtension` | [x] |

---

## 9. Result and Conclusion

Task 10 (Continuous Testing in Jenkins) has been successfully implemented and verified for the **Containerized Public Toilet Maintenance Tracker**. 

Key outcomes achieved:
1. **Pipeline Evolution:** Upgraded the 8-stage deployment pipeline to a 9-stage Continuous Testing pipeline with automated UI validation against live staging deployments.
2. **Lifecycle Optimization:** Separated fast unit testing (Stage 4) from end-to-end browser testing (Stage 9), maximizing developer feedback speed while guaranteeing full UI acceptance.
3. **Automated Reporting:** Unified 27 automated tests under the Jenkins JUnit test reporting system.
4. **Regression Resilience:** Validated failure detection, automated failure screenshot capture, and clean recovery mechanisms.
5. **Zero Disruption:** Kept all existing host services (Jenkins on port 8080 and active app on port 8081) fully operational.

---

## 10. Viva Questions and Answers

### Q1: What is Continuous Testing, and how does it differ from traditional automated testing?
**Ans:** Continuous Testing is the practice of executing automated tests as part of the software delivery pipeline at every stage of the lifecycle (pre-commit, build, post-deployment). Unlike traditional automated testing, which is often executed manually or at the end of a sprint, Continuous Testing provides immediate, automated feedback on code quality, catches regressions early, and acts as an automated quality gate preventing defective builds from reaching production.

### Q2: Why did we separate Unit Tests in Stage 4 from Selenium Tests in Stage 9?
**Ans:** 
1. **Execution Speed & Fast Feedback:** Unit and controller tests run in memory without a browser or web server and complete in ~10 seconds. If a syntax error or logic defect exists, the build fails immediately before wasting time packaging JARs or spinning up staging processes.
2. **Environmental Dependency:** Selenium end-to-end tests require a running HTTP server and browser engine. Running Selenium tests before deployment would either test an outdated process or fail due to the absence of a live server. Running them in Stage 9 ensures they test the newly deployed artifact on its actual staging port (`8082`).

### Q3: How does headless browser execution work in a CI/CD server like Jenkins?
**Ans:** Headless execution runs the browser engine (Chromium/Chrome) in the background without launching a visible Graphical User Interface (GUI) desktop window. In Windows services (like Jenkins running under the `LocalSystem` account, which lacks an interactive desktop session), headless mode (`--headless=new`, `--no-sandbox`, `--disable-gpu`) allows the browser to render the DOM, execute JavaScript, parse CSS, and take screenshots entirely in system memory.

### Q4: How does Jenkins capture and display test results from Maven?
**Ans:** Maven Surefire produces standardized XML test result files conforming to the JUnit XML schema in `target/surefire-reports/TEST-*.xml`. The Jenkins JUnit plugin reads these XML files using the declarative pipeline step `junit testResults: 'target/surefire-reports/*.xml'`, parses the test counts, execution durations, and stack traces, and renders interactive test trend graphs and detailed failure drill-downs on the Jenkins project dashboard.

### Q5: What is the purpose of the `ScreenshotOnFailureExtension` in the continuous testing suite?
**Ans:** When tests fail in a headless CI/CD environment, developers cannot inspect the browser visually. The `ScreenshotOnFailureExtension` implements JUnit 5's `AfterTestExecutionCallback`, automatically capturing a full-page PNG screenshot of the browser viewport at the exact moment an assertion fails. This visual artifact is saved to disk and can be archived by Jenkins, allowing engineers to quickly diagnose DOM layout issues, unexpected alert dialogs, or API error states without having to reproduce the failure manually.
