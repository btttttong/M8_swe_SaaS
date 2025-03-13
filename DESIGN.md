
# 🎨 System Design

## 🏛️ High-Level Design
The system follows a **modular approach**, dividing concerns into **Controllers, Services, Repositories, and Models**.

### 📂 Package Structure
```
com.swe.saas
│── controller/   # REST API controllers
│── service/      # Business logic layer
│── repository/   # Database interactions
│── model/        # Data models (entities)
│── scheduler/    # Scheduled jobs (GitHub polling)
│── config/       # Configuration files
│── SaasApplication.java  # Main entry point
```

## 🛠️ Key Design Patterns Used
1. **MVC Pattern** - Separates concerns for better maintainability.
2. **Singleton Pattern** - Ensures a single instance for services.
3. **Repository Pattern** - Abstracts database access.
4. **Observer Pattern** - Used in email notification alerts.

## 🔄 Data Flow
1. **User registers a repository** → Stored in DB.
2. **Scheduler polls GitHub API** → Fetches updates.
3. **Data is stored in SQLite** → To reduce API calls.
4. **Notifications sent** → If conditions match an alert.

## 🚀 Optimizations
- **API Rate Limit Handling** - Delays retries when GitHub API limit is reached.
- **Efficient DB Queries** - Uses indexing for fast lookups.
- **Logging & Debugging** - Uses SLF4J logging for better insights.
