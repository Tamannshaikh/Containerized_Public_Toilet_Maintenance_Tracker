# Task 13: Configuration Management & Environment Drift

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
- **Current Task:** Task 13 – Configuration Management & Environment Drift
- **Document Version:** 1.0.0
- **Status:** **Completed & Empirically Verified** (Ansible-based Configuration Management & Audit Implemented, Profile Segregation Verified between H2 and PostgreSQL, Idempotence Empirically Proven with 0 Changes across Multiple Runs, Configuration Drift Detected and Isolated against Test Fixtures, Sensitive Secrets Excluded from Git Tracking, PostgreSQL Persistent Volume Preserved, Jenkins Undisturbed on Port 8080)

---

## 1. Aim and Objectives

### 1.1 Aim
To establish robust configuration management and automated environment drift detection for the **Containerized Public Toilet Maintenance Tracker** using Ansible. The implementation standardizes configuration across environments, segregates development and containerized runtime profiles, enforces security practices for environment secrets, and provides non-destructive, idempotent auditing of infrastructure state and configuration consistency.

### 1.2 Objectives
1. **Ansible Environment Provisioning:** Set up Ansible Core (`2.20.1`) on Python `3.14.4` within the WSL 2 (Ubuntu) substrate to automate configuration validation on the local Windows/WSL hybrid environment.
2. **Environment Variable Standardization & Secret Isolation:** Standardize all configurable parameters (`APP_PORT`, `DB_PORT`, `POSTGRES_DB`, `POSTGRES_USER`, `POSTGRES_PASSWORD`, `SPRING_PROFILES_ACTIVE`) through `.env.example` templates and a local `.env` file, ensuring sensitive credentials remain strictly ignored by Git version control.
3. **Multi-Environment Spring Boot Profile Segregation:** Segregate application profiles so that the local development environment defaults cleanly to an in-memory H2 database (`application-dev.properties`) while containerized Docker Compose runs automatically bind to the PostgreSQL 15 relational datastore (`application-docker.properties`).
4. **Idempotent Audit Playbook Authoring:** Author a comprehensive, non-destructive Ansible playbook (`ansible/playbook.yml`) utilizing core modules (`stat`, `assert`, `command`, `wait_for`, `uri`, `debug`) to audit file structures, secret leakage, profile separation, Docker daemon health, container states, and REST endpoints.
5. **Empirical Idempotency Verification:** Execute the Ansible playbook across consecutive runs to prove that repeat audits result in identical outcomes with zero state mutations (`changed=0 failed=0`).
6. **Automated Configuration Drift Detection:** Implement a reproducible test harness demonstrating that any deviation in configuration parameters (e.g., modified port allocations or invalid active profiles) is immediately identified and flagged as drift, while keeping the production stack unharmed.
7. **Infrastructure Preservation:** Guarantee zero disruption to existing workloads: preserve the active Docker Compose stack (`public-toilet-app` on port `8085`, `public-toilet-db` on port `5432`), Jenkins CI/CD on port `8080`, baseline on `8081`, staging on `8082`, and the persistent test ticket `TKT-20261019-9F7207` within Docker volume `pgdata`.

---

## 2. Configuration Management Concepts

### 2.1 What is Configuration Management?
Configuration Management (CM) is the discipline of maintaining systems, servers, applications, and infrastructure in a known, consistent, and desired state over time. Rather than manually executing shell commands or editing configuration files ad-hoc on individual nodes, CM tools treat configuration as code (Infrastructure as Code / Configuration as Code). This enables teams to:
- Define declarative desired states in human-readable code (e.g., YAML playbooks).
- Track configuration changes through version control systems (Git).
- Ensure consistency across development, testing, staging, and production environments.
- Automate compliance auditing, rollback, and continuous system provisioning.

