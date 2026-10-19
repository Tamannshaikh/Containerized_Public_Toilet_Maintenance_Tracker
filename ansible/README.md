# Ansible Configuration Management & Environment Drift Audit

This directory contains Ansible automation assets for **Task 13: Configuration Management & Environment Drift** for the *Containerized Public Toilet Maintenance Tracker* project.

---

## 1. Directory Structure

```
ansible/
├── ansible.cfg       # Ansible configuration (inventory path, YAML callback, python interpreter)
├── inventory.ini     # Local inventory targeting localhost via local connection
├── playbook.yml      # Idempotent configuration audit and drift detection playbook
└── README.md         # Documentation and execution instructions
```

---

## 2. Prerequisites

- **WSL 2 (Ubuntu)** or Linux host environment
- **Ansible Core 2.20.1+** installed (`sudo apt-get install ansible-core`)
- **Python 3.12+ / 3.14+**
- **Docker & Docker Compose** (running Docker Desktop with WSL 2 integration)
- Live project stack running on host port `8085` and `5432`

---

## 3. Usage Instructions

### 3.1 Syntax Check
```bash
ansible-playbook --syntax-check playbook.yml
```

### 3.2 Baseline Audit Execution
Run the playbook against the local baseline configuration:
```bash
ansible-playbook playbook.yml
```

### 3.3 Idempotency Verification
Re-run the playbook without any changes to confirm that the audit is completely repeatable and changes nothing:
```bash
ansible-playbook playbook.yml
```
Expected result: `changed=0 failed=0`.

### 3.4 Configuration Drift Detection
To test drift detection non-destructively against an altered test fixture:
```bash
# 1. Create a temporary test fixture with drifted values
cat << 'EOF' > /tmp/test_drift.env
APP_PORT=9999
DB_PORT=5432
POSTGRES_DB=toilet_tracker_db
SPRING_PROFILES_ACTIVE=invalid_profile
EOF

# 2. Run the playbook passing the fixture path
ansible-playbook playbook.yml -e "audit_env_file=/tmp/test_drift.env"

# 3. Clean up the temporary fixture
rm -f /tmp/test_drift.env
```
The playbook will immediately catch the drift and fail with detailed diagnostics showing the expected vs. found values, while leaving the production stack completely untouched.

---

## 4. Audit Coverage

1. **Configuration File Integrity**: Ensures `pom.xml`, `Dockerfile`, `docker-compose.yml`, `.env.example`, and profile properties exist.
2. **Secret Isolation**: Verifies `.env` exists locally without leaking contents and confirms `.env` is ignored by Git.
3. **Profile Segregation**: Confirms local development defaults to H2 (`application-dev.properties`) while Docker containers use PostgreSQL (`application-docker.properties`).
4. **Docker Stack Health**: Validates Docker daemon and checks that `public-toilet-app` and `public-toilet-db` are `running:healthy`.
5. **Non-Destructive Port Probing**: Audits ports `8085` and `5432` without interfering with Jenkins on port `8080`.
6. **Live Data Persistence**: Verifies the REST API endpoint and confirms ticket `TKT-20261019-9F7207` persisted in PostgreSQL.
7. **Drift Detection**: Audits environment parameters against expected baselines.
