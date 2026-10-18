# Containerized_Public_Toilet_Maintenance_Tracker

An end-to-end DevOps automated public toilet maintenance and hygiene tracking system, integrating agile planning, continuous integration with Jenkins, automated Selenium testing, multi-stage Docker containerization, and continuous delivery.

## Academic Project Details
- **Student Name:** Tamanna Shaikh
- **Roll Number:** 24102A2006
- **Department:** Computer Engineering
- **Institute:** Vidyalankar Institute of Technology
- **Academic Year:** 2026–27

## Project Structure

```text
Containerized_Public_Toilet_Maintenance_Tracker/
│
├── src/
│   ├── main/                 # Application source code (Java/Spring Boot)
│   └── test/                 # Unit and Selenium test suites
│
├── frontend/                 # Web interface assets & dashboard components
│
├── datasets/                 # Facility locations, ward maps, maintenance logs
│
├── docs/                     # Documentation, architecture, and diagrams
│   ├── architecture/         # System architecture documents
│   ├── diagrams/             # High-level architecture and pipeline diagrams
│   └── screenshots/          # Evidence and deliverables organized by task (Task-01 to Task-15)
│
├── REPORTS/                  # Comprehensive task completion reports (Task 01 - Task 15)
│
├── Dockerfile                # Multi-stage production container definition
├── docker-compose.yml        # Multi-container local orchestration (App, Database)
├── Jenkinsfile               # Declarative CI/CD pipeline script
├── pom.xml                   # Maven dependencies and build configuration
└── .gitignore                # Git ignore configuration
```

## Quick Start

### 1. Prerequisites
- **Java Development Kit (JDK 17+)**
- **Apache Maven 3.8+**
- **Docker & Docker Compose**
- **Jenkins CI Server**

### 2. Local Build
```bash
mvn clean install
```

### 3. Run with Docker Compose
```bash
docker compose up -d --build
```