### 2.2 Declarative vs. Imperative Approaches
- **Imperative Approach:** Specifies *how* to achieve a state through step-by-step sequential commands (e.g., bash scripts). Imperative scripts frequently fail or cause unwanted side-effects when run more than once if they lack explicit guardrails.
- **Declarative Approach (Ansible):** Specifies *what* the system state should be (e.g., "file `/etc/app.conf` must exist with permissions 0644"). The automation engine inspects the current state, compares it with the target definition, and only executes changes if an actual divergence exists.

### 2.3 The Principle of Idempotence
Idempotence is the defining property of robust configuration management: executing an operation once produces the desired outcome, and executing it repeatedly yields the exact same outcome without causing further changes or side effects.

In mathematical notation:
$$f(f(x)) = f(x)$$

For a read-only configuration audit playbook, idempotency ensures that repeated evaluations perform rigorous validation without mutating any live services, creating phantom records, or restarting active containers.

---

## 3. Configuration Drift and Why It Matters

### 3.1 Defining Configuration Drift
Configuration Drift refers to the gradual, uncoordinated divergence of an environment’s actual configuration from its intended baseline. Drift occurs when:
- Manual interventions ("hotfixes" or quick troubleshooting tweaks) are applied directly to a server or container without updating the source repository.
- Environment variables or port allocations are changed locally without synchronizing `.env` templates.
- Package versions, library dependencies, or configuration properties diverge between developer workstations and continuous delivery environments.
- Uncoordinated updates are pushed to container images or deployment descriptors.

### 3.2 Risks of Unmanaged Drift
1. **"Works on My Machine" Syndrome:** Discrepancies between local development defaults (e.g., H2 in-memory databases) and staging/production databases (e.g., PostgreSQL) cause late-stage integration failures.
2. **Deployment Failures:** CD pipelines fail unexpectedly when hardcoded host ports or missing environment parameters conflict with existing server configurations.
3. **Security Vulnerabilities:** Untracked configuration drift may expose default credentials, disable authentication modules, or accidentally commit sensitive `.env` files into public source repositories.
4. **Untraceable Incidents:** In post-incident reviews, engineers cannot determine whether an outage was caused by a software defect or an undocumented configuration change.

### 3.3 How Ansible Detects and Mitigates Drift
Ansible acts as an automated drift auditor. By comparing live runtime properties (container health, open listening ports, active Spring profiles, and file properties) against predefined declarative assertions, Ansible flags differences as failures before erroneous deployments impact end users.

---

## 4. Project Architecture and Configuration Files

### 4.1 System Architecture Topology

