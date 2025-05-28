package model;

import java.time.LocalDate;

public class Student {
    private int studentId;
    private String studentCode;
    private String fullName;
    private LocalDate dateOfBirth;
    private String gender;
    private String phoneNumber;
    private String Gmail;
    private String hometown;
    private int roomId;
    private String status;

    public Student(String studentCode, String fullName, LocalDate dateOfBirth,
                   String gender, String phoneNumber, String Gmail, String hometown) {
        this.studentCode = studentCode;
        this.fullName = fullName;
        this.dateOfBirth = dateOfBirth;
        this.gender = gender;
        this.phoneNumber = phoneNumber;
        this.Gmail = Gmail;
        this.hometown = hometown;
        this.status = "ACTIVE";
    }
    
    // Getters and Setters
    public int getStudentId() { return studentId; }
    public void setStudentId(int studentId) { this.studentId = studentId; }
    
    public String getStudentCode() { return studentCode; }
    public void setStudentCode(String studentCode) { this.studentCode = studentCode; }
    
    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }
    
    public LocalDate getDateOfBirth() { return dateOfBirth; }
    public void setDateOfBirth(LocalDate dateOfBirth) { this.dateOfBirth = dateOfBirth; }
    
    public String getGender() { return gender; }
    public void setGender(String gender) { this.gender = gender; }
    
    public String getPhoneNumber() { return phoneNumber; }
    public void setPhoneNumber(String phoneNumber) { this.phoneNumber = phoneNumber; }
    
    public String getGmail() { return Gmail; }
    public void setGmail(String Gmail) { this.Gmail = Gmail; }
    
    public String getHometown() { return hometown; }
    public void setHometown(String hometown) { this.hometown = hometown; }
    
    public int getRoomId() { return roomId; }
    public void setRoomId(int roomId) { this.roomId = roomId; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    
    @Override
    public String toString() {
        return String.format("%s - %s", studentCode, fullName);
    }

}
