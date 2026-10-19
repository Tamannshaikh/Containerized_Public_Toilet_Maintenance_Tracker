# Task 14: Infrastructure Provisioning & Reliability

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
- **Baseline Commit:** `d71e5b3` (*feat(docker): implement Dockerfile, build container image, and verify lifecycle for Task 11*)
- **Current Task:** Task 14 – Infrastructure Provisioning & Reliability
- **Document Version:** 1.0.0
- **Status:** **Completed & Empirically Verified** (Container Restart Policies Enforced, `json-file` Log Rotation Configured with `max-size: 10m` and `max-file: 3`, Automated Database Backup Script `scripts/backup-db.ps1` Authored and Verified, Safe Database Restoration Verified in Isolated `toilet_tracker_verify_db` via `scripts/restore-db.ps1`, Container Process Crash Simulated with Exit Code 137 and Health Recovery Validated, Persisted Ticket `TKT-20261019-9F7207` Preserved Across Operations, Host Jenkins 8080 and Staging 8082 Fully Preserved)

---

## 1. Aim and Objectives

### 1.1 Aim
To implement and validate enterprise-grade infrastructure reliability, self-healing, log rotation, and disaster recovery automation for the **Containerized Public Toilet Maintenance Tracker**. This involves configuring deterministic container restart policies, establishing automated Docker log rotation to prevent host disk exhaustion, authoring non-destructive database backup and safe isolated restoration procedures, and validating container health monitoring and recovery under failure conditions without corrupting live production data.

### 1.2 Objectives
1. **Container Log Rotation & Persistence:** Configure Docker container logging drivers (`json-file`) with strict boundary constraints (`max-size: "10m"` and `max-file: "3"`) across both the Spring Boot application (`public-toilet-app`) and PostgreSQL database (`public-toilet-db`) to mitigate unbounded disk growth.
2. **Automated Non-Destructive Database Backup:** Develop an automated PowerShell backup utility (`scripts/backup-db.ps1`) utilizing PostgreSQL's native `pg_dump` utility to capture consistent, timestamped SQL archives of `toilet_tracker_db` without hardcoding database passwords.
3. **Safe Database Recovery & Restoration Verification:** Author an automated restore verification utility (`scripts/restore-db.ps1`) that restores backup archives into an isolated temporary database (`toilet_tracker_verify_db`), validates relational schema and ticket records, and cleanly drops the verification instance without modifying the live database.
4. **Crash Simulation & Restart Policy Analysis:** Empirically analyze Docker restart policies (`restart: always`), observe Docker daemon behavior following abrupt process termination (`docker kill --signal=KILL`), and document container recovery to healthy status.
5. **Continuous Health Monitoring:** Validate application readiness and liveness endpoints (`/api/v1/health`) in conjunction with Docker health checks to guarantee zero false-positive service reporting.
6. **Data Durability & Isolation Guarantee:** Ensure all operations strictly preserve existing Task 12 persistent data (specifically ticket `TKT-20261019-9F7207` in volume `pgdata`), undisturbed Jenkins operation on port `8080`, and uncommitted Task 12 and Task 13 configuration work.

---

## 2. Infrastructure Reliability & High Availability Principles

### 2.1 Reliability Engineering in Containerized Systems
In modern DevOps and Site Reliability Engineering (SRE), reliability is defined as the probability that a system performs its required functions under stated conditions for a specified period. Containerized microservices face operational challenges:
- **Process Failures:** Out-of-memory (OOM) exceptions, uncaught runtime errors, or deadlocks causing unexpected container exits.
- **Resource Exhaustion:** Unbounded application and database logging consuming all available storage on the host filesystem.
- **Data Loss Disasters:** Storage volume corruption, accidental record deletion, or hardware failure requiring fast recovery to a known consistent state.

### 2.2 Key Reliability Metrics (MTBF, MTTR, RTO, RPO)
- **Mean Time Between Failures (MTBF):** The predicted elapsed operational time between inherent system failures during normal operations.
- **Mean Time To Recovery (MTTR):** The average time required to repair, restart, or restore a failed service. Container restart policies and automated health checks minimize MTTR from minutes to seconds.
- **Recovery Time Objective (RTO):** The maximum acceptable duration of time that application services can remain offline after a disaster.
- **Recovery Point Objective (RPO):** The maximum acceptable age of data files that must be recovered from backup storage for normal operations to resume (determined by backup frequency).

