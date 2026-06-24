package com.codemirror;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.*;
import org.springframework.context.annotation.Bean;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import java.util.HashMap;
import java.util.Map;
import java.util.ArrayList;
import java.util.List;

@SpringBootApplication
@RestController
@CrossOrigin(origins = "*")
public class App {

    public static void main(String[] args) {
        SpringApplication.run(App.class, args);
        System.out.println("""
            ╔═══════════════════════════════════════════╗
            ║  🚀 CodeMirror Started Successfully!     ║
            ║  📝 Code Review Assistant is Ready       ║
            ║  🌐 http://localhost:8080                ║
            ╚═══════════════════════════════════════════╝
        """);
    }

    // ============================================================
    // CORS CONFIGURATION - Allows frontend to communicate with backend
    // ============================================================
    @Bean
    public WebMvcConfigurer corsConfigurer() {
        return new WebMvcConfigurer() {
            @Override
            public void addCorsMappings(CorsRegistry registry) {
                registry.addMapping("/**")
                        .allowedOrigins("*")
                        .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                        .allowedHeaders("*")
                        .allowCredentials(false);
            }
        };
    }

    // ============================================================
    // HEALTH CHECK ENDPOINT
    // ============================================================
    @GetMapping("/api/health")
    public String health() {
        return "✅ CodeMirror is running!";
    }

