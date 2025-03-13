
# 📐 System Architecture

## Overview
This project is a **Spring Boot application** that integrates with the **GitHub API** to monitor repositories for commits, issues, and releases.

## 🏗️ Architecture Design
- **Backend:** Spring Boot (Java)
- **Database:** SQLite (using Spring Data JPA)
- **External API:** GitHub API (REST)
- **Scheduler:** Spring Scheduler for periodic GitHub polling
- **Security:** Basic authentication (Spring Security)

## 📡 Components
1. **GitHub Activity Service** - Fetches commits, issues, and releases from GitHub.
2. **Database Layer** - Stores repository tracking details and fetched activities.
3. **Scheduler** - Periodically polls GitHub for new activities.
4. **Notification System** - Sends email alerts for specific repository events.

## 🔗 Flow Diagram
1. User registers a GitHub repository.
2. System periodically polls GitHub API.
3. Data is stored in SQLite to avoid redundant API calls.
4. Alerts are sent via email when configured events occur.

---

## 📌 Key Technologies
- **Spring Boot** - Main framework
- **Spring Data JPA** - Database ORM
- **Quartz Scheduler** - Periodic GitHub polling
- **Spring Mail** - Email notifications
- **JUnit & Mockito** - Testing