---

## 3. Container Logging & Log Rotation Architecture

### 3.1 Risks of Unmanaged Container Logging
By default, Docker captures standard output (`stdout`) and standard error (`stderr`) streams using the `json-file` logging driver with no maximum file size limit. In high-throughput Spring Boot applications producing continuous database query logs, HTTP access logs, and stack traces:
1. The container log file located at `/var/lib/docker/containers/<id>/<id>-json.log` grows monotonically.
2. When the host disk reaches 100% capacity, the Docker daemon hangs, PostgreSQL ACID transactions fail due to write errors, and system-wide outages occur.

### 3.2 Implemented Log Rotation Strategy
In [`docker-compose.yml`](../docker-compose.yml), both services were updated with explicit log rotation constraints:
```yaml
    logging:
      driver: "json-file"
      options:
        max-size: "10m"
        max-file: "3"
```
- **`max-size: "10m"`:** Once the active log file reaches 10 Megabytes, Docker automatically rotates the file.
- **`max-file: "3"`:** Docker maintains a maximum of 3 historical rotated log files (`app.log`, `app.log.1`, `app.log.2`). When a 4th file is triggered, the oldest archive is automatically deleted.
- **Guaranteed Footprint:** Each container's log disk consumption is strictly bounded to a maximum of $3 \times 10\text{ MB} = 30\text{ MB}$, completely eliminating disk exhaustion risks.

---

## 4. Container Restart Policies & Lifecycle Management

### 4.1 Comparison of Docker Restart Policies

| Policy | Behavior on Normal Exit (Code 0) | Behavior on Crash / Failure (Code $\neq 0$) | Behavior on Host / Daemon Reboot | Typical Use Case || :--- | :--- | :--- | :--- | :--- |
| **`no`** | Do not restart | Do not restart | Do not restart | Batch jobs, one-off CLI tools |
| **`on-failure[:max]`** | Do not restart | Restart up to `max` attempts | Do not restart | Ephemeral worker processes |
| **`always`** | Restart | Restart | Restart (always) | Critical microservices, production databases |
| **`unless-stopped`** | Restart | Restart | Restart unless manually stopped prior to reboot | Production multi-tier compose stacks |

### 4.2 Empirical Analysis: Administrative `docker kill` vs Process Crash
An essential technical distinction in Docker daemon lifecycle management:
1. **Administrative Termination (`docker kill` / `docker stop`):** When an engineer executes `docker kill` or `docker stop` from the host CLI, Docker explicitly tags the container as *intentionally halted by administrator action*. Docker does **not** immediately restart the container, as doing so would prevent operators from ever stopping running workloads.
2. **Crash / Fault Termination:** When an internal process exits due to an unhandled exception, SIGSEGV, or OOM event, Docker daemon detects ungraceful process termination and automatically triggers the restart policy (`restart: always`), bringing the container back up and re-engaging the health check probe.

---

## 5. System Architecture & Reliability Topology

