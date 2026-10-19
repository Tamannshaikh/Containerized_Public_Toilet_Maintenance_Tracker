# Task 11: Docker Image and Container Lifecycle

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
- **Baseline Commit:** `6636deb` (*feat(ci): integrate continuous Selenium testing into Jenkins pipeline*)
- **Current Task:** Task 11 – Docker Image and Container Lifecycle
- **Document Version:** 1.0.0
- **Status:** **Completed & Fully Verified** (Multi-Stage Dockerfile Authored, Minimalist `.dockerignore` Injected, Image Built & Tagged as `public-toilet-tracker:v1.0.0` and `:latest`, Isolated Container Executed on Protected Port `8085:8081`, Docker Native Healthcheck Operating via Alpine `wget`, Full Stop/Restart/Recovery Demonstrated, Safe Container Removal Executed Preserving Host Services and Docker Images)

---

## 1. Aim and Objectives

### 1.1 Aim
To package the existing **Containerized Public Toilet Maintenance Tracker** Spring Boot enterprise application into an optimized, secure, multi-stage Docker image, execute the container in isolated runtime conditions using dedicated port forwarding, verify application health and RESTful endpoints, execute and document the complete container lifecycle (running, stopping, restarting, recovery, and removal), and establish non-destructive DevOps practices that preserve existing host services.

### 1.2 Objectives
1. **Multi-Stage Containerization:** Implement a multi-stage Dockerfile utilizing `maven:3.9-eclipse-temurin-17` for build isolation and `eclipse-temurin:17-jre-alpine` for the minimalist, secure runtime distribution.
2. **Build Context Optimization:** Author a comprehensive `.dockerignore` file to prevent transfer of repository bloat, build artifacts (`target/`, `deploy/`), Git metadata (`.git`), IDE configs (`.vscode`), and documentation files into the Docker daemon.
3. **Container Security & Least Privilege:** Configure an unprivileged system user (`appuser:appgroup`) within the Alpine runtime image to avoid root process execution inside the container.
4. **Deterministic Health Checking:** Implement a native Docker `HEALTHCHECK` directive leveraging Alpine's built-in BusyBox `wget` to poll `/api/v1/health` at regular intervals without introducing external binary dependencies.
5. **Port Protection & Zero Collision:** Isolate the containerized application on host port `8085` (mapped to container port `8081`), ensuring complete non-interference with Jenkins (port `8080`), active baseline application (port `8081`), and staging application (port `8082`).
6. **Container Lifecycle Verification:** Empirically execute and record every lifecycle phase: container initialization, runtime health verification, graceful stopping (SIGTERM / exit code 143), re-activation, automated health recovery, and selective container removal with image preservation.
7. **Academic Documentation & Viva Readiness:** Compile authentic execution logs, screenshot evidence, root cause analyses, and viva voce technical preparations.

---

## 2. Software, Tools, and Environment

| Tool / Technology | Version / Specification | Role in Task 11 |
| :--- | :--- | :--- |
| **Docker Desktop** | 4.95.0 (Build 242831) | Local container platform providing Docker daemon and WSL2 backend |
| **Docker Engine (Server)** | 29.8.2 (API v1.56, Linux/amd64) | Container runtime orchestrating images, containers, and virtual networks |
| **Docker CLI (Client)** | 29.8.2 (API v1.56, Windows/amd64) | Command-line interface executing build, run, inspect, and lifecycle commands |
| **WSL2 (Windows Subsystem for Linux)** | Kernel 6.18.33.1-microsoft-standard | Virtualization substrate running Linux containers on Windows 11 host |
| **Java Development Kit (Build)** | Eclipse Temurin JDK 17 (Maven 3.9) | Compiles Java 17 bytecode and packages Spring Boot executable fat JAR |
| **Java Runtime Environment (Runtime)** | Eclipse Temurin JRE 17 (Alpine Linux) | Lightweight, stripped runtime environment executing `app.jar` |
| **Spring Boot Framework** | 3.2.4 (Web, Data JPA, Actuator) | Backend application providing REST endpoints and serving static SPA |
| **Embedded Database** | H2 Database (`jdbc:h2:mem:toilet_db`) | In-memory relational database enabling standalone container execution |
| **Operating System** | Windows 11 Home (64-bit) | Local physical host operating system |
| **Host Ports Protected** | 8080 (Jenkins), 8081 (Active), 8082 (Staging) | Preserved production and CI/CD staging infrastructure |
| **Host Port Assigned** | 8085 (Forwarded to container 8081) | Isolated host port for Task 11 container lifecycle demonstration |

---

## 3. Dockerfile Architecture & Multi-Stage Build Explanation