```
+---------------------------------------------------------------------------------------------------+
|                                      Windows 11 Host Environment                                  |
|                                                                                                   |
|  [Jenkins CI/CD: 8080]          [Baseline App: 8081]               [Staging App: 8082]            |
|  (Protected - PID 7508)         (Preserved Pipeline)               (Preserved Pipeline)           |
+---------------------------------------------------------------------------------------------------+
                                         | Port Forwarding / WSL Integration
                                         v
+---------------------------------------------------------------------------------------------------+
| WSL 2 (Ubuntu Substrate) - Automation Controller                                                  |
|                                                                                                   |
|   +-------------------------------------------------------------------------------------------+   |
|   | Ansible Core 2.20.1 (Python 3.14.4)                                                       |   |
|   | Inventory: inventory.ini (localhost via local connection)                                  |   |
|   | Playbook: playbook.yml (Idempotent Configuration Audit & Drift Detector)                  |   |
|   | Config: ansible.cfg (stdout_callback = default)                                           |   |
|   +-------------------------------------------------------------------------------------------+   |
|                                         |                                                         |
|                                         | Inspects & Validates                                    |
|                                         v                                                         |
|   Docker Desktop Bridge:                                                                          |
|     - http://127.0.0.1:8085 (Application REST API / Health Endpoint)                              |
|     - 127.0.0.1:5432 (PostgreSQL Relational Port)                                                 |
|     - docker inspect / docker compose config                                                      |
+---------------------------------------------------------------------------------------------------+
                                         |
                                         v
+---------------------------------------------------------------------------------------------------+
| Docker Virtual Bridge Network: containerized_public_toilet_maintenance_tracker_public-toilet-net  |
| Subnet: 172.18.0.0/16 | Gateway: 172.18.0.1                                                        |
|                                                                                                   |
|  +----------------------------------+          Internal JDBC Connection +-----------------------+ |
|  | Container: public-toilet-app     | --------------------------------> | Container:            | |
|  | Image: public-toilet-tracker:v1  |    jdbc:postgresql://             | public-toilet-db      | |
|  | IP: 172.18.0.3                   |    public-toilet-db:5432/         | Image:                | |
|  | User: appuser (non-root)         |    toilet_tracker_db              | postgres:15-alpine    | |
|  | Profile: docker (PostgreSQL)     |                                   | IP: 172.18.0.2        | |
|  | Port: 8081 (mapped to host 8085) |                                   | Health: pg_isready    | |
|  +----------------------------------+                                   | Port: 5432            | |
|                 ^                                                       +-----------------------+ |
|                 | depends_on: condition: service_healthy                            |             |
|                 +===================================================================+             |
+-------------------------------------------------------------------------------------|-------------+
                                                                                      |
                                                                                      v
                                                        +-----------------------------------------+
                                                        | Docker Named Volume:                    |
                                                        | ..._pgdata                              |
                                                        | Mount: /var/lib/postgresql/data         |
                                                        | Preserved Ticket: TKT-20261019-9F7207   |
                                                        +-----------------------------------------+
```

### 4.2 Configuration File Hierarchy

The project implements a multi-tiered configuration hierarchy that strictly segregates local development from containerized deployments:

| Configuration File | Path | Scope & Role || :--- | :--- | :--- |
| **`.env.example`** | Project Root | Version-controlled template defining all configurable variables with dummy/default placeholders. Zero credentials. |
| **`.env`** | Project Root | Local active environment parameters. Ignored by Git. Injects ports and database parameters. |
| **`docker-compose.yml`** | Project Root | Multi-tier orchestration file using parameter expansion `${VAR:-default}` for resilient defaults. |
| **`application.properties`** | `src/main/resources/` | Core Spring Boot configuration. Specifies `spring.profiles.active=dev` for seamless zero-config developer onboarding. |
| **`application-dev.properties`** | `src/main/resources/` | Local development profile. Binds to in-memory H2 database (`jdbc:h2:mem:toilet_db`) with `H2Dialect`. |
| **`application-docker.properties`**| `src/main/resources/` | Container deployment profile. Binds to PostgreSQL 15 (`jdbc:postgresql://public-toilet-db:5432/toilet_tracker_db`). |

---

## 5. Ansible Installation and Environment

### 5.1 Environment Prerequisites
To avoid polluting the Windows host Python environment while retaining high execution performance, Ansible was installed within the existing **WSL 2 Ubuntu Linux** distribution. WSL 2 provides full Linux system call compatibility, allowing Ansible to run native POSIX modules while interacting directly with Docker Desktop and the host filesystem mounted under `/mnt/c/`.

### 5.2 Package Management & Installation Verification
Ansible Core was provisioned via Ubuntu's Advanced Package Tool (`apt`):

```bash
sudo apt-get update
sudo apt-get install -y ansible-core
```

### 5.3 Verified Runtime Specifications
```
root@UBUNTU-WSL:~# ansible --version
ansible [core 2.20.1]
  config file = /mnt/c/Users/Sajiya/Desktop/Containerized_Public_Toilet_Maintenance_Tracker/ansible/ansible.cfg
  configured module search path = ['/root/.ansible/plugins/modules', '/usr/share/ansible/plugins/modules']
  ansible python module location = /usr/lib/python3/dist-packages/ansible
  ansible collection location = /root/.ansible/collections:/usr/share/ansible/collections
  executable location = /usr/bin/ansible
  python version = 3.14.4 (main, Apr  8 2026, 04:02:31) [GCC 15.2.0] (/usr/bin/python3)
  jinja version = 3.1.6
  pyyaml version = 6.0.3 (with libyaml v0.2.5)
```

