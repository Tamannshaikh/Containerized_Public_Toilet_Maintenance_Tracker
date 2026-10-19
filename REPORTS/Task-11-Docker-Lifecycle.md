# Task 11: Containerization & Docker Lifecycle

**Project:** Containerized Public Toilet Maintenance Tracker
**Student:** Tamanna Shaikh (Roll No: 24102A2006)
**Department:** Computer Engineering | Vidyalankar Institute of Technology (VIT Mumbai)
**Course:** DevOps Laboratory (2026–2027)
**Active Branch:** `feature/maintenance-ticket-api`
**Status:** **Completed & Fully Verified**

> [!NOTE]
> The full comprehensive academic laboratory report with detailed command execution logs, image layer analysis, and viva voce preparation is documented in:
> [Task-11-Docker-Image-Container-Lifecycle.md](file:///c:/Users/Sajiya/Desktop/Containerized_Public_Toilet_Maintenance_Tracker/REPORTS/Task-11-Docker-Image-Container-Lifecycle.md)

---

## 1. Objective
Author a multi-stage Dockerfile, author `.dockerignore` for context optimization, build and dual-tag container images (`public-toilet-tracker:v1.0.0` and `:latest`), run the container in an isolated environment on host port `8085:8081` without conflicting with existing services on ports `8080`, `8081`, or `8082`, verify application health and REST APIs, and demonstrate the complete container lifecycle (running, stopping, restarting, recovery, and removal).

---

## 2. Technical Summary

| Attribute | Specification | Verification Result |
| :--- | :--- | :--- |
| **Base Build Image** | `maven:3.9-eclipse-temurin-17` | Multi-stage builder, compiled in 21.897s |
| **Base Runtime Image** | `eclipse-temurin:17-jre-alpine` | Alpine Linux 3.x, Temurin JRE 17 |
| **Security User** | `appuser` (non-root) | Principle of least privilege enforced |
| **Health Check** | Alpine BusyBox `wget` | `wget -q --spider http://localhost:8081/api/v1/health` |
| **Image Tags** | `public-toilet-tracker:v1.0.0`, `:latest` | Image ID `c76b96d26b4e`, Size: 357 MB |
| **Container Name** | `toilet-tracker-task11` | Container ID: `9b5530c10352` |
| **Port Forwarding** | Host `8085` -> Container `8081` | Fully preserved ports `8080`, `8081`, `8082` |
| **Stop Verification** | `docker stop` | Graceful SIGTERM exit code `143` |
| **Restart & Recovery** | `docker start` | Re-initialized in 5.68s, status transitioned to `healthy` |
| **Safe Removal** | `docker rm` | Container removed; both image tags preserved |

---

## 3. Artifacts & Screenshots

All execution proof and screenshots are stored in [docs/screenshots/Task-11/](file:///c:/Users/Sajiya/Desktop/Containerized_Public_Toilet_Maintenance_Tracker/docs/screenshots/Task-11):

| Screenshot File | Description |
| :--- | :--- |
| [`01-docker-daemon-version.png`](file:///c:/Users/Sajiya/Desktop/Containerized_Public_Toilet_Maintenance_Tracker/docs/screenshots/Task-11/01-docker-daemon-version.png) | Docker daemon version and system environment info |
| [`02-docker-build-and-tag.png`](file:///c:/Users/Sajiya/Desktop/Containerized_Public_Toilet_Maintenance_Tracker/docs/screenshots/Task-11/02-docker-build-and-tag.png) | Multi-stage build output and image tagging evidence |
| [`03-docker-run-port-mapping.png`](file:///c:/Users/Sajiya/Desktop/Containerized_Public_Toilet_Maintenance_Tracker/docs/screenshots/Task-11/03-docker-run-port-mapping.png) | Container execution, ID `9b5530c10352`, and port forwarding |
| [`04-container-inspect-and-logs.png`](file:///c:/Users/Sajiya/Desktop/Containerized_Public_Toilet_Maintenance_Tracker/docs/screenshots/Task-11/04-container-inspect-and-logs.png) | Container state, health status, and Tomcat boot logs |
| [`04-container-web-dashboard.png`](file:///c:/Users/Sajiya/Desktop/Containerized_Public_Toilet_Maintenance_Tracker/docs/screenshots/Task-11/04-container-web-dashboard.png) | Live browser capture of web UI served from container on port 8085 |
| [`05-health-endpoint-verification.png`](file:///c:/Users/Sajiya/Desktop/Containerized_Public_Toilet_Maintenance_Tracker/docs/screenshots/Task-11/05-health-endpoint-verification.png) | Browser verification of `/api/v1/health` JSON payload |
| [`05-facilities-api-verification.png`](file:///c:/Users/Sajiya/Desktop/Containerized_Public_Toilet_Maintenance_Tracker/docs/screenshots/Task-11/05-facilities-api-verification.png) | Browser verification of `/api/v1/facilities` JSON data |
| [`06-container-stop.png`](file:///c:/Users/Sajiya/Desktop/Containerized_Public_Toilet_Maintenance_Tracker/docs/screenshots/Task-11/06-container-stop.png) | Graceful stop and `Exited (143)` verification |
| [`06-container-restart.png`](file:///c:/Users/Sajiya/Desktop/Containerized_Public_Toilet_Maintenance_Tracker/docs/screenshots/Task-11/06-container-restart.png) | Container restart, port re-binding, and 5.6s Spring Boot boot |
| [`06-container-health-recovery.png`](file:///c:/Users/Sajiya/Desktop/Containerized_Public_Toilet_Maintenance_Tracker/docs/screenshots/Task-11/06-container-health-recovery.png) | HTTP probe recovery and Docker `healthy` confirmation |
| [`07-container-removal-and-image-preservation.png`](file:///c:/Users/Sajiya/Desktop/Containerized_Public_Toilet_Maintenance_Tracker/docs/screenshots/Task-11/07-container-removal-and-image-preservation.png) | Safe container removal with images and port 8080 preserved |

---

## 4. Conclusion & Next Steps
Task 11 has been completed with complete verification. Ready for **Task 12: Continuous Deployment with Docker in Jenkins CI/CD**.