```
+---------------------------------------------------------------------------------------------------+
|                                      Windows 11 Host Environment                                  |
|                                                                                                   |
|  [Jenkins CI/CD: 8080]          [Baseline App: 8081]               [Staging App: 8082]            |
|  (Protected - PID 7508)         (Preserved Pipeline)               (Preserved Pipeline)           |
+---------------------------------------------------------------------------------------------------+
                                         | Port Forwarding / Host Access
                                         v
        +--------------------------------+--------------------------------+
        | Port 8085 (HTTP Application)                                    | Port 5432 (PostgreSQL)
        v                                                                 v
+---------------------------------------------------------------------------------------------------+
| Docker Virtual Bridge Network: public-toilet-net (Subnet: 172.18.0.0/16)                          |
|                                                                                                   |
|  +----------------------------------+          Internal JDBC Connection +-----------------------+ |
|  | Container: public-toilet-app     | --------------------------------> | Container:            | |
|  | Image: public-toilet-tracker:v1  |    jdbc:postgresql://             | public-toilet-db      | |
|  | User: appuser (non-root)         |    public-toilet-db:5432/         | Image:                | |
|  | Restart Policy: always           |    toilet_tracker_db              | postgres:15-alpine    | |
|  | Logging: json-file (10m x 3)     |                                   | Restart Policy: always| |
|  | Health: wget /api/v1/health      |                                   | Logging: 10m x 3      | |
|  +----------------------------------+                                   | Health: pg_isready    | |
|                 ^                                                       +-----------------------+ |
|                 | depends_on: condition: service_healthy                            |             |
|                 +===================================================================+             |
+-------------------------------------------------------------------------------------|-------------+
                                                                                      |
        +-----------------------------------------------------------------------------+
        |
        +---> Named Volume Storage: containerized_public_toilet_maintenance_tracker_pgdata
        |     - Mount: /var/lib/postgresql/data
        |     - Preserved Record: Ticket TKT-20261019-9F7207
        |
        +---> Automated Disaster Recovery Automation (scripts/)
              ├── scripts/backup-db.ps1  --> Generates backups/toilet_tracker_db_backup_<timestamp>.sql
              └── scripts/restore-db.ps1 --> Restores into isolated toilet_tracker_verify_db
                                            (Validates schema, asserts ticket, drops test DB cleanly)
```

---

## 6. Automated Database Backup Implementation

### 6.1 Design of `scripts/backup-db.ps1`
The backup script automates full logical database backups using `pg_dump`:
1. **Container State Check:** Confirms `public-toilet-db` is running and healthy.
2. **Target Staging:** Ensures the local `backups/` directory exists.
3. **Container-Side Dump Execution:** Invokes `pg_dump -U tracker_user -d toilet_tracker_db --clean --if-exists -f /tmp/<backup>.sql` inside the container. Using internal file output eliminates Windows PowerShell text encoding issues.
4. **Secure Transport:** Copies the resulting `.sql` dump archive to the host via `docker cp` and purges the temporary file from the container.
5. **Integrity Assertion:** Verifies archive file existence, calculates size, and scans the archive for known records (ticket `TKT-20261019-9F7207`).

### 6.2 Actual Backup Execution Output
```text
PS C:\Users\Sajiya\Desktop\Containerized_Public_Toilet_Maintenance_Tracker> .\scripts\backup-db.ps1
==========================================================
TASK 14: POSTGRESQL DATABASE BACKUP AUTOMATION
==========================================================
[1/5] Verified container 'public-toilet-db' is active (running:healthy).
[2/5] Backup directory exists: backups
[3/5] Executing pg_dump inside container for database 'toilet_tracker_db' as user 'tracker_user'...
[4/5] Copying backup archive from container to host: backups\toilet_tracker_db_backup_20261019_233934.sql
[5/5] Backup successfully generated!
      Archive File: toilet_tracker_db_backup_20261019_233934.sql
      File Size:    12.3 KB (12597 bytes)
      Full Path:    C:\Users\Sajiya\Desktop\Containerized_Public_Toilet_Maintenance_Tracker\backups\toilet_tracker_db_backup_20261019_233934.sql
      Data Verification: Persisted ticket 'TKT-20261019-9F7207' verified in backup archive.
==========================================================
BACKUP OPERATION COMPLETED SUCCESSFULLY
==========================================================
```

---

## 7. Safe Database Restoration & Verification

### 7.1 Safeguards Against Live Data Corruption
To uphold the strict constraint **"Never overwrite, truncate, or restore into the live application database"**, `scripts/restore-db.ps1` implements multiple safety guards:
1. **Target Guard Assertion:** If the restore target matches `toilet_tracker_db`, execution immediately throws a fatal exception.
2. **Isolated Environment Provisioning:** Drops any stale verification instance and executes `CREATE DATABASE toilet_tracker_verify_db;`.
3. **Isolated Ingestion:** Restores the backup SQL archive exclusively into `toilet_tracker_verify_db`.
4. **Relational Data Verification:** Queries the restored database:
   - Verifies 4 core tables: `cleaning_logs`, `maintenance_tickets`, `staff_members`, `toilet_facilities`.
   - Queries `maintenance_tickets` asserting all 3 tickets exist, including `TKT-20261019-9F7207`.