---

## 6. Inventory and Playbook Explanation

### 6.1 Directory Layout
```
ansible/
├── ansible.cfg       # Global execution settings, inventory pointer, output callback
├── inventory.ini     # Host target definitions targeting localhost via local connection
├── playbook.yml      # Idempotent configuration audit and drift detection playbook
└── README.md         # Comprehensive usage documentation
```

### 6.2 Ansible Configuration (`ansible/ansible.cfg`)
```ini
[defaults]
inventory = inventory.ini
host_key_checking = False
retry_files_enabled = False
stdout_callback = default
interpreter_python = auto_silent
nocows = 1
```
*Note on Windows Subsystem for Linux (WSL) Filesystem Permissions:* When running inside WSL against an NTFS Windows drive (`/mnt/c/`), directories appear with permission mode `0777`. Ansible intentionally emits a security warning when an `ansible.cfg` file is loaded from a world-writable directory unless the configuration path is explicitly exported via `ANSIBLE_CONFIG`. This was cleanly configured in `~/.bashrc` to ensure unattended, reliable playbook runs.

### 6.3 Local Inventory (`ansible/inventory.ini`)
```ini
[local]
localhost ansible_connection=local ansible_python_interpreter=/usr/bin/python3
```
By specifying `ansible_connection=local`, Ansible executes modules directly on the local machine via the Python interpreter without requiring SSH daemon overhead or network latency.

### 6.4 Playbook Architecture (`ansible/playbook.yml`)
The audit playbook executes 9 structured stages containing 30 tasks:
1. **Stage 1 (Filesystem Integrity):** Iterates over core project configuration files (`pom.xml`, `Dockerfile`, `docker-compose.yml`, `.env.example`, `application*.properties`) using `ansible.builtin.stat` and verifies their physical presence with `ansible.builtin.assert`.
2. **Stage 2 (Secret Isolation):** Checks for the local existence of `.env` without printing or capturing its secret content. Uses `git check-ignore -q .env` to guarantee that `.env` cannot be accidentally tracked in source control.
3. **Stage 3 (Profile Segregation):** Audits property files to confirm `application.properties` delegates to `dev`, `application-dev.properties` configures H2, and `application-docker.properties` configures PostgreSQL.
4. **Stage 4 (Docker Engine & Compose):** Interrogates the Docker daemon via `docker info` to verify engine responsiveness and validates Compose syntax via `docker compose config --quiet`.
5. **Stage 5 (Non-Destructive Port Audit):** Probes port `8085` (Application) and `5432` (PostgreSQL) using `ansible.builtin.wait_for`. Uses Windows PowerShell interoperability from WSL to verify that Jenkins on port `8080` remains completely undisturbed.
6. **Stage 6 (Container Health State):** Queries Docker container states using `docker inspect`, asserting that both `public-toilet-app` and `public-toilet-db` report status `running:healthy`.
7. **Stage 7 (Live Endpoint & Data Persistence Audit):** Performs an HTTP GET request to `http://127.0.0.1:8085/api/v1/health` via `ansible.builtin.uri`, confirming status `UP`. Queries `/api/v1/tickets/3` to prove that Task 12 ticket `TKT-20261019-9F7207` persisted in the PostgreSQL database volume.
8. **Stage 8 (Configuration Drift Detection):** Inspects the target environment file (defaulting to `.env`, or overridden via `-e "audit_env_file=..."`), parses key parameters (`APP_PORT`, `DB_PORT`, `POSTGRES_DB`, `SPRING_PROFILES_ACTIVE`), and validates them against deployment baselines.
9. **Stage 9 (Audit Summary):** Emits an audit recap verifying all baselines, profiles, and health checks.

