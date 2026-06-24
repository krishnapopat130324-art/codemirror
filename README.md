# 🔍 CodeMirror

### AI-Powered Code Analysis, Security Scanning & Quality Assessment Platform

CodeMirror is a production-ready code analysis platform designed to help developers identify bugs, security vulnerabilities, code smells, and maintainability issues before they reach production.

The platform combines static analysis, security scanning, quality scoring, and intelligent recommendations into a single developer-friendly dashboard that supports multiple programming languages and local execution.

---

## 📖 Overview

CodeMirror improves software quality through automated inspection and intelligent feedback.

The system analyzes source code to detect:

- Bugs and risky coding patterns
- Security vulnerabilities
- Code smells and maintainability issues
- Performance concerns
- Industry best-practice violations

The result is a detailed analysis report containing quality scores, security ratings, detected issues, and actionable suggestions.

---

## 🌟 Features

### 🐛 Bug Detection

Automatically detects common programming issues including:

- Division by zero
- Infinite loops
- Empty catch blocks
- Magic numbers
- Print statements in production code
- Too many method parameters
- Unused variables
- Code smells
- Maintainability issues

---

### 🔒 Security Scanning

Identify critical security vulnerabilities including:

- Hardcoded passwords
- SQL Injection vulnerabilities
- Hardcoded API keys
- Hardcoded URLs
- Stack trace exposure
- Unsafe coding practices

---

### 📊 Quality Metrics

Every analysis generates:

- Quality Score (0-100)
- Issue Count and Severity Levels
- Security Issue Count
- Smart Suggestions for Improvements
- Detailed Analysis Summary

---

### 🌐 Multi-Language Support

Supported languages include:

- Java (Full Support)
- Python
- JavaScript
- TypeScript
- C
- C++

---

### 🎨 Modern Dashboard

- Clean and responsive UI
- Real-time analysis results
- Interactive issue reporting
- Keyboard shortcut support (`Ctrl + Enter`)
- Developer-friendly interface

---

## 🚀 Technology Stack

### Backend Technologies

| Technology | Version | Purpose |
|------------|---------|---------|
| Java | 17+ | Core programming language |
| Spring Boot | 2.7+ | Backend framework |
| Maven | 3.x | Build automation |
| Lombok | 1.18+ | Boilerplate reduction |
| Embedded Tomcat | Included | Application server |

### Frontend Technologies

| Technology | Version | Purpose |
|------------|---------|---------|
| HTML5 | Latest | Application structure |
| CSS3 | Latest | Styling and layout |
| JavaScript | ES6+ | Client-side functionality |

---

## 🛠️ Installation

### Prerequisites

- Java 17 or higher
- Maven 3.x or higher

### Step 1: Clone Repository

```bash
git clone https://github.com/krishnapopat130324-art/codemirror.git
cd codemirror
```

### Step 2: Build and Run Backend

```bash
mvn clean install
mvn spring-boot:run
```

Backend server starts at:

```text
http://localhost:8080
```

### Step 3: Open Frontend

Open the following file in your browser:

```text
frontend/index.html
```

---

## 📡 API Endpoints

### Health Check

```http
GET /api/health
```

Response:

```text
✅ CodeMirror is running!
```

---

### Analyze Code

```http
POST /api/analyze
```

Example Request:

```json
{
  "code": "public class Test { public void hello() { System.out.println(\"Hi\"); } }",
  "language": "java"
}
```

Example Response:

```json
{
  "language": "java",
  "qualityScore": 85,
  "issues": [
    "System.out.println used in production code."
  ],
  "securityIssues": [],
  "suggestions": [
    "Use a proper logging framework like SLF4J or Logback."
  ]
}
```

---

## 📁 Project Structure

```text
codemirror/
│
├── backend/
│   └── src/main/java/com/codemirror/
│       └── App.java
│
├── frontend/
│   └── index.html
│
├── pom.xml
├── .gitignore
└── README.md
```

---

## 🎯 Example Analysis

### Input

```java
public class Calculator {

    public int divide(int a, int b) {
        return a / b;
    }

    public void login() {
        String password = "admin123";
        System.out.println("Logged in!");
    }
}
```

### Output

```text
Quality Score: 70/100

⚠️ Issues Detected:
• Division by zero risk detected
• Hardcoded password detected
• System.out.println used in production code

🔒 Security Issues:
• Hardcoded credentials found

💡 Suggested Improvements:
• Validate divisor before division
• Move credentials to environment variables
• Replace print statements with a logging framework
```

---


## 👨‍💻 Author

### Krishna Popat

Passionate about developer tools, software quality engineering, and intelligent automation solutions.

---

## ⭐ Support

If you found this project useful, consider giving it a ⭐ on GitHub.

---

<div align="center">

Built with ❤️ by Krishna Popat

</div>
