package com.codemirror;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.*;
import org.springframework.context.annotation.Bean;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.http.*;
import java.util.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;
import java.util.stream.Collectors;

@SpringBootApplication
@RestController
@CrossOrigin(origins = "*")
public class App {

    // ============================================================
    // IN-MEMORY STORAGE FOR HISTORY
    // ============================================================
    private final List<AnalysisHistory> analysisHistory = new ArrayList<>();
    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public static void main(String[] args) {
        SpringApplication.run(App.class, args);
        System.out.println("""
            ╔═══════════════════════════════════════════════════════════╗
            ║  🚀 CodeMirror-X Started Successfully!                  ║
            ║  📝 Enterprise Code Review Assistant                    ║
            ║  🌐 http://localhost:8080                               ║
            ║  📊 Multi-Language Support | History | Export           ║
            ╚═══════════════════════════════════════════════════════════╝
        """);
    }

    // ============================================================
    // CORS CONFIGURATION
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
        return "✅ CodeMirror-X is running!";
    }

    // ============================================================
    // CODE ANALYSIS ENDPOINT
    // ============================================================
    @PostMapping("/api/analyze")
    public Map<String, Object> analyzeCode(@RequestBody Map<String, String> request) {
        String code = request.get("code");
        String language = request.getOrDefault("language", "java");
        String sessionId = request.getOrDefault("sessionId", UUID.randomUUID().toString());
        
        Map<String, Object> response = new HashMap<>();
        
        // Basic Metrics
        response.put("language", language);
        response.put("lines", code.split("\n").length);
        response.put("characters", code.length());
        response.put("sessionId", sessionId);
        response.put("timestamp", LocalDateTime.now().format(formatter));
        
        // Bug Detection
        List<String> issues = new ArrayList<>();
        List<String> securityIssues = new ArrayList<>();
        List<String> suggestions = new ArrayList<>();
        
        detectCommonIssues(code, language, issues, securityIssues, suggestions);
        detectLanguageSpecificIssues(code, language, issues, securityIssues, suggestions);
        
        // Scores
        int qualityScore = calculateQualityScore(issues, securityIssues);
        int securityScore = calculateSecurityScore(securityIssues);
        String securityLevel = securityScore >= 80 ? "HIGH" : 
                               securityScore >= 50 ? "MEDIUM" : "LOW";
        
        response.put("issues", issues);
        response.put("securityIssues", securityIssues);
        response.put("suggestions", suggestions);
        response.put("qualityScore", qualityScore);
        response.put("securityScore", securityScore);
        response.put("securityLevel", securityLevel);
        
        setStatus(response, issues, securityIssues);
        response.put("summary", generateSummary(language, code, issues, securityIssues, qualityScore, securityScore));
        
        saveToHistory(sessionId, language, code, response);
        
        return response;
    }

    // ============================================================
    // COMMON ISSUES DETECTION
    // ============================================================
    private void detectCommonIssues(String code, String language, 
                                    List<String> issues, 
                                    List<String> securityIssues, 
                                    List<String> suggestions) {
        // Division by zero
        if (code.contains("/ 0") || code.contains("/0") || code.contains(" /0")) {
            issues.add("⚠️ Division by zero detected!");
            suggestions.add("Add a condition to check if denominator is not zero.");
        }
        
        // Hardcoded passwords
        if (code.toLowerCase().contains("password") && (code.contains("=") || code.contains("=="))) {
            securityIssues.add("🔴 Hardcoded password detected!");
            suggestions.add("Use environment variables or a secure vault.");
        }
        
        // Hardcoded secrets/API keys
        if (code.toLowerCase().contains("secret") || 
            code.toLowerCase().contains("apikey") || 
            code.toLowerCase().contains("api_key")) {
            securityIssues.add("🔴 Hardcoded secret/API key detected!");
            suggestions.add("Store secrets in environment variables.");
        }
        
        // Infinite loops
        if (code.contains("while (true)") || code.contains("while(true)")) {
            issues.add("🔴 Infinite loop detected!");
            suggestions.add("Add a break condition or timeout mechanism.");
        }
        
        // SQL Injection
        if (code.toUpperCase().contains("SELECT") && code.contains("+")) {
            securityIssues.add("🔴 SQL Injection risk detected!");
            suggestions.add("Use PreparedStatement with parameterized queries.");
        }
        
        // Empty catch blocks
        if (code.contains("catch") && code.contains("{}")) {
            issues.add("⚠️ Empty catch block found!");
            suggestions.add("Log the exception or handle it properly.");
        }
        
        // Print statements
        if (code.contains("System.out.println") || 
            code.contains("print(") || 
            code.contains("console.log(")) {
            issues.add("💡 Print statement used in production code.");
            suggestions.add("Use a proper logging framework.");
        }
        
        // printStackTrace
        if (code.contains("printStackTrace")) {
            issues.add("⚠️ printStackTrace() used!");
            suggestions.add("Use a logging framework to log exceptions.");
        }
        
        // Magic numbers
        if (code.matches(".*[^\\w]\\d{3,}.*") && !code.contains("//") && !code.contains("/*")) {
            issues.add("💡 Magic number detected.");
            suggestions.add("Define constants with meaningful names.");
        }
        
        // Hardcoded URLs
        if ((code.contains("http://") || code.contains("https://")) && 
            !code.contains("properties") && !code.contains("config")) {
            issues.add("💡 Hardcoded URL detected.");
            suggestions.add("Move URLs to configuration files.");
        }
        
        // TODO/FIXME comments
        if (code.contains("TODO") || code.contains("FIXME")) {
            issues.add("💡 TODO/FIXME comment found.");
            suggestions.add("Complete TODO items before deployment.");
        }
        
        // Too many parameters
        if (code.matches(".*\\w+\\s*\\([^)]*\\).*")) {
            String[] parts = code.split(",");
            int paramCount = parts.length - 1;
            if (paramCount > 5) {
                issues.add("⚠️ Method with " + paramCount + " parameters detected!");
                suggestions.add("Use a DTO or builder pattern.");
            }
        }
        
        // Null checks
        if (code.contains(".equals(") && !code.contains("null")) {
            issues.add("💡 Potential NullPointerException risk.");
            suggestions.add("Add null checks before calling methods on objects.");
        }
        
        // Thread.sleep in loops
        if (code.contains("Thread.sleep") && (code.contains("while") || code.contains("for"))) {
            issues.add("⚠️ Thread.sleep() in loop detected!");
            suggestions.add("Consider using a scheduled executor service.");
        }
        
        // Large methods
        if (code.split("\n").length > 50) {
            issues.add("⚠️ Large method detected (>50 lines).");
            suggestions.add("Break down large methods into smaller, focused functions.");
        }
    }

    // ============================================================
    // LANGUAGE-SPECIFIC DETECTION
    // ============================================================
    private void detectLanguageSpecificIssues(String code, String language,
                                              List<String> issues,
                                              List<String> securityIssues,
                                              List<String> suggestions) {
        switch (language.toLowerCase()) {
            case "python":
                if (code.contains("print(") && !code.contains("logging")) {
                    issues.add("💡 print() used instead of logging.");
                    suggestions.add("Use Python's logging module for production.");
                }
                if (code.contains("import ") && code.contains("*")) {
                    issues.add("⚠️ Wildcard import detected (import *).");
                    suggestions.add("Import only specific functions/classes.");
                }
                if (code.contains("except:") && !code.contains("except Exception")) {
                    issues.add("⚠️ Bare except clause detected.");
                    suggestions.add("Specify exception type to catch.");
                }
                break;
                
            case "javascript":
            case "typescript":
                if (code.contains("==") && !code.contains("===")) {
                    issues.add("⚠️ '==' used instead of '==='.");
                    suggestions.add("Use '===' for strict equality checking.");
                }
                if (code.contains("var ")) {
                    issues.add("💡 'var' used. Consider 'let' or 'const'.");
                    suggestions.add("Use 'let' for mutable, 'const' for constants.");
                }
                if (code.contains("console.log")) {
                    issues.add("💡 console.log used in production.");
                    suggestions.add("Remove console.log statements in production.");
                }
                if (code.contains("any") && !code.contains(": any")) {
                    issues.add("⚠️ 'any' type used in TypeScript.");
                    suggestions.add("Use a specific type instead of 'any'.");
                }
                break;
                
            case "c":
            case "cpp":
                if (code.contains("malloc(") && !code.contains("free(")) {
                    issues.add("⚠️ Potential memory leak: malloc without free.");
                    suggestions.add("Always free allocated memory.");
                }
                if (code.contains("scanf(") && !code.contains("fgets(")) {
                    issues.add("⚠️ scanf() detected. Buffer overflow risk.");
                    suggestions.add("Use fgets() instead of scanf().");
                }
                break;
                
            case "csharp":
                if (code.contains("Console.WriteLine") && !code.contains("ILogger")) {
                    issues.add("💡 Console.WriteLine used in production.");
                    suggestions.add("Use ILogger for logging in C#.");
                }
                break;
                
            case "go":
                if (code.contains("panic(") && !code.contains("recover()")) {
                    issues.add("⚠️ panic() detected without recover.");
                    suggestions.add("Use recover() to handle panics gracefully.");
                }
                break;
                
            case "rust":
                if (code.contains("unwrap(") && !code.contains("expect(")) {
                    issues.add("⚠️ unwrap() used. This can panic.");
                    suggestions.add("Use expect() with a message or handle Result properly.");
                }
                if (code.contains("unsafe {")) {
                    securityIssues.add("🔴 Unsafe block used in Rust.");
                    suggestions.add("Minimize use of unsafe blocks. Document why it's needed.");
                }
                break;
                
            case "php":
                if (code.contains("mysql_query(") || code.contains("mysqli_query(")) {
                    securityIssues.add("🔴 Direct database query detected. SQL Injection risk.");
                    suggestions.add("Use PDO with prepared statements.");
                }
                break;
        }
    }

    // ============================================================
    // SCORE CALCULATIONS
    // ============================================================
    private int calculateQualityScore(List<String> issues, List<String> securityIssues) {
        int score = 100;
        score -= issues.size() * 5;
        score -= securityIssues.size() * 15;
        if (issues.isEmpty() && securityIssues.isEmpty()) {
            score = Math.min(score + 5, 100);
        }
        return Math.max(0, Math.min(100, score));
    }

    private int calculateSecurityScore(List<String> securityIssues) {
        int score = 100;
        score -= securityIssues.size() * 20;
        return Math.max(0, Math.min(100, score));
    }

    // ============================================================
    // STATUS & SUMMARY
    // ============================================================
    private void setStatus(Map<String, Object> response, 
                           List<String> issues, 
                           List<String> securityIssues) {
        if (issues.isEmpty() && securityIssues.isEmpty()) {
            response.put("status", "✅ Code looks clean!");
            response.put("message", "No issues found. Great job! 🎉");
        } else if (!securityIssues.isEmpty()) {
            response.put("status", "🔴 CRITICAL: Security issues found!");
            response.put("message", "Fix security issues immediately before deploying!");
        } else if (issues.size() > 3) {
            response.put("status", "⚠️ Multiple issues found!");
            response.put("message", "Review all issues and fix them for better code quality.");
        } else {
            response.put("status", "⚠️ Issues found!");
            response.put("message", "Review the issues above and fix them.");
        }
    }

    private String generateSummary(String language, String code, 
                                   List<String> issues, 
                                   List<String> securityIssues,
                                   int qualityScore, int securityScore) {
        return String.format(
            "📊 Code Analysis Summary\n" +
            "────────────────────────\n" +
            "Language: %s\n" +
            "Lines: %d\n" +
            "Characters: %d\n" +
            "Issues: %d\n" +
            "Security Issues: %d\n" +
            "Quality Score: %d/100\n" +
            "Security Score: %d/100\n" +
            "Security Level: %s\n" +
            "────────────────────────",
            language,
            code.split("\n").length,
            code.length(),
            issues.size(),
            securityIssues.size(),
            qualityScore,
            securityScore,
            securityScore >= 80 ? "HIGH" : securityScore >= 50 ? "MEDIUM" : "LOW"
        );
    }

    // ============================================================
    // HISTORY
    // ============================================================
    private void saveToHistory(String sessionId, String language, String code, 
                               Map<String, Object> response) {
        AnalysisHistory record = new AnalysisHistory();
        record.setId(UUID.randomUUID().toString());
        record.setSessionId(sessionId);
        record.setLanguage(language);
        record.setCode(code);
        record.setTimestamp(LocalDateTime.now().format(formatter));
        record.setQualityScore((int) response.get("qualityScore"));
        record.setSecurityScore((int) response.get("securityScore"));
        record.setIssues((List<String>) response.get("issues"));
        record.setSecurityIssues((List<String>) response.get("securityIssues"));
        record.setSuggestions((List<String>) response.get("suggestions"));
        record.setStatus((String) response.get("status"));
        
        analysisHistory.add(record);
        if (analysisHistory.size() > 100) {
            analysisHistory.remove(0);
        }
    }

    @GetMapping("/api/history")
    public ResponseEntity<List<Map<String, Object>>> getHistory() {
        List<Map<String, Object>> history = analysisHistory.stream()
            .map(record -> {
                Map<String, Object> item = new HashMap<>();
                item.put("id", record.getId());
                item.put("timestamp", record.getTimestamp());
                item.put("language", record.getLanguage());
                item.put("qualityScore", record.getQualityScore());
                item.put("securityScore", record.getSecurityScore());
                item.put("issues", record.getIssues());
                item.put("securityIssues", record.getSecurityIssues());
                item.put("suggestions", record.getSuggestions());
                item.put("status", record.getStatus());
                String truncatedCode = record.getCode();
                if (truncatedCode.length() > 200) {
                    truncatedCode = truncatedCode.substring(0, 200) + "...";
                }
                item.put("codePreview", truncatedCode);
                return item;
            })
            .collect(Collectors.toList());
        
        Collections.reverse(history);
        return ResponseEntity.ok(history);
    }

    @GetMapping("/api/export/{id}")
    public ResponseEntity<String> exportReport(@PathVariable String id) {
        Optional<AnalysisHistory> record = analysisHistory.stream()
            .filter(r -> r.getId().equals(id))
            .findFirst();
        
        if (record.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        
        AnalysisHistory r = record.get();
        String report = generateJsonReport(r);
        
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setContentDispositionFormData("attachment", 
            "codemirror-report-" + id + ".json");
        
        return new ResponseEntity<>(report, headers, HttpStatus.OK);
    }

    private String generateJsonReport(AnalysisHistory record) {
        return String.format("""
            {
              "report": {
                "id": "%s",
                "timestamp": "%s",
                "language": "%s",
                "qualityScore": %d,
                "securityScore": %d,
                "status": "%s",
                "issues": %s,
                "securityIssues": %s,
                "suggestions": %s,
                "codePreview": "%s"
              }
            }
            """,
            record.getId(),
            record.getTimestamp(),
            record.getLanguage(),
            record.getQualityScore(),
            record.getSecurityScore(),
            record.getStatus(),
            formatList(record.getIssues()),
            formatList(record.getSecurityIssues()),
            formatList(record.getSuggestions()),
            record.getCode().replace("\"", "\\\"").substring(0, Math.min(record.getCode().length(), 500))
        );
    }

    private String formatList(List<String> list) {
        if (list == null || list.isEmpty()) {
            return "[]";
        }
        return "[\"" + String.join("\", \"", list) + "\"]";
    }

    // ============================================================
    // INNER CLASS: AnalysisHistory
    // ============================================================
    public static class AnalysisHistory {
        private String id;
        private String sessionId;
        private String language;
        private String code;
        private String timestamp;
        private int qualityScore;
        private int securityScore;
        private List<String> issues;
        private List<String> securityIssues;
        private List<String> suggestions;
        private String status;

        public String getId() { return id; }
        public void setId(String id) { this.id = id; }
        public String getSessionId() { return sessionId; }
        public void setSessionId(String sessionId) { this.sessionId = sessionId; }
        public String getLanguage() { return language; }
        public void setLanguage(String language) { this.language = language; }
        public String getCode() { return code; }
        public void setCode(String code) { this.code = code; }
        public String getTimestamp() { return timestamp; }
        public void setTimestamp(String timestamp) { this.timestamp = timestamp; }
        public int getQualityScore() { return qualityScore; }
        public void setQualityScore(int qualityScore) { this.qualityScore = qualityScore; }
        public int getSecurityScore() { return securityScore; }
        public void setSecurityScore(int securityScore) { this.securityScore = securityScore; }
        public List<String> getIssues() { return issues; }
        public void setIssues(List<String> issues) { this.issues = issues; }
        public List<String> getSecurityIssues() { return securityIssues; }
        public void setSecurityIssues(List<String> securityIssues) { this.securityIssues = securityIssues; }
        public List<String> getSuggestions() { return suggestions; }
        public void setSuggestions(List<String> suggestions) { this.suggestions = suggestions; }
        public String getStatus() { return status; }
        public void setStatus(String status) { this.status = status; }
    }
}