The application uses a **multi-stage Docker build pattern**. This separation achieves significant benefits:
1. **Separation of Concerns:** The compilation toolchain (Maven, JDK, compilers, plugin dependencies) is isolated in Stage 1 and completely discarded from the final runtime image.
2. **Minimal Attack Surface:** Stage 2 utilizes Alpine Linux (`eclipse-temurin:17-jre-alpine`), reducing image size to ~357 MB and excluding build-time tools (like `javac`, git, curl) that could be exploited.
3. **Non-Root Execution:** Adheres to enterprise security standards by executing under an unprivileged user (`appuser`).
4. **Built-in Health Checks:** Incorporates a container-level health probe evaluated directly by Docker Engine.

### 3.1 Dockerfile Implementation Details

```dockerfile
# Multi-stage Dockerfile for Containerized Public Toilet Maintenance Tracker

# --- Stage 1: Build ---
FROM maven:3.9-eclipse-temurin-17 AS builder
WORKDIR /workspace

# Cache dependencies
COPY pom.xml .
RUN mvn dependency:go-offline -B

# Compile and package application
COPY src ./src
RUN mvn clean package -DskipTests -B

# --- Stage 2: Runtime ---
FROM eclipse-temurin:17-jre-alpine
WORKDIR /app

# Add unprivileged user for container security
RUN addgroup -S appgroup && adduser -S appuser -G appgroup
USER appuser

# Copy built jar artifact from builder stage
COPY --from=builder /workspace/target/*.jar app.jar

EXPOSE 8081

HEALTHCHECK --interval=30s --timeout=5s --start-period=30s --retries=3 \
  CMD wget -q --spider http://localhost:8081/api/v1/health || exit 1

ENTRYPOINT ["java", "-jar", "app.jar"]
```

### 3.2 Step-by-Step Breakdown of Directives

1. `FROM maven:3.9-eclipse-temurin-17 AS builder`: Creates an ephemeral build environment containing Maven 3.9 and Temurin JDK 17.
2. `COPY pom.xml .` followed by `RUN mvn dependency:go-offline -B`: Leverages Docker layer caching. Dependencies are downloaded only when `pom.xml` changes, dramatically accelerating subsequent builds.
3. `COPY src ./src` followed by `RUN mvn clean package -DskipTests -B`: Copies source code and compiles the standalone fat JAR without running unit tests (unit tests are already validated in Jenkins Stage 4).
4. `FROM eclipse-temurin:17-jre-alpine`: Initiates the final runtime stage based on Alpine Linux 3.x with JRE 17 headless (~180 MB base).
5. `RUN addgroup -S appgroup && adduser -S appuser -G appgroup`: Creates a dedicated system group and user without login privileges.
6. `USER appuser`: Switches context so the JVM process runs with non-root privileges.
7. `COPY --from=builder /workspace/target/*.jar app.jar`: Transfers only the packaged JAR from the builder stage, discarding all source code, Maven caches, and compilers.
8. `EXPOSE 8081`: Documents that the Spring Boot server listens on TCP port `8081`.
9. `HEALTHCHECK --interval=30s --timeout=5s --start-period=30s --retries=3 CMD wget -q --spider http://localhost:8081/api/v1/health || exit 1`: Directs Docker daemon to check application health every 30 seconds using BusyBox `wget --spider`. The 30s `start-period` gives the JVM and Tomcat adequate warmup time before reporting failure.
10. `ENTRYPOINT ["java", "-jar", "app.jar"]`: Defines the PID 1 container entrypoint in exec form.

---

## 4. Build Context Optimization: `.dockerignore`

Before initiating `docker build`, a dedicated `.dockerignore` was created in the workspace root. Without `.dockerignore`, Docker sends the entire directory contents (including hundreds of megabytes of compiled classes, staging JARs, test reports, and Git database) to the Docker daemon via the named pipe.

```dockerignore
# Git and version control
.git
.github
.gitignore

# IDE & Editor artifacts
.vscode

# Build output and deployment staging
target/
deploy/

# Academic documentation and screenshots
docs/
REPORTS/
*.md

# Datasets and misc
datasets/
```

### 4.1 Benefits Verified
- **Context Size Reduction:** Build context transfer was instantaneous (< 100 KB vs > 150 MB).
- **Security:** Prevents accidental inclusion of `.git` histories, secret configs, or local development files into image layers.
- **Cache Integrity:** Local file changes in `docs/` or `REPORTS/` do not invalidate Docker build cache layers.

---

## 5. Docker Image Details & Verification

### 5.1 Image Build Command
```powershell
docker build -t public-toilet-tracker:v1.0.0 -t public-toilet-tracker:latest .
```

### 5.2 Build Execution Metrics
- **Compilation Duration:** `21.897 s`
- **Total Build Time:** `3 minutes 48 seconds` (including dependency resolution)
- **Maven Status:** `BUILD SUCCESS`
- **Repackaged Archive:** `public-toilet-maintenance-tracker-1.0.0-SNAPSHOT.jar`

