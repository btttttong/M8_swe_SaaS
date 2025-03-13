# GitHub Activity Tracker 🚀  
A Spring Boot application that tracks GitHub repositories, fetches commits/issues/releases, and sends alerts.

## 📦 Installation  
```bash
git clone https://github.com/your-repo.git
cd your-repo
./gradlew bootRun
```

## ⚙️ Configuration  
Create a `.env` file with:  
```env
GITHUB_TOKEN=your_personal_access_token
SPRING_MAIL_USERNAME=your-email@gmail.com
SPRING_MAIL_PASSWORD=your-app-password
```

## 🛠 API Usage  
### **Register a repository**  
```bash
curl -X POST "http://localhost:9090/api/github/activity/register"      -H "Content-Type: application/json"      -d '{"owner": "btttttong", "name": "M8_swe_SaaS"}'
```
### **Fetch recent activities**  
```bash
curl -X GET "http://localhost:9090/api/github/activity/fetch?owner=btttttong&repo=M8_swe_SaaS"
```