---

## 7. Expected versus Actual Configuration Checks

| Configuration Parameter | Expected Value | Actual Value | Verification Mechanism | Status || :--- | :--- | :--- | :--- | :--- |
| **`.env` Git Ignore Status** | Strictly Ignored (Exit 0) | Ignored (`.gitignore:54:.env`) | `git check-ignore -v .env` | **PASS** |
| **Default Active Profile** | `dev` | `spring.profiles.active=dev` | `grep` on `application.properties` | **PASS** |
| **Dev Database Dialect** | H2 In-Memory | `jdbc:h2:mem:toilet_db` | `grep` on `application-dev.properties` | **PASS** |
| **Docker Database Dialect** | PostgreSQL 15 | `jdbc:postgresql://public-toilet-db:5432/...` | `grep` on `application-docker.properties`| **PASS** |
| **Application Host Port** | `8085` | `8085` listening | `ansible.builtin.wait_for: port 8085` | **PASS** |
| **Database Host Port** | `5432` | `5432` listening | `ansible.builtin.wait_for: port 5432` | **PASS** |
| **Jenkins Host Port** | `8080` (Preserved) | HTTP 200 OK | `Invoke-WebRequest http://localhost:8080`| **PASS** |
| **`public-toilet-app` Health**| `running:healthy` | `running:healthy` | `docker inspect` health check | **PASS** |
| **`public-toilet-db` Health** | `running:healthy` | `running:healthy` | `docker inspect` health check | **PASS** |
| **REST Health Endpoint** | `status: "UP"` | `{"status":"UP", ...}` | `ansible.builtin.uri` GET `/api/v1/health`| **PASS** |
| **Data Persistence Record** | `TKT-20261019-9F7207` | `ticketNumber: "TKT-20261019-9F7207"`| `ansible.builtin.uri` GET `/tickets/3` | **PASS** |
| **Docker Compose Syntax** | Valid (Exit code 0) | Valid (Exit code 0) | `docker compose config --quiet` | **PASS** |

---

## 8. Idempotence Verification with Actual Results

### 8.1 First Playbook Run (Baseline Audit)
Command executed:
```bash
ansible-playbook playbook.yml
```
Execution Recap:
```
PLAY RECAP *********************************************************************
localhost                  : ok=30   changed=0    unreachable=0    failed=0    skipped=0    rescued=0    ignored=0
```
- **Total Tasks Evaluated:** 30
- **Successful Checks:** 30
- **Mutated Systems:** 0 (`changed=0`)
- **Failures:** 0 (`failed=0`)

### 8.2 Second Playbook Run (Idempotency Proof)
Command executed immediately following Run 1 without changing environment state:
```bash
ansible-playbook playbook.yml
```
Execution Recap:
```
PLAY RECAP *********************************************************************
localhost                  : ok=30   changed=0    unreachable=0    failed=0    skipped=0    rescued=0    ignored=0
```

### 8.3 Idempotency Analysis
Because every task in `playbook.yml` is authored with read-only inspection modules (`stat`, `assert`, `wait_for`, `uri`) and commands with explicit `changed_when: false` directives, running the playbook multiple times produces identical, repeatable results. It confirms that the system complies fully with the declarative baseline without altering existing database rows, container states, or network configurations.

---

## 9. Drift Detection Test with Actual Results

### 9.1 Non-Destructive Drift Simulation
To rigorously test Ansible's configuration drift detection without risking the live containerized environment, an isolated temporary test fixture `/tmp/test_drift.env` was provisioned with intentional configuration drift:
- `APP_PORT=9999` (expected: `8085`)
- `SPRING_PROFILES_ACTIVE=invalid_profile` (expected: `docker`)