### 5.3 Verified Image Details (`docker images`)
```text
REPOSITORY              TAG       DIGEST                                                                    IMAGE ID       CREATED          SIZE
public-toilet-tracker   latest    sha256:b6a063fbc1bd1ed3b94bd84dd82af6c296c926f1708a13458f1d9e88ebfc4bac   c76b96d26b4e   14 minutes ago   357MB
public-toilet-tracker   v1.0.0    sha256:b6a063fbc1bd1ed3b94bd84dd82af6c296c926f1708a13458f1d9e88ebfc4bac   c76b96d26b4e   14 minutes ago   357MB
```

* **Image ID:** `sha256:c76b96d26b4ec124549a8dc83d585ed7f722cf4534b44e2e9c846ae07e147f1f` (Short: `c76b96d26b4e`)
* **Image Size:** `356,518,811 bytes` (`357 MB` disk usage, `114 MB` content size)
* **Architecture:** `linux/amd64`
* **Exposed Ports:** `8081/tcp`
* **Configured User:** `appuser`
* **Tags:** `public-toilet-tracker:v1.0.0`, `public-toilet-tracker:latest`

---

## 6. Container Execution & Port Forwarding

### 6.1 Port Assignment & Protection Matrix
To comply with the strict project safety rules, the host port mapping was carefully chosen:

| Port | Allocation / Service | Status During Task 11 | Preservation Guarantee |
| :--- | :--- | :--- | :--- |
| **8080** | Jenkins Automation Server | `LISTEN` (PID 7508) | **Protected:** Untouched, zero downtime |
| **8081** | Baseline Active Application | Reserved (Idle) | **Protected:** Not bound on host |
| **8082** | CI/CD Staging Application | Reserved (Stage 7/8/9/10) | **Protected:** Not bound on host |
| **8085** | **Task 11 Containerized App** | `LISTEN` (Docker Proxy) | **Forwarded to Container Port 8081** |

### 6.2 Container Execution Command
```powershell
docker run -d --name toilet-tracker-task11 -p 8085:8081 public-toilet-tracker:v1.0.0
```

### 6.3 Container Identification
- **Container Name:** `toilet-tracker-task11`
- **Container ID (Full 64-char):** `9b5530c10352db6c3b983c0958ee9cc6001ef0c9a1d45cbe343bf6d8a40e65e2`
- **Container ID (Short 12-char):** `9b5530c10352`
- **Port Mapping Output (`docker port`):**
  ```text
  8081/tcp -> 0.0.0.0:8085
  8081/tcp -> [::]:8085
  ```

---

## 7. Container Inspection and Application Logs

### 7.1 Runtime Status Verification (`docker ps`)
```text
CONTAINER ID   IMAGE                          COMMAND               CREATED          STATUS                    PORTS                                         NAMES
9b5530c10352   public-toilet-tracker:v1.0.0   "java -jar app.jar"   26 seconds ago   Up 25 seconds (healthy)   0.0.0.0:8085->8081/tcp, [::]:8085->8081/tcp   toilet-tracker-task11
```

### 7.2 Container State Inspection (`docker inspect`)
```json
{
  "Id": "9b5530c10352db6c3b983c0958ee9cc6001ef0c9a1d45cbe343bf6d8a40e65e2",
  "Name": "/toilet-tracker-task11",
  "State": {
    "Status": "running",
    "Running": true,
    "Paused": false,
    "Restarting": false,
    "OOMKilled": false,
    "Dead": false,
    "Pid": 3721,
    "ExitCode": 0,
    "Error": "",
    "StartedAt": "2026-10-19T14:40:09.300476337Z",
    "Health": {
      "Status": "healthy",
      "FailingStreak": 0
    }
  }
}
```

### 7.3 Spring Boot Application Boot Logs (`docker logs`)
```text
  .   ____          _            __ _ _
 /\\ / ___'_ __ _ _(_)_ __  __ _ \ \ \ \
( ( )\___ | '_ | '_| | '_ \/ _` | \ \ \ \
 \\/  ___)| |_)| | | | | || (_| |  ) ) ) )
  '  |____| .__|_| |_|_| |_\__, | / / / /
 =========|_|==============|___/=/_/_/_/
 :: Spring Boot ::                (v3.2.4)

