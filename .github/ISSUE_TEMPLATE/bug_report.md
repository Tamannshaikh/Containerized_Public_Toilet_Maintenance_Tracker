---
name: Bug Report
about: Create a report to help us improve the Containerized Public Toilet Maintenance Tracker
title: '[BUG] '
labels: 'bug'
assignees: ''
---

## Summary
A clear and concise description of what the bug is.

## Affected Component
- [ ] Backend REST API (`/api/v1/...`)
- [ ] Web Dashboard UI (`frontend/`)
- [ ] Database / Persistence (PostgreSQL / H2)
- [ ] Docker Containerization
- [ ] Jenkins CI/CD Pipeline
- [ ] Automated Selenium Testing

## Steps to Reproduce
1. Navigate to '...'
2. Click on '....'
3. Submit payload '....'
4. See error

## Expected Behaviour
A clear and concise description of what you expected to happen.

## Actual Behaviour
What actually occurred (include exact HTTP status codes, error traces, or console outputs).

## Screenshots / Terminal Logs
If applicable, attach screenshots or paste terminal logs to help explain the problem.

## Environment Details
- **OS:** Windows 11 / Linux (Docker)
- **Java Version:** Java 17 LTS / JDK 26
- **Build Tool:** Apache Maven 3.9+
- **Database:** PostgreSQL 15 / H2 In-Memory
- **Browser (for UI/Selenium):** Headless Chrome / Selenium WebDriver 4.19+

## Acceptance Criteria for Resolution
- [ ] Root cause identified and covered by automated test.
- [ ] Defect resolved with zero regressions in existing test suite.
- [ ] Verified via `mvn clean test` and pipeline green build.