    // ============================================================
    // CODE ANALYSIS ENDPOINT
    // ============================================================
    @PostMapping("/api/analyze")
    public Map<String, Object> analyzeCode(@RequestBody Map<String, String> request) {
        // Get code and language from request
        String code = request.get("code");
        String language = request.getOrDefault("language", "java");
        
        // Create response object
        Map<String, Object> response = new HashMap<>();
        
        // ============================================================
        // BASIC METRICS
        // ============================================================
        response.put("language", language);
        response.put("lines", code.split("\n").length);
        response.put("characters", code.length());
        
        // ============================================================
        // BUG DETECTION
        // ============================================================
        List<String> issues = new ArrayList<>();
        List<String> securityIssues = new ArrayList<>();
        List<String> suggestions = new ArrayList<>();
        
        // 1. Check for division by zero
        if (code.contains("/ 0") || code.contains("/0") || code.contains(" /0")) {
            issues.add("⚠️ Division by zero detected! Check if denominator can be 0.");
            suggestions.add("Add a condition to check if denominator is not zero before division.");
        }
        
        // 2. Check for hardcoded passwords
        if (code.toLowerCase().contains("password") && (code.contains("=") || code.contains("=="))) {
            securityIssues.add("🔴 Hardcoded password detected! Never hardcode credentials.");
            suggestions.add("Use environment variables or a secure vault for passwords.");
        }
        
        // 3. Check for hardcoded secrets / API keys
        if (code.toLowerCase().contains("secret") || 
            code.toLowerCase().contains("apikey") || 
            code.toLowerCase().contains("api_key")) {
            securityIssues.add("🔴 Hardcoded secret/API key detected!");
            suggestions.add("Store secrets in environment variables or a secure configuration.");
        }
        
        // 4. Check for infinite loops
        if (code.contains("while (true)") || code.contains("while(true)")) {
            issues.add("🔴 Infinite loop detected! This will crash your application.");
            suggestions.add("Add a break condition or a timeout mechanism.");
        }
        
        // 5. Check for SQL Injection
        if (code.toUpperCase().contains("SELECT") && code.contains("+")) {
            securityIssues.add("🔴 SQL Injection risk detected!");
            suggestions.add("Use PreparedStatement with parameterized queries instead of string concatenation.");
        }
        
        // 6. Check for empty catch blocks
        if (code.contains("catch") && code.contains("{}")) {
            issues.add("⚠️ Empty catch block found! Exceptions should be handled properly.");
            suggestions.add("Log the exception or handle it appropriately.");
        }
        
        // 7. Check for System.out.println
        if (code.contains("System.out.println")) {
            issues.add("💡 System.out.println used in production code.");
            suggestions.add("Use a proper logging framework like SLF4J or Logback.");
        }
        
        // 8. Check for printStackTrace
        if (code.contains("printStackTrace")) {
            issues.add("⚠️ printStackTrace() used! This exposes stack traces to users.");
            suggestions.add("Use a logging framework to log exceptions properly.");
        }
        
        // 9. Check for Thread.sleep in loops
        if (code.contains("Thread.sleep") && (code.contains("while") || code.contains("for"))) {
            issues.add("⚠️ Thread.sleep() in loop detected! This can cause performance issues.");
            suggestions.add("Consider using a scheduled executor service.");
        }
        
        // 10. Check for magic numbers
        if (code.matches(".*[^\\w]\\d{3,}.*") && !code.contains("//") && !code.contains("/*")) {
            issues.add("💡 Magic number detected. Numbers without explanation are hard to maintain.");
            suggestions.add("Define constants with meaningful names for numbers.");
        }
        
        // 11. Check for null checks
        if (code.contains(".equals(") && !code.contains("null")) {
            issues.add("💡 Potential NullPointerException risk.");
            suggestions.add("Add null checks before calling methods on objects.");
        }
        
        // 12. Check for hardcoded URLs
        if ((code.contains("http://") || code.contains("https://")) && 
            !code.contains("properties") && !code.contains("config")) {
            issues.add("💡 Hardcoded URL detected.");
            suggestions.add("Move URLs to configuration files.");
        }
        
        // 13. Check for too many parameters in a method
        if (code.matches(".*\\w+\\s*\\([^)]*\\).*")) {
            String[] parts = code.split(",");
            int paramCount = parts.length - 1;
            if (paramCount > 5) {
                issues.add("⚠️ Method with " + paramCount + " parameters detected. Too many!");
                suggestions.add("Consider using a DTO (Data Transfer Object) or builder pattern.");
            }
        }
        
        // 14. Check for TODO comments
        if (code.contains("TODO") || code.contains("FIXME")) {
            issues.add("💡 TODO/FIXME comment found. This might be unfinished work.");
            suggestions.add("Complete the TODO items before deployment.");
        }
        
        // 15. Check for large methods (simplified)
        if (code.split("\n").length > 50) {
            issues.add("⚠️ Large method detected. Methods should be short and focused.");
            suggestions.add("Break down large methods into smaller, focused functions.");
        }

        // ============================================================
        // QUALITY SCORE CALCULATION
        // ============================================================
        int score = 100;
        
        // Deduct points for each issue
        score -= issues.size() * 5;           // Each issue = -5 points
        score -= securityIssues.size() * 15;  // Each security issue = -15 points
        
        // Bonus for clean code
        if (issues.isEmpty() && securityIssues.isEmpty()) {
            score = Math.min(score + 5, 100); // Bonus for clean code
        }
        
        // Ensure score is between 0 and 100
        score = Math.max(0, Math.min(100, score));
        
        // ============================================================
        // BUILD RESPONSE
        // ============================================================
        response.put("issues", issues);
        response.put("securityIssues", securityIssues);
        response.put("suggestions", suggestions);
        response.put("qualityScore", score);
        
        // Determine status based on issues found
        if (issues.isEmpty() && securityIssues.isEmpty()) {
            response.put("status", "✅ Code looks clean!");
            response.put("message", "No issues found. Great job! 🎉");
        } else if (!securityIssues.isEmpty()) {
            response.put("status", "🔴 CRITICAL: Security issues found!");
            response.put("message", "Fix security issues immediately before deploying!");
        } else if (issues.size() > 3) {
            response.put("status", "⚠️ Multiple issues found!");
            response.put("message", "Please review all issues and fix them for better code quality.");
        } else {
            response.put("status", "⚠️ Issues found!");
            response.put("message", "Review the issues above and fix them to improve code quality.");
        }
        
        // ============================================================
        // SUMMARY
        // ============================================================
        String summary = String.format(
            "Code Analysis Summary\n" +
            "────────────────────\n" +
            "Language: %s\n" +
            "Lines: %d\n" +
            "Characters: %d\n" +
            "Issues: %d\n" +
            "Security Issues: %d\n" +
            "Quality Score: %d/100\n" +
            "────────────────────",
            language,
            code.split("\n").length,
            code.length(),
            issues.size(),
            securityIssues.size(),
            score
        );
        response.put("summary", summary);
        
        return response;
    }
}