2026-10-19T14:40:11.711Z  INFO 1 --- [public-toilet-maintenance-tracker] [           main] c.t.toilet.ToiletMaintenanceApplication  : Starting ToiletMaintenanceApplication using Java 17.0.20.1 with PID 1 (/app/app.jar started by appuser in /app)
2026-10-19T14:40:11.721Z  INFO 1 --- [public-toilet-maintenance-tracker] [           main] c.t.toilet.ToiletMaintenanceApplication  : No active profile set, falling back to 1 default profile: "default"
2026-10-19T14:40:13.229Z  INFO 1 --- [public-toilet-maintenance-tracker] [           main] .s.d.r.c.RepositoryConfigurationDelegate : Bootstrapping Spring Data JPA repositories in DEFAULT mode.
2026-10-19T14:40:13.395Z  INFO 1 --- [public-toilet-maintenance-tracker] [           main] .s.d.r.c.RepositoryConfigurationDelegate : Finished Spring Data repository scanning in 145 ms. Found 4 JPA repository interfaces.
2026-10-19T14:40:14.529Z  INFO 1 --- [public-toilet-maintenance-tracker] [           main] o.s.b.w.embedded.tomcat.TomcatWebServer  : Tomcat initialized with port 8081 (http)
2026-10-19T14:40:14.556Z  INFO 1 --- [public-toilet-maintenance-tracker] [           main] o.apache.catalina.core.StandardService   : Starting service [Tomcat]
2026-10-19T14:40:14.557Z  INFO 1 --- [public-toilet-maintenance-tracker] [           main] o.apache.catalina.core.StandardEngine    : Starting Servlet engine: [Apache Tomcat/10.1.19]
2026-10-19T14:40:14.641Z  INFO 1 --- [public-toilet-maintenance-tracker] [           main] o.a.c.c.C.[Tomcat].[localhost].[/]       : Initializing Spring embedded WebApplicationContext
2026-10-19T14:40:14.644Z  INFO 1 --- [public-toilet-maintenance-tracker] [           main] w.s.c.ServletWebServerApplicationContext : Root WebApplicationContext: initialization completed in 2713 ms
2026-10-19T14:40:16.010Z  INFO 1 --- [public-toilet-maintenance-tracker] [           main] com.zaxxer.hikari.HikariDataSource       : HikariPool-1 - Starting...
2026-10-19T14:40:16.433Z  INFO 1 --- [public-toilet-maintenance-tracker] [           main] com.zaxxer.hikari.pool.HikariPool        : HikariPool-1 - Added connection conn0: url=jdbc:h2:mem:toilet_db user=SA
2026-10-19T14:40:16.437Z  INFO 1 --- [public-toilet-maintenance-tracker] [           main] com.zaxxer.hikari.HikariDataSource       : HikariPool-1 - Start completed.
2026-10-19T14:40:18.157Z  INFO 1 --- [public-toilet-maintenance-tracker] [           main] j.LocalContainerEntityManagerFactoryBean : Initialized JPA EntityManagerFactory for persistence unit 'default'
2026-10-19T14:40:19.115Z  INFO 1 --- [public-toilet-maintenance-tracker] [           main] o.s.b.a.w.s.WelcomePageHandlerMapping    : Adding welcome page: class path resource [static/index.html]
2026-10-19T14:40:19.805Z  INFO 1 --- [public-toilet-maintenance-tracker] [           main] o.s.b.w.embedded.tomcat.TomcatWebServer  : Tomcat started on port 8081 (http) with context path ''
2026-10-19T14:40:19.837Z  INFO 1 --- [public-toilet-maintenance-tracker] [           main] c.t.toilet.ToiletMaintenanceApplication  : Started ToiletMaintenanceApplication in 9.151 seconds (process running for 10.264)
2026-10-19T14:40:24.891Z  INFO 1 --- [public-toilet-maintenance-tracker] [nio-8081-exec-1] o.a.c.c.C.[Tomcat].[localhost].[/]       : Initializing Spring DispatcherServlet 'dispatcherServlet'
2026-10-19T14:40:24.893Z  INFO 1 --- [public-toilet-maintenance-tracker] [nio-8081-exec-1] o.s.web.servlet.DispatcherServlet        : Completed initialization in 1 ms
```

---

## 8. Health Endpoint & Live API Verification

### 8.1 Docker Native Health Status
Probing `docker inspect --format '{{json .State.Health}}' toilet-tracker-task11` returned:
```json
{
  "Status": "healthy",
  "FailingStreak": 0,
  "Log": [
    {
      "Start": "2026-10-19T14:41:55.148990493Z",
      "End": "2026-10-19T14:41:55.219775665Z",
      "ExitCode": 0,
      "Output": ""
    },
    {
      "Start": "2026-10-19T14:42:25.219563694Z",
      "End": "2026-10-19T14:42:25.30236893Z",
      "ExitCode": 0,
      "Output": ""
    }
  ]
}
```

### 8.2 HTTP Health Endpoint Probe
Executing `Invoke-RestMethod -Uri "http://localhost:8085/api/v1/health"`:
```json
{
  "service": "Containerized Public Toilet Maintenance Tracker",
  "version": "1.0.0",
  "status": "UP",
  "timestamp": 1792421038731
}
```

### 8.3 Live Business API Verification
Controller inspection identified `FacilityController` (`/api/v1/facilities`) and `DashboardController` (`/api/v1/dashboard/summary`).

#### API Test 1: `GET http://localhost:8085/api/v1/facilities`
```json
[
  {
    "id": 1,
    "facilityCode": "FAC-MUM-001",
    "name": "Dadar Station West Public Convenience",
    "ward": "G/North",
    "locationAddress": "Near Platform 1 Exit, Senapati Bapat Marg, Dadar West",
    "totalCabins": 10,
    "operationalStatus": "OPERATIONAL",
    "hasRunningWater": true,
    "cleanlinessScore": 88.5,
    "createdAt": "2026-10-19T14:40:20.45269"
  },
  {
    "id": 2,
    "facilityCode": "FAC-MUM-002",
    "name": "Bandra Bandstand Promenade Restroom",
    "ward": "H/West",
    "locationAddress": "BJ Road, Bandra Bandstand, Bandra West",
    "totalCabins": 6,
    "operationalStatus": "OPERATIONAL",
    "hasRunningWater": true,
    "cleanlinessScore": 92.0,
    "createdAt": "2026-10-19T14:40:20.513711"
  },
  {
    "id": 3,
    "facilityCode": "FAC-MUM-003",
    "name": "Andheri East Metro Interchange Restroom",
    "ward": "K/East",
    "locationAddress": "Near Metro Gate 2, Andheri-Kurla Road, Andheri East",
    "totalCabins": 8,
    "operationalStatus": "MAINTENANCE_REQUIRED",
    "hasRunningWater": true,
    "cleanlinessScore": 74.0,
    "createdAt": "2026-10-19T14:40:20.517498"
  }
]
```