```bash
cat << 'EOF' > /tmp/test_drift.env
APP_PORT=9999
DB_PORT=5432
POSTGRES_DB=toilet_tracker_db
SPRING_PROFILES_ACTIVE=invalid_profile
EOF
```

### 9.2 Execution against Drift Fixture
The playbook was executed targeting the temporary fixture via parameter override:
```bash
ansible-playbook playbook.yml -e "audit_env_file=/tmp/test_drift.env"
```

### 9.3 Actual Diagnostic Error Output
Ansible evaluated the configuration parameters and immediately halted execution at Task 8.4, reporting the exact nature of the drift:

```
TASK [8.4 Parse and audit configuration variables for drift] *******************
[ERROR]: Task failed: Action failed: CONFIGURATION DRIFT DETECTED in /tmp/test_drift.env! Expected [APP_PORT=8085, DB_PORT=5432, POSTGRES_DB=toilet_tracker_db, SPRING_PROFILES_ACTIVE=docker] Found [APP_PORT=9999, DB_PORT=5432, POSTGRES_DB=toilet_tracker_db, SPRING_PROFILES_ACTIVE=invalid_profile]
Origin: /mnt/c/Users/Sajiya/Desktop/Containerized_Public_Toilet_Maintenance_Tracker/ansible/playbook.yml:250:7

fatal: [localhost]: FAILED! => {
    "assertion": "app_port_found == expected_app_port",
    "changed": false,
    "evaluated_to": false,
    "msg": "CONFIGURATION DRIFT DETECTED in /tmp/test_drift.env! Expected [APP_PORT=8085, DB_PORT=5432, POSTGRES_DB=toilet_tracker_db, SPRING_PROFILES_ACTIVE=docker] Found [APP_PORT=9999, DB_PORT=5432, POSTGRES_DB=toilet_tracker_db, SPRING_PROFILES_ACTIVE=invalid_profile]"
}

PLAY RECAP *********************************************************************
localhost                  : ok=28   changed=0    unreachable=0    failed=1    skipped=0    rescued=0    ignored=0
```

### 9.4 Post-Test Clean-up and Baseline Preservation
The temporary fixture was deleted immediately:
```bash
rm -f /tmp/test_drift.env
```
Subsequent validation confirmed that the primary project `.env` file and running containers remained completely unaffected and healthy.

---

## 10. Security Precautions and Secret Handling

1. **Zero Secret Tracking in Git:** The `.env` file containing local credentials was added to `.gitignore` line 54 and confirmed ignored using `git check-ignore -v .env`.
2. **Template Sanitization:** `.env.example` provides descriptive variable names and default local ports without containing real production passwords, API tokens, or secrets.
3. **Ansible Playbook Secret Protection:** The playbook verifies `.env` presence strictly via the `stat` module rather than printing file contents. Tasks that parse environment values do not print sensitive credential variables in standard output logs.
4. **Isolated Host Network Boundaries:** Container ports `8085` and `5432` were strictly bound to isolated host ports, avoiding any interference with Jenkins (port `8080`), baseline (port `8081`), or staging (port `8082`).

---

## 11. Commands Executed

