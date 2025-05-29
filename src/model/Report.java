package model;

import java.time.LocalDateTime;

public class Report {
    private int id;
    private String title;
    private String description;
    private LocalDateTime generatedDate;
    private String type; // OVERVIEW, FINANCIAL, STUDENT_LIST, etc.
    private String filePath;
    private String format; // PDF, EXCEL
    private String status; // GENERATING, COMPLETED, FAILED
    
    public Report() {
        // Empty constructor
    }
    
    public Report(int id, String title, String description, String type) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.type = type;
        this.generatedDate = LocalDateTime.now();
        this.status = "GENERATING";
    }
    
    // Getters and Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    
    public LocalDateTime getGeneratedDate() { return generatedDate; }
    public void setGeneratedDate(LocalDateTime generatedDate) { this.generatedDate = generatedDate; }
    
    public String getType() { return type; }
    public void setType(String type) { this.type = type; }
    
    public String getFilePath() { return filePath; }
    public void setFilePath(String filePath) { this.filePath = filePath; }
    
    public String getFormat() { return format; }
    public void setFormat(String format) { this.format = format; }
    
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    
    @Override
    public String toString() {
        return "Report{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", type='" + type + '\'' +
                ", status='" + status + '\'' +
                ", generatedDate=" + generatedDate +
                '}';
    }
} 