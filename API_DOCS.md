# 📊 API Documentation - GitHub Repository Monitoring System

This document describes the API endpoints, request formats, and response structures for the GitHub Repository Monitoring System.

## 🔑 Authentication

All API requests must include an **Authorization** header with a GitHub personal access token.

```
Authorization: Bearer YOUR_GITHUB_TOKEN
```

---

## **1️⃣ Register a Repository for Tracking**
**Endpoint:**  
`POST /api/github/activity/register`

**Description:**  
Allows users to register a GitHub repository for monitoring.

**Request Body (JSON):**
```json
{
  "owner": "btttttong",
  "name": "M8_swe_SaaS"
}
```

**Response (Success - 200 OK):**
```json
"✅ Repo registered successfully!"
```

**Response (Failure - 400 Bad Request):**
```json
"❌ Repo already registered!"
```

---

## **2️⃣ Fetch Recent Activities**
**Endpoint:**  
`GET /api/github/activity/fetch`

**Query Parameters:**
| Parameter | Type   | Required | Description |
|-----------|--------|----------|-------------|
| `owner`   | String | ✅ Yes    | Repository owner |
| `repo`    | String | ✅ Yes    | Repository name |

**Example Request:**
```
GET /api/github/activity/fetch?owner=btttttong&repo=M8_swe_SaaS
Authorization: Bearer YOUR_GITHUB_TOKEN
```

**Response (Success - 200 OK):**
```json
[
  {
    "repositoryOwner": "btttttong",
    "repositoryName": "M8_swe_SaaS",
    "eventType": "Commit",
    "details": "{commit details...}",
    "eventTimestamp": "2025-03-13T19:25:04.308+00:00"
  }
]
```

**Response (Failure - 403 Forbidden if repo not registered):**
```json
"❌ Repo is not registered. Please register first."
```

---

## **3️⃣ View Stored Activities**
**Endpoint:**  
`GET /api/github/activity/stored`

**Query Parameters:**
| Parameter | Type   | Required | Description |
|-----------|--------|----------|-------------|
| `owner`   | String | ✅ Yes    | Repository owner |
| `repo`    | String | ✅ Yes    | Repository name |

**Response (Success - 200 OK):**
```json
[
  {
    "repositoryOwner": "btttttong",
    "repositoryName": "M8_swe_SaaS",
    "eventType": "Issue",
    "details": "{issue details...}",
    "eventTimestamp": "2025-03-13T19:25:04.308+00:00"
  }
]
```

---

## **4️⃣ View Aggregated Statistics**
**Endpoint:**  
`GET /api/github/stats`

**Description:**  
Returns statistics on repository activities.

**Response (Success - 200 OK):**
```json
{
  "totalCommits": 10,
  "totalPullRequests": 5,
  "totalIssues": 3,
  "mostActiveRepo": "btttttong/M8_swe_SaaS",
  "mostActiveUser": "supakavadee r"
}
```

---

## **5️⃣ Set Up Custom Alerts**
**Endpoint:**  
`POST /api/github/alerts`

**Description:**  
Allows users to create alerts for specific repository events.

**Request Body (JSON):**
```json
{
  "owner": "btttttong",
  "repo": "M8_swe_SaaS",
  "eventType": "commit",
  "condition": "new commit"
}
```

**Response (Success - 200 OK):**
```json
"✅ Alert set successfully!"
```

---

## **6️⃣ Test API Connection**
**Endpoint:**  
`GET /api/github/activity/test`

**Response (Success - 200 OK):**
```json
"✅ API is working!"
```

---

# 🚀 Additional Notes
- **Rate Limits:** If you hit GitHub’s rate limit, the system will **retry automatically**.
- **Error Handling:** Proper responses are given for invalid API calls.
- **Security:** API tokens should **never** be hardcoded in the codebase.