```bash
# 1. WSL Environment Verification and Ansible Installation
wsl bash -c "which ansible && python3 --version"
wsl bash -c "ansible --version"

# 2. Syntax Check
wsl bash -ic "cd /mnt/c/Users/Sajiya/Desktop/Containerized_Public_Toilet_Maintenance_Tracker/ansible && ansible-playbook --syntax-check playbook.yml"

# 3. Secret Isolation and Profile Inspection
git check-ignore -v .env
powershell -Command "Get-Content src/main/resources/application.properties"
powershell -Command "Get-Content src/main/resources/application-dev.properties"
powershell -Command "Get-Content src/main/resources/application-docker.properties"

# 4. First Playbook Run (Baseline Audit)
wsl bash -ic "cd /mnt/c/Users/Sajiya/Desktop/Containerized_Public_Toilet_Maintenance_Tracker/ansible && ansible-playbook playbook.yml"

# 5. Second Playbook Run (Idempotency Verification)
wsl bash -ic "cd /mnt/c/Users/Sajiya/Desktop/Containerized_Public_Toilet_Maintenance_Tracker/ansible && ansible-playbook playbook.yml"

# 6. Configuration Drift Test Execution
wsl bash -c "
cat << 'EOF' > /tmp/test_drift.env
APP_PORT=9999
DB_PORT=5432
POSTGRES_DB=toilet_tracker_db
SPRING_PROFILES_ACTIVE=invalid_profile
EOF
cd /mnt/c/Users/Sajiya/Desktop/Containerized_Public_Toilet_Maintenance_Tracker/ansible
ansible-playbook playbook.yml -e 'audit_env_file=/tmp/test_drift.env' || true
rm -f /tmp/test_drift.env
"

# 7. Container Health and Persistence Verification
docker compose ps
curl.exe -s http://localhost:8085/api/v1/health
curl.exe -s http://localhost:8085/api/v1/tickets/3

# 8. Final Git and Stack Review
git status --short
git diff --check
docker compose config --quiet
powershell -Command "(Invoke-WebRequest -Uri http://localhost:8080/login -UseBasicParsing).StatusCode"
```

---

## 12. Screenshots and Explanations

All screenshots are stored in `docs/screenshots/Task-13/`:

| Screenshot File | Description & Verification Evidence || :--- | :--- |
| **`01-ansible-installation-and-version.png`** | Demonstrates successful Ansible Core `2.20.1` installation on Python `3.14.4` inside WSL 2 Ubuntu. |
| **`02-inventory-and-playbook-structure.png`** | Shows inventory layout, `inventory.ini`, and clean `ansible-playbook --syntax-check` validation. |
| **`03-spring-profile-segregation.png`** | Shows `.env` Git ignore verification and profile separation across `application*.properties`. |
| **`04-playbook-run-1-baseline-audit.png`** | Complete initial baseline execution of `playbook.yml` passing all 30 audit tasks (`changed=0 failed=0`). |
| **`05-playbook-run-2-idempotency.png`** | Consecutive execution of `playbook.yml` confirming strict idempotency (`ok=30 changed=0 failed=0`). |
| **`06-configuration-drift-detection.png`** | Demonstrates automated drift detection catching invalid port `9999` and bad profile in `/tmp/test_drift.env`. |
| **`07-live-container-health-and-persistence.png`**| Shows `docker compose ps` healthy containers, health endpoint `UP`, and persisted ticket `TKT-20261019-9F7207`. |
| **`08-final-git-status-and-validation.png`** | Clean `git status`, syntax validation, and verification of undisturbed Jenkins on port `8080`. |

---

## 13. Limitations

1. **Hybrid Windows/WSL Network Bridging:** Because Jenkins runs as a native Windows service on `0.0.0.0:8080`, direct Linux socket connections from inside the WSL 2 virtual network namespace may encounter Windows Firewall restrictions unless invoked through Windows PowerShell interoperability (`powershell.exe`).
2. **Read-Only Audit Scope:** The current Ansible implementation focuses on configuration verification and drift detection. Automated drift remediation (e.g., dynamically rewriting drifted `.env` files and executing `docker compose up -d`) was intentionally disabled to adhere strictly to the non-destructive safety constraints of Phase C.
3. **Local Connection Scope:** The inventory targets `localhost` via `ansible_connection=local`. While ideal for container and workstation auditing, distributed cloud deployments will require SSH key provisioning and dynamic inventory plugins (e.g., AWS EC2 or GCP compute plugins).

---

## 14. Conclusion and Future Improvements