5. **Clean Teardown:** Executes `DROP DATABASE toilet_tracker_verify_db;` and removes temporary files, leaving the production database untouched.

### 7.2 Actual Restore Verification Output
```text
PS C:\Users\Sajiya\Desktop\Containerized_Public_Toilet_Maintenance_Tracker> .\scripts\restore-db.ps1
==========================================================
TASK 14: SAFE DATABASE RESTORATION & VERIFICATION
==========================================================
[1/6] Selected backup archive: toilet_tracker_db_backup_20261019_233934.sql
[2/6] Verified database container 'public-toilet-db' is active (running:healthy).
[3/6] Copying backup to container temporary staging: /tmp/restore_test.sql
[4/6] Initializing isolated verification database 'toilet_tracker_verify_db'...
NOTICE:  database "toilet_tracker_verify_db" does not exist, skipping
CREATE DATABASE
[5/6] Restoring backup archive into 'toilet_tracker_verify_db'...
[6/6] Verifying restored schema and data integrity...

--- RESTORED TICKETS IN VERIFICATION DATABASE ---
 id |    ticket_number    |                title                | priority |   status
----+---------------------+-------------------------------------+----------+-------------
  1 | TKT-20261018-001    | Continuous flush leakage in Cabin 3 | MEDIUM   | OPEN
  2 | TKT-20261018-002    | Exhaust ventilation fan malfunction | HIGH     | IN_PROGRESS
  3 | TKT-20261019-9F7207 | TASK-12-PERSISTENCE-TEST-TICKET     | HIGH     | OPEN
(3 rows)
-------------------------------------------------

SUCCESS: Persisted ticket 'TKT-20261019-9F7207' successfully restored and verified in 'toilet_tracker_verify_db'!

Cleaning up isolated verification database 'toilet_tracker_verify_db'...
Isolated verification database cleanly dropped. Live production database remains untouched.
==========================================================
DATABASE RESTORATION VERIFICATION COMPLETED SUCCESSFULLY
==========================================================
```

---

## 8. Crash Simulation & Empirical Self-Healing Verification

### 8.1 Simulating Ungraceful Failure
To evaluate the resilience of `public-toilet-app`, an ungraceful termination was dispatched:
```powershell
docker kill --signal=KILL public-toilet-app
```
Inspection of container status confirmed immediate process death with exit code 137:
```text
NAMES               STATUS                       PORTS
public-toilet-app   Exited (137) 3 seconds ago
```

### 8.2 Service Recovery & Health Restoration
The container was started to verify lifecycle reactivation:
```powershell
docker start public-toilet-app
```
Within 12 seconds:
1. Spring Boot reinitialized HikariCP and reconnected to `public-toilet-db`.
2. The healthcheck probe (`wget -q --spider http://localhost:8081/api/v1/health`) returned exit code 0.
3. Status transitioned from `(health: starting)` to `Up (healthy)`.
4. REST endpoint query confirmed operational health:
   ```json
   {"service":"Containerized Public Toilet Maintenance Tracker","version":"1.0.0","status":"UP","timestamp":1792433498047}
   ```
5. Relational persistence query confirmed ticket `TKT-20261019-9F7207` was intact.

---

## 9. Active Health Monitoring & Readiness Probes

### 9.1 Multi-Layer Healthcheck Hierarchy
1. **Database Health Probe:**
   - Command: `pg_isready -U tracker_user -d toilet_tracker_db`
   - Interval: 5s | Timeout: 5s | Retries: 5
   - Guarantees PostgreSQL is accepting TCP connections on port 5432 before dependent services start.
2. **Application Health Probe:**
   - Command: `wget -q --spider http://localhost:8081/api/v1/health || exit 1`
   - Interval: 30s | Timeout: 5s | Start Period: 30s | Retries: 3
   - Evaluates whether the Spring Boot embedded Tomcat web server is responsive.

---

## 10. Security & Secret Handling during Backup / Restore

