# 🔍 CodeMirror - AI Code Analysis & Security Scanner

> Intelligent code review, bug detection, and security analysis platform for modern developers.

CodeMirror is an AI-inspired static code analysis platform designed to automatically detect bugs, security vulnerabilities, code smells, and maintainability issues while generating actionable recommendations and quality scores.

The platform combines rule-based analysis, security scanning, and quality assessment into a single developer-friendly dashboard that helps teams write cleaner, safer, and more maintainable code.

---

## 📖 Overview

CodeMirror helps developers improve software quality through automated inspection and intelligent recommendations.

The system analyzes source code to identify bugs, risky coding practices, security vulnerabilities, and maintainability concerns while generating detailed reports and quality metrics.

Whether you're a student, developer, or software engineer, CodeMirror provides valuable insights that improve productivity and code reliability.

---

## 🏗️ The Technology Behind It

| Component       | Technology              | Purpose                                |
| --------------- | ----------------------- | -------------------------------------- |
| Backend         | Java + Spring Boot      | Handles API requests and code analysis |
| Analysis Engine | Custom Java Logic       | Detects bugs and security issues       |
| Frontend        | HTML + CSS + JavaScript | Provides modern user interface         |
| Build Tool      | Maven                   | Dependency management and builds       |
| Server          | Embedded Tomcat         | Runs application locally               |

---

## 🌟 Features

### 🐛 Bug Detection

Detects 15+ common programming issues including:

* Division by zero
* Infinite loops
* Empty catch blocks
* Magic numbers
* Print statements in production code
* Too many method parameters
* Unused variables
* Code smells
* Maintainability issues

---

### 🔒 Security Scanning

Identifies critical security vulnerabilities such as:

* Hardcoded passwords
* SQL Injection vulnerabilities
* Hardcoded API keys
* Hardcoded URLs
* Stack trace exposure
* Unsafe coding practices

---

### 📊 Quality Metrics

Generate intelligent quality insights including:

* Quality Score (0-100)
* Issue Count and Severity Levels
* Security Risk Analysis
* Smart Improvement Suggestions
* Detailed Analysis Summary

---

### 🌐 Multi-Language Support

Supported languages include:

* Java (Full Support)
* Python
* JavaScript
* TypeScript
* C
* C++

---

### 🎨 Beautiful User Interface

* Clean and modern dashboard
* Multiple color themes
* Real-time analysis
* Interactive result visualization
* Keyboard shortcut support (`Ctrl + Enter`)

---

## 🚀 Tech Stack

### Backend Technologies

| Technology  | Version | Purpose                   |
| ----------- | ------- | ------------------------- |
| Java        | 17+     | Core programming language |
| Spring Boot | 2.7.0   | Backend framework         |
| Maven       | 3.x     | Build automation          |
| Lombok      | 1.18.24 | Boilerplate reduction     |

### Frontend Technologies

| Technology | Version | Purpose                   |
| ---------- | ------- | ------------------------- |
| HTML5      | Latest  | Application structure     |
| CSS3       | Latest  | Styling and layout        |
| JavaScript | ES6+    | Client-side functionality |

---

## 🛠️ Installation

### Prerequisites

* Java 17 or higher
* Maven 3.x or higher

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

Request Example:

```json
{
  "code": "public class Test { public void hello() { System.out.println(\"Hi\"); } }",
  "language": "java"
}
```

Response Example:

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

## 🎯 Sample Usage

### Example: Code Analysis

#### Input

```java
public class Calculator {

    public int divide(int a, int b) {
        return a / b; // Division by zero risk
    }

    public void login() {
        String password = "admin123"; // Hardcoded password
        System.out.println("Logged in!");
    }
}
```

#### Analysis Result

```text
Quality Score: 70/100

⚠️ Issues Detected:
- Division by zero risk detected
- Hardcoded password detected
- System.out.println used in production code

🔒 Security Issues:
- Hardcoded credentials found

💡 Suggested Improvements:
- Validate divisor before division
- Move credentials to environment variables
- Replace print statements with a logging framework
```
---

## 👨‍💻 Author

### Krishna Popat

Passionate about building AI-powered developer tools, automation solutions, and intelligent software systems.

---

## ⭐ Support

If you found this project useful, consider giving it a star on GitHub.

Made with ❤️ by Krishna Popat