### 14.1 Summary of Completion
Task 13 has successfully delivered an end-to-end Configuration Management and Drift Detection framework for the Containerized Public Toilet Maintenance Tracker:
- **Automation Established:** Ansible Core was installed and configured with a local inventory and non-destructive audit playbook.
- **Profiles Segregated:** In-memory H2 development settings and containerized PostgreSQL settings were cleanly divided into distinct Spring Boot profiles.
- **Idempotence Proven:** Two consecutive playbook runs confirmed 100% repeatability with `changed=0 failed=0`.
- **Drift Detected:** An automated test fixture proved that configuration divergence is immediately captured and diagnosed.
- **Zero Disruptions:** Existing persistent PostgreSQL volume data (`TKT-20261019-9F7207`), the running Docker Compose stack, and Jenkins on port `8080` remained completely undisturbed throughout.

### 14.2 Future Improvements
1. **Automated CI Drift Gates:** Integrate the Ansible audit playbook as a mandatory pre-deployment verification stage in the Jenkins CI/CD pipeline (`Jenkinsfile`).
2. **Ansible Vault Integration:** Transition sensitive production database credentials from `.env` files into encrypted Ansible Vault secrets (`ansible-vault`).
3. **Automated Self-Healing:** Implement an automated drift remediation play that can safely reconcile minor configuration drifts with interactive administrator approval.

---

## 15. Academic Viva Voce Preparation

### Q1: What is configuration drift, and why is it dangerous in containerized applications?
**Answer:** Configuration drift is the gradual and often undocumented divergence of an environment's actual runtime configuration from its intended baseline defined in version control. In containerized applications, drift can occur when container environment variables, published ports, or volume mounts are modified imperatively via `docker run` or ad-hoc commands without updating `docker-compose.yml` or `.env` files. It is dangerous because it leads to inconsistent behavior across development, staging, and production environments, introduces elusive runtime bugs, invalidates automated test results, and compromises security by introducing unvetted settings.

### Q2: What is the significance of idempotence in Ansible?
**Answer:** Idempotence is the property where an operation can be applied multiple times without changing the result beyond the initial application. In Ansible, modules are designed to inspect the current state of a managed node before taking action. If the node is already in the desired state, Ansible takes no action and reports `changed=false`. This guarantees that automation playbooks can be safely run repeatedly against production systems without inadvertently overwriting custom data, restarting healthy services, or creating duplicate resources.

### Q3: Why did we separate `application-dev.properties` and `application-docker.properties` into distinct Spring Boot profiles?
**Answer:** In professional software engineering, different environments have conflicting infrastructure requirements. Local developers need a lightweight, zero-dependency environment that boots rapidly without requiring external databases, making an in-memory H2 database ideal (`dev` profile). Conversely, containerized and production deployments require persistent, highly concurrent, ACID-compliant relational databases like PostgreSQL 15 (`docker` profile). By separating these configurations into profile-specific properties files, we eliminate hardcoded connection parameters, prevent accidental test data pollution in production, and adhere to Twelve-Factor App principles for environment parity.

### Q4: How does Ansible's `stat` module prevent secret leakage during configuration auditing?
**Answer:** The `ansible.builtin.stat` module retrieves file metadata (such as existence, file permissions, ownership, modification timestamps, and checksums) from the filesystem without reading, loading, or displaying the file's text contents into Ansible's memory buffers or standard output logs. By using `stat` to verify that `.env` is present, the audit playbook confirms that environment configurations are in place while guaranteeing zero exposure of database passwords or API tokens in terminal logs or console reports.

### Q5: How does Ansible handle execution on localhost using `ansible_connection=local`?
**Answer:** When `ansible_connection=local` is specified in the inventory for `localhost`, Ansible bypasses the SSH transport layer completely. Instead of attempting to open an SSH socket connection, authenticate with private keys, and create remote pseudo-terminals, Ansible directly invokes Python sub-processes on the local system running the control node. This dramatically speeds up playbook execution, eliminates SSH daemon configuration dependencies, and makes Ansible ideal for local container auditing and workstation provisioning.
