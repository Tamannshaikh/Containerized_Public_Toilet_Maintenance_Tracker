# Task 15: Final Release, Audit & Retrospective

**Project:** Containerized Public Toilet Maintenance Tracker
**Student:** Tamanna Shaikh (Roll No: 24102A2006)
**Department:** Computer Engineering | Vidyalankar Institute of Technology (VIT Mumbai)
**Course:** DevOps Laboratory | Academic Year 2026–2027
**Active Branch:** `feature/maintenance-ticket-api`

---

> [!IMPORTANT]
> The full, comprehensive Task 15 academic report and release audit is maintained in:
> **[REPORTS/Task-15-Final-Release-Audit-Retrospective.md](Task-15-Final-Release-Audit-Retrospective.md)**

---

## 1. Executive Summary

Task 15 represents the culmination and formal academic release audit for the **Containerized Public Toilet Maintenance Tracker**. It synthesizes the complete 15-task Agile journey across the 8 continuous DevOps lifecycle phases:

1. **Problem Definition & Inception (Tasks 01–04):** Urban public toilet maintenance problem statement, 5 MVP modules, 5 Epics, 10 User Stories, and GitFlow governance.
2. **Core Feature Engineering (Tasks 05–06):** Spring Boot 3.2 backend, JPA entities, REST controllers, and vanilla JS/CSS web dashboard.
3. **CI/CD & Continuous Testing (Tasks 07–10):** Native Jenkins service on port `8080`, declarative pipeline ([`Jenkinsfile`](../Jenkinsfile)), automated staging deployments (port `8082`), and 5 headless Selenium UI test journeys.
4. **Containerization & Orchestration (Tasks 11–12):** Multi-stage build ([`Dockerfile`](../Dockerfile)), unprivileged user execution (`USER appuser`), and multi-container Docker Compose stack ([`docker-compose.yml`](../docker-compose.yml)) with named volume persistence (`pgdata`).
5. **Configuration Management & Reliability (Tasks 13–14):** Ansible-driven configuration audit and drift detection, Spring profile segregation (`dev`/`docker`), `json-file` log rotation (`10m`/`3`), and automated non-destructive database backup/restore verification ([`scripts/backup-db.ps1`](../scripts/backup-db.ps1), [`scripts/restore-db.ps1`](../scripts/restore-db.ps1)).
6. **Final Release Audit (Task 15):** Comprehensive verification matrix, evidence catalog (60 verified screenshots), security audit, retrospective, and viva defense guide.

---

## 2. Key Release Metrics & Status

- **Working Tree State:** Completely clean (`git status` clean, zero uncommitted changes).
- **Git Commit Parity:** Upstream branch `origin/feature/maintenance-ticket-api` is 100% in sync (`0 0` divergence).
- **Main Branch Integrity:** Main integration trunk (`main`) remains untouched at baseline `a881840`.
- **Runtime Stack Health:** Both `public-toilet-app` and `public-toilet-db` reporting healthy status on port `8085` and `5432`.
- **Relational Data Durability:** Test ticket `TKT-20261019-9F7207` verified intact in PostgreSQL volume `pgdata`.
- **Disaster Recovery Proof:** Automated database restore verified into isolated temporary database `toilet_tracker_verify_db` with zero live data risk.
- **Visual Evidence Catalog:** Exactly 60 physical screenshots mapped across Tasks 09–15 (51 historical + 9 Task 15 final release) in [`docs/screenshots/`](../docs/screenshots/).

---

## 3. Comprehensive Documentation Reference

For complete engineering data, architecture diagrams, command outputs, limitations, retrospective findings, and academic viva voce defense preparation, refer to:
- **Comprehensive Final Report:** [Task-15-Final-Release-Audit-Retrospective.md](Task-15-Final-Release-Audit-Retrospective.md)
