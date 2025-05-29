package model;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.HashMap;

public class Statistics {
    private LocalDateTime periodStart;
    private LocalDateTime periodEnd;
    private int totalStudents;
    private int totalRooms;
    private int occupiedRooms;
    private double occupancyRate;
    private Map<String, Integer> studentsByStatus;
    private Map<String, Double> financialSummary;
    
    public Statistics() {
        this.studentsByStatus = new HashMap<>();
        this.financialSummary = new HashMap<>();
    }
    
    // Getters and Setters
    public LocalDateTime getPeriodStart() { return periodStart; }
    public void setPeriodStart(LocalDateTime periodStart) { this.periodStart = periodStart; }
    
    public LocalDateTime getPeriodEnd() { return periodEnd; }
    public void setPeriodEnd(LocalDateTime periodEnd) { this.periodEnd = periodEnd; }
    
    public int getTotalStudents() { return totalStudents; }
    public void setTotalStudents(int totalStudents) { this.totalStudents = totalStudents; }
    
    public int getTotalRooms() { return totalRooms; }
    public void setTotalRooms(int totalRooms) { this.totalRooms = totalRooms; }
    
    public int getOccupiedRooms() { return occupiedRooms; }
    public void setOccupiedRooms(int occupiedRooms) { this.occupiedRooms = occupiedRooms; }
    
    public double getOccupancyRate() { return occupancyRate; }
    public void setOccupancyRate(double occupancyRate) { this.occupancyRate = occupancyRate; }
    
    public Map<String, Integer> getStudentsByStatus() { return studentsByStatus; }
    public void setStudentsByStatus(Map<String, Integer> studentsByStatus) { 
        this.studentsByStatus = studentsByStatus; 
    }
    
    public Map<String, Double> getFinancialSummary() { return financialSummary; }
    public void setFinancialSummary(Map<String, Double> financialSummary) { 
        this.financialSummary = financialSummary; 
    }
    
    public void calculateOccupancyRate() {
        if (totalRooms > 0) {
            this.occupancyRate = (double) occupiedRooms / totalRooms * 100;
        }
    }
    
    public void addStudentStatus(String status, int count) {
        studentsByStatus.put(status, count);
    }
    
    public void addFinancialData(String category, double amount) {
        financialSummary.put(category, amount);
    }
} 