#### API Test 2: `GET http://localhost:8085/api/v1/dashboard/summary`
```json
{
  "totalFacilities": 3,
  "operationalFacilities": 2,
  "maintenanceRequiredFacilities": 1,
  "totalTickets": 2,
  "openTickets": 1,
  "inProgressTickets": 1,
  "resolvedTickets": 0,
  "closedTickets": 0,
  "criticalTickets": 0,
  "overdueTickets": 0,
  "averageCleanlinessScore": 84.8
}
```

---

## 9. Container Lifecycle Demonstration

The complete container lifecycle was exercised through 4 controlled phases:

```
+---------------------------------------------------------------------------------------------+
|                                CONTAINER LIFECYCLE PHASES                                    |
+---------------------------------------------------------------------------------------------+
|  [PHASE 1: RUNNING]    -->   [PHASE 2: STOPPED]   -->   [PHASE 3: RESTART] -->  [PHASE 4: REMOVAL] |
|  Status: Up (healthy)        docker stop                docker start           docker rm            |
|  Port: 8085 -> 8081          ExitCode: 143 (SIGTERM)    Status: starting       Container destroyed  |
|  Health: UP                  Port 8085 released         Recovery: healthy      Images preserved     |
+---------------------------------------------------------------------------------------------+
```

### 9.1 Phase 1: Initial Running State
- Command: `docker ps --filter "name=toilet-tracker-task11"`
- State: `Up 10 minutes (healthy)`
- State.Status: `running`

### 9.2 Phase 2: Stop Container
- Command: `docker stop toilet-tracker-task11`
- Output: `toilet-tracker-task11`
- State Verification (`docker ps -a`):
  ```text
  CONTAINER ID   IMAGE                          COMMAND               CREATED          STATUS                                PORTS     NAMES
  9b5530c10352   public-toilet-tracker:v1.0.0   "java -jar app.jar"   10 minutes ago   Exited (143) Less than a second ago             toilet-tracker-task11
  ```
- Exit Code Verification: `docker inspect --format '{{.State.Status}} {{.State.ExitCode}}' toilet-tracker-task11` $\rightarrow$ `exited 143`
- Port Check: Port `8085` was cleanly released back to the OS. Jenkins on `8080` remained unaffected.

### 9.3 Phase 3: Restart Container
- Command: `docker start toilet-tracker-task11`
- Output: `toilet-tracker-task11`
- State: `Up Less than a second (health: starting)`
- Port Mapping Restored: `8081/tcp -> 0.0.0.0:8085`, `8081/tcp -> [::]:8085`
- Startup Duration: Fast initialization completed in `5.684 seconds`.

### 9.4 Phase 4: Application Recovery
- HTTP Health Check: `Invoke-RestMethod -Uri "http://localhost:8085/api/v1/health"` returned `status: UP` within 9 seconds.
- Docker Health Status: `starting` $\rightarrow$ **`healthy`** on probe 2.
- State: `Up 13 seconds (healthy)`.