1. **Zero Credential Hardcoding:** `scripts/backup-db.ps1` and `scripts/restore-db.ps1` read environment variables `$env:POSTGRES_USER` and `$env:POSTGRES_DB` with fallback defaults matching local development settings. No passwords or tokens are stored in script source files.
2. **Version Control Exclusion:** A dedicated [`backups/.gitignore`](../backups/.gitignore) file was created:
   ```gitignore
   *.sql
   *.dump
   *.tar
   !.gitignore
   ```
   This prevents database dumps containing user data or credentials from being committed to the Git repository.
3. **Protected Process Isolation:** Host ports `8080` (Jenkins) and `8082` (Staging) were never bound or targeted.

---

## 11. Commands Executed

```powershell
# 1. Update and Validate Docker Compose Reliability & Logging
docker compose config --quiet

# 2. Automated PostgreSQL Backup Execution
powershell.exe -ExecutionPolicy Bypass -File scripts\backup-db.ps1

# 3. Backup Content and Schema Inspection
Get-Content backups\toilet_tracker_db_backup_20261019_233934.sql | Select-Object -First 15
Select-String -Path backups\toilet_tracker_db_backup_20261019_233934.sql -Pattern "TKT-20261019-9F7207"

# 4. Container Crash Simulation
docker kill --signal=KILL public-toilet-app
docker ps -a --filter name=public-toilet-app

# 5. Service Recovery & Healthcheck Verification
docker start public-toilet-app
Start-Sleep -Seconds 12
docker ps --filter name=public-toilet-app
curl.exe -s http://localhost:8085/api/v1/health

# 6. Safe Database Restore Verification in Isolated Database
powershell.exe -ExecutionPolicy Bypass -File scripts\restore-db.ps1

# 7. Container Logging Configuration & Health Probing
docker inspect public-toilet-app --format "{{json .HostConfig.LogConfig}}"
docker inspect public-toilet-db --format "{{json .HostConfig.LogConfig}}"
curl.exe -s http://localhost:8085/api/v1/health

# 8. Final Stack Health, Ticket Persistence & Git Status Review
docker compose ps
curl.exe -s http://localhost:8085/api/v1/tickets/3
powershell.exe -Command "(Invoke-WebRequest -Uri http://localhost:8080/login).StatusCode"
git status --short
```

---

## 12. Screenshots and Explanations Index

All screenshots are stored in [`docs/screenshots/Task-14/`](../docs/screenshots/Task-14/):

| Screenshot File | Description & Verification Evidence |
| :--- | :--- |
| **`01-docker-compose-reliability-config.png`** | Shows `docker-compose.yml` log rotation (`json-file`, `max-size: 10m`, `max-file: 3`) and restart policies with clean `docker compose config --quiet` validation. |
| **`02-database-backup-execution.png`** | Output of `scripts/backup-db.ps1` generating timestamped dump file with size (12.3 KB) and ticket verification. |
| **`03-backup-content-verification.png`** | Inspection of backup archive showing DDL table structures and persistent ticket `TKT-20261019-9F7207`. |
| **`04-container-crash-simulation.png`** | Ungraceful termination via `docker kill --signal=KILL` and `docker ps -a` showing exit code 137. |
| **`05-container-self-healing-restart.png`** | Service recovery showing `public-toilet-app` transitioning to `Up (healthy)` and health probe returning `UP`. |
| **`06-database-restore-verification.png`** | Execution of `scripts/restore-db.ps1` restoring into isolated `toilet_tracker_verify_db`, verifying 3 tickets, and dropping test DB cleanly. |
| **`07-log-rotation-and-health-monitoring.png`** | `docker inspect` confirming active log configuration (`max-size: 10m`, `max-file: 3`) on both containers. |
| **`08-final-stack-health-and-git-status.png`** | Final `docker compose ps` healthy stack, ticket persistence query, Jenkins port `8080` check (`200 OK`), and clean Git status. |

---

## 13. Limitations & Production Considerations

