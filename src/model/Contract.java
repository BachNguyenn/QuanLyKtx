package model;

import java.math.BigDecimal;
import java.time.LocalDate;

public class Contract {
    private int contractId;
    private String contractCode;
    private int studentId;
    private int roomId;
    private LocalDate startDate;
    private LocalDate endDate;
    private BigDecimal roomPrice;
    private String paymentMethod;
    private String contractStatus;
    private BigDecimal depositAmount;


    public Contract(String contractCode, int studentId, int roomId,
                    LocalDate startDate, LocalDate endDate, BigDecimal roomPrice) {
        this.contractCode = contractCode;
        this.studentId = studentId;
        this.roomId = roomId;
        this.startDate = startDate;
        this.endDate = endDate;
        this.roomPrice = roomPrice;
        this.paymentMethod = "MONTHLY";
        this.contractStatus = "ACTIVE";
        this.depositAmount = BigDecimal.ZERO;
    }
    
    // Getters and Setters
    public int getContractId() { return contractId; }
    public void setContractId(int contractId) { this.contractId = contractId; }
    
    public String getContractCode() { return contractCode; }
    public void setContractCode(String contractCode) { this.contractCode = contractCode; }
    
    public int getStudentId() { return studentId; }
    public void setStudentId(int studentId) { this.studentId = studentId; }
    
    public int getRoomId() { return roomId; }
    public void setRoomId(int roomId) { this.roomId = roomId; }
    
    public LocalDate getStartDate() { return startDate; }
    public void setStartDate(LocalDate startDate) { this.startDate = startDate; }
    
    public LocalDate getEndDate() { return endDate; }
    public void setEndDate(LocalDate endDate) { this.endDate = endDate; }
    
    public BigDecimal getRoomPrice() { return roomPrice; }
    public void setRoomPrice(BigDecimal roomPrice) { this.roomPrice = roomPrice; }
    
    public String getPaymentMethod() { return paymentMethod; }
    public void setPaymentMethod(String paymentMethod) { this.paymentMethod = paymentMethod; }
    
    public String getContractStatus() { return contractStatus; }
    public void setContractStatus(String contractStatus) { this.contractStatus = contractStatus; }
    
    public BigDecimal getDepositAmount() { return depositAmount; }
    public void setDepositAmount(BigDecimal depositAmount) { this.depositAmount = depositAmount; }
    
    public BigDecimal getMonthlyFee() {
        return roomPrice;
    }

    public void setMonthlyFee(BigDecimal fee) {
        this.roomPrice = fee;
    }

    public String getStatus() {
        return contractStatus;
    }

    public void setStatus(String status) {
        this.contractStatus = status;
    }
    
    @Override
    public String toString() {
        return String.format("%s (%s to %s)", contractCode, startDate, endDate);
    }
}