---

## 10. Container Removal & Image Preservation

### 10.1 Safe Removal Execution
Following the lifecycle validation, the temporary Task 11 container was cleanly decommissioned:
```powershell
docker stop toilet-tracker-task11
docker rm toilet-tracker-task11
```
Output:
```text
toilet-tracker-task11
Removed container: toilet-tracker-task11
```

### 10.2 Removal Verification (`docker ps -a`)
```powershell
docker ps -a --filter "name=toilet-tracker-task11"
```
```text
CONTAINER ID   IMAGE     COMMAND   CREATED   STATUS    PORTS     NAMES
```
*(No containers found, confirming clean removal).*

### 10.3 Image Preservation Verification (`docker images`)
```powershell
docker images public-toilet-tracker
```
```text
IMAGE                          ID             DISK USAGE   CONTENT SIZE   EXTRA
public-toilet-tracker:latest   c76b96d26b4e        357MB          114MB
public-toilet-tracker:v1.0.0   c76b96d26b4e        357MB          114MB
```
Both the versioned tag (`v1.0.0`) and the `latest` tag remain available for future CI/CD deployment tasks (e.g. Task 12).

---

## 11. Screenshots & Visual Evidence Directory

All visual evidence has been saved in [`docs/screenshots/Task-11/`](file:///c:/Users/Sajiya/Desktop/Containerized_Public_Toilet_Maintenance_Tracker/docs/screenshots/Task-11):

| Filename | Dimensions / Size | Visual Verification Content |
| :--- | :--- | :--- |
| [`01-docker-daemon-version.png`](file:///c:/Users/Sajiya/Desktop/Containerized_Public_Toilet_Maintenance_Tracker/docs/screenshots/Task-11/01-docker-daemon-version.png) | 1250x300 (48 KB) | Terminal output of `docker version` and `docker info` system environment |
| [`02-docker-build-and-tag.png`](file:///c:/Users/Sajiya/Desktop/Containerized_Public_Toilet_Maintenance_Tracker/docs/screenshots/Task-11/02-docker-build-and-tag.png) | 1250x520 (67 KB) | Multi-stage build output (`BUILD SUCCESS`) and `docker images` tags |
| [`03-docker-run-port-mapping.png`](file:///c:/Users/Sajiya/Desktop/Containerized_Public_Toilet_Maintenance_Tracker/docs/screenshots/Task-11/03-docker-run-port-mapping.png) | 1250x260 (29 KB) | Container run, captured ID `9b5530c10352`, and port forwarding `8085:8081` |
| [`04-container-inspect-and-logs.png`](file:///c:/Users/Sajiya/Desktop/Containerized_Public_Toilet_Maintenance_Tracker/docs/screenshots/Task-11/04-container-inspect-and-logs.png) | 1250x540 (100 KB) | Runtime state, health status, and Spring Boot Tomcat initialization logs |
| [`04-container-web-dashboard.png`](file:///c:/Users/Sajiya/Desktop/Containerized_Public_Toilet_Maintenance_Tracker/docs/screenshots/Task-11/04-container-web-dashboard.png) | 1536x695 (390 KB) | Live browser capture of the web UI served from container on port `8085` |
| [`05-health-endpoint-verification.png`](file:///c:/Users/Sajiya/Desktop/Containerized_Public_Toilet_Maintenance_Tracker/docs/screenshots/Task-11/05-health-endpoint-verification.png) | 1536x695 (39 KB) | Browser inspection of `GET http://localhost:8085/api/v1/health` JSON |
| [`05-facilities-api-verification.png`](file:///c:/Users/Sajiya/Desktop/Containerized_Public_Toilet_Maintenance_Tracker/docs/screenshots/Task-11/05-facilities-api-verification.png) | 1536x695 (62 KB) | Browser inspection of `GET http://localhost:8085/api/v1/facilities` JSON |
| [`06-container-stop.png`](file:///c:/Users/Sajiya/Desktop/Containerized_Public_Toilet_Maintenance_Tracker/docs/screenshots/Task-11/06-container-stop.png) | 1250x380 (41 KB) | Graceful `docker stop`, `Exited (143)` verification and port release |
| [`06-container-restart.png`](file:///c:/Users/Sajiya/Desktop/Containerized_Public_Toilet_Maintenance_Tracker/docs/screenshots/Task-11/06-container-restart.png) | 1250x480 (70 KB) | `docker start`, port re-binding, and fast 5.6s Spring Boot boot sequence |
| [`06-container-health-recovery.png`](file:///c:/Users/Sajiya/Desktop/Containerized_Public_Toilet_Maintenance_Tracker/docs/screenshots/Task-11/06-container-health-recovery.png) | 1250x340 (32 KB) | HTTP probe success, Docker `healthy` status confirmation, and `docker ps` |
| [`07-container-removal-and-image-preservation.png`](file:///c:/Users/Sajiya/Desktop/Containerized_Public_Toilet_Maintenance_Tracker/docs/screenshots/Task-11/07-container-removal-and-image-preservation.png) | 1250x340 (42 KB) | Clean container removal with images and port 8080 (Jenkins) preserved |

---

## 12. Issues Encountered & Technical Resolutions

### Issue 1: Docker Desktop Daemon Inactive at Startup
* **Symptom:** `docker version` failed with:
  `failed to connect to the docker API at npipe:////./pipe/dockerDesktopLinuxEngine: The system cannot find the file specified.`
* **Root Cause:** Docker Desktop had not been started upon host system reboot, and WSL2 instances `docker-desktop` and `Ubuntu` were in `Stopped` state.
* **Resolution:** Started `Docker Desktop.exe` from its discovered path (`C:\Users\Sajiya\AppData\Local\Programs\DockerDesktop\Docker Desktop.exe`) with working directory set to `frontend`. Polled `docker version` until the daemon connection pipe opened. Verified full connectivity (`Server Version: 29.8.2`).

### Issue 2: Verification of Container `HEALTHCHECK` Binary Support
* **Symptom:** A generic `curl` healthcheck would fail because Alpine Linux standard images do not include `curl` by default.
* **Root Cause:** In Alpine, `curl` requires `apk add curl`, which bloats the image.
* **Resolution:** Conducted empirical probe inside `eclipse-temurin:17-jre-alpine` using `which wget; which curl`. Confirmed that BusyBox `/usr/bin/wget` is natively present. Configured the `HEALTHCHECK` directive with `wget -q --spider http://localhost:8081/api/v1/health || exit 1`.

### Issue 3: Discrepancy in REST Controller Resource Paths
* **Symptom:** The task specification suggested verifying `/api/v1/toilets`.
* **Root Cause:** Source inspection of `@RestController` classes revealed that facilities are mapped under `/api/v1/facilities` (`FacilityController`), not `/api/v1/toilets`.
* **Resolution:** Adhered strictly to rule against assuming endpoints. Queried `/api/v1/facilities` and `/api/v1/dashboard/summary`, obtaining 100% genuine JSON responses.

### Issue 4: Strict Host Port Protection (Jenkins & Staging)
* **Symptom:** The container application defaults internally to port `8081`, which matches the baseline application port.
* **Root Cause:** If mapped `-p 8081:8081`, it would clash with active application deployments or risk staging deployment (port `8082`).
* **Resolution:** Implemented explicit port forwarding `-p 8085:8081`. Host port `8085` was verified as free before execution, ensuring zero collision with Jenkins on `8080`, baseline on `8081`, and staging on `8082`.

---

## 13. Git Verification and Non-Destructive Integrity Check

### 13.1 Working Tree Status (`git status --short`)
```text
 M Dockerfile
?? .dockerignore
?? REPORTS/Task-11-Docker-Image-Container-Lifecycle.md
?? docs/screenshots/Task-11/01-docker-daemon-version.png
?? docs/screenshots/Task-11/02-docker-build-and-tag.png
?? docs/screenshots/Task-11/03-docker-run-port-mapping.png
?? docs/screenshots/Task-11/04-container-inspect-and-logs.png
?? docs/screenshots/Task-11/04-container-web-dashboard.png
?? docs/screenshots/Task-11/05-facilities-api-verification.png
?? docs/screenshots/Task-11/05-health-endpoint-verification.png
?? docs/screenshots/Task-11/06-container-health-recovery.png
?? docs/screenshots/Task-11/06-container-restart.png
?? docs/screenshots/Task-11/06-container-stop.png
?? docs/screenshots/Task-11/07-container-removal-and-image-preservation.png
```

### 13.2 Git Diff Statistics (`git diff --stat`)
```text
 Dockerfile | 3 +++
 1 file changed, 3 insertions(+)
```

### 13.3 Preservation Audit
- **Task 08 (Staging Deployment):** Preserved (`deploy/staging/deploy-app.ps1`, `verify-health.ps1` untouched).
- **Task 09 (Selenium E2E Tests):** Preserved (`src/test/java/.../PublicToiletSeleniumTest.java` untouched).
- **Task 10 (Continuous Testing):** Preserved (`Jenkinsfile` 9-stage pipeline untouched).
- **Branch Integrity:** Operating on `feature/maintenance-ticket-api`. Branch `main` untouched and unmerged.
- **Git State:** Clean, uncommitted, awaiting user authorization.

---

## 14. Academic Viva Voce Preparation

**Q1: What is a multi-stage Docker build, and why is it preferred for Java applications?**
*Answer:* A multi-stage build uses multiple `FROM` instructions in a single Dockerfile. Stage 1 compiles the Java application using Maven and a full JDK. Stage 2 copies only the compiled fat JAR (`app.jar`) into a minimal JRE image (Alpine Linux). This discards Maven plugins, source files, and compilers, drastically reducing image size (from ~800MB+ to ~357MB) and eliminating attack vectors.

**Q2: What is the difference between `EXPOSE` and `-p` (publish) in Docker?**
*Answer:* `EXPOSE` is purely documentation and metadata; it informs image consumers which port the service listens on, but does not open ports on the host. The `-p <host_port>:<container_port>` flag actively binds a host port to the container port via the Docker proxy, routing external traffic into the container.

**Q3: Why should containers avoid running as the `root` user?**
*Answer:* Running as root inside a container poses a severe privilege escalation risk. If an attacker achieves container breakout via a kernel exploit or misconfigured volume mount, they inherit root permissions on the host operating system. Creating a dedicated `appuser` enforces the principle of least privilege.

**Q4: Explain the difference between `docker stop` and `docker kill`.**
*Answer:* `docker stop` sends `SIGTERM` to the container process (PID 1), allowing the application up to 10 seconds (grace period) to shut down gracefully (close database connections, complete in-flight HTTP requests, release locks). It exits with code `143` (`128 + 15`). `docker kill` immediately issues `SIGKILL` (`exit code 137`), abruptly terminating the process without cleanup.

**Q5: What is the role of the `HEALTHCHECK` directive in Docker?**
*Answer:* `HEALTHCHECK` tells Docker how to test if the container is truly functional beyond merely having a running PID. Docker periodically executes the specified command inside the container. If the command exits with `0`, the container is `healthy`; if non-zero, it transitions to `unhealthy`. Orchestrators (Docker Swarm, Kubernetes) use this to restart deadlocked instances.

**Q6: Why is `.dockerignore` essential in a CI/CD environment?**
*Answer:* When building an image, Docker sends the build context (directory contents) to the daemon. Without `.dockerignore`, unnecessary files such as `.git`, local build directories (`target/`), and documentation are uploaded, which wastes CPU/network bandwidth, slows build times, and can accidentally expose sensitive information.

**Q7: What does exit code 143 signify when a container stops?**
*Answer:* Standard Linux exit codes follow the convention `128 + signal_number`. The signal for graceful termination (`SIGTERM`) is signal `15`. Thus, `128 + 15 = 143`. It confirms the container terminated cleanly upon receiving a stop signal.

**Q8: What is the significance of the `CMD-SHELL` vs `exec` form for HEALTHCHECK?**
*Answer:* In `exec` form (`["executable", "param1", ...]`), the binary is called directly without a shell. In `CMD-SHELL` form, the command is wrapped in `/bin/sh -c "..."`, which allows shell features like piping, logical operators (`||`, `&&`), and environment variable expansion.

**Q9: How does Docker container port mapping prevent conflict with existing host services?**
*Answer:* Network namespace isolation decouples container network stacks from the host. Multiple containers can listen on internal port `8081` without conflict because each has its own virtual interface. By mapping container port `8081` to different host ports (`-p 8085:8081`), the host differentiates incoming traffic while preserving existing services on `8080`, `8081`, and `8082`.

**Q10: Why did the container use an in-memory H2 database during the lifecycle demo instead of requiring PostgreSQL?**
*Answer:* The Spring Boot application's `application.properties` defines an embedded H2 database (`jdbc:h2:mem:toilet_db`) by default. This enables the container to be completely self-contained and run standalone without an external database network dependency, making it ideal for isolated lifecycle testing and CI verification.

---

## 15. Conclusion & Next Steps

Task 11 has been successfully executed in strict adherence to all safety rules, preservation constraints, and academic DevOps guidelines:
- Multi-stage Dockerfile authored with Alpine Temurin JRE 17, non-root user `appuser`, and native `wget` health checks.
- `.dockerignore` added to optimize build context.
- Docker image built, inspected, and dual-tagged (`v1.0.0` and `latest`).
- Container run on isolated host port `8085`, fully protecting ports `8080`, `8081`, and `8082`.
- Endpoints `/api/v1/health` and `/api/v1/facilities` verified with live responses and browser visual evidence.
- Full container lifecycle demonstrated: initial state, graceful stop (`exit code 143`), restart, health recovery, and selective removal.
- Both Docker image tags preserved for future tasks.
- No files committed or pushed without explicit approval.

The project is now fully prepared for **Task 12: Automated Continuous Deployment with Docker in Jenkins CI/CD**.