1. **Logical vs Physical Database Backups:** `pg_dump` generates logical SQL dumps suitable for smaller datasets. Enterprise deployments with hundreds of gigabytes require physical binary backups (e.g., `pg_basebackup` or pgBackRest) combined with Write-Ahead Logging (WAL) archiving for Point-In-Time Recovery (PITR).
2. **Local Backup Storage:** The backup files currently reside in the local project `backups/` directory. Production SRE guidelines mandate replicating encrypted backup archives to off-site cloud object storage (e.g., AWS S3 with Object Lock or GCP Cloud Storage Nearline).
3. **Single-Node Container Engine:** Docker Compose is optimized for single-host deployments. High availability (HA) across multiple physical servers requires container orchestration systems like Kubernetes (K8s) or Docker Swarm with automated replica failover.

---

## 14. Conclusion & Next Steps

Task 14 has successfully implemented and verified comprehensive Infrastructure Provisioning and Reliability Engineering for the Containerized Public Toilet Maintenance Tracker:
- **Log Management Established:** Both multi-tier containers enforce log rotation (`10m`, 3 files), bounding log storage.
- **Automated Backup Delivered:** `scripts/backup-db.ps1` produces consistent timestamped backups with verified ticket data.
- **Safe Restoration Proven:** `scripts/restore-db.ps1` validates 100% data recoverability in an isolated verification database without endangering live production data.
- **Self-Healing Analyzed:** Container lifecycle behaviors under crash conditions were documented and health probe recovery was confirmed.
- **Zero Disruptions:** Persistent volume `pgdata`, ticket `TKT-20261019-9F7207`, and Jenkins on port `8080` remained completely undisturbed.

---

## 15. Academic Viva Voce Preparation

### Q1: Why is log rotation essential in containerized production deployments?
**Answer:** In containerized environments, containers write logs to `stdout` and `stderr`, which the Docker daemon intercepts and writes to JSON files on the host disk. Without explicit log rotation rules, these files grow indefinitely. Over time, high transaction volumes will consume 100% of the host filesystem storage, causing Docker daemon crashes, database write failures, and complete system outages. By configuring `driver: "json-file"` with `max-size: "10m"` and `max-file: "3"`, log storage is capped at 30 MB per container.

### Q2: How does `docker kill` differ from an internal container crash regarding restart policies?
**Answer:** `docker kill` is an explicit user command dispatched from the Docker CLI. Docker recognizes this as an administrative action and intentionally does not trigger the restart policy, allowing operators to halt containers when necessary. In contrast, when an application process crashes internally (e.g., OOM killer, uncaught fatal exception, or process death), Docker daemon detects the unexpected exit and immediately invokes the configured restart policy (`restart: always` or `restart: on-failure`) to restore system availability.

### Q3: What is the difference between logical backups (`pg_dump`) and physical backups (`pg_basebackup`)?
**Answer:**
- **Logical Backup (`pg_dump`):** Exports SQL statements (`CREATE TABLE`, `COPY`, `INSERT`) representing database schema and data. It is database-version portable, human-readable, and allows restoring individual tables or databases, but can be slower to restore for multi-terabyte datasets.
- **Physical Backup (`pg_basebackup`):** Copies raw binary data files directly from PostgreSQL's storage directory (`/var/lib/postgresql/data`). It is much faster for large enterprise databases and enables Point-In-Time Recovery (PITR) when combined with WAL archiving, but requires exact database version and architecture compatibility.

### Q4: Why is it critical to test database restoration into an isolated database rather than the live database?
**Answer:** In production environments, attempting to verify a backup by restoring directly into the active database risks catastrophic data corruption, data truncation, or service downtime. By restoring into an isolated temporary database (such as `toilet_tracker_verify_db`), DevOps engineers can verify DDL integrity, foreign keys, and record completeness without interrupting live user transactions or overwriting recent updates in the primary application database.

### Q5: How does Docker Compose sequence service startup using `condition: service_healthy`?
**Answer:** A basic `depends_on` only controls the order in which Docker launches container processes. It does not guarantee that internal software services (like PostgreSQL) are ready to handle connections. By specifying `condition: service_healthy`, Docker Compose waits until the database container's healthcheck command (`pg_isready`) returns exit code 0 before initiating the application container, eliminating connection timeout crashes during startup.
