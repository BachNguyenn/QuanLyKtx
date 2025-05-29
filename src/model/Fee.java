package model;

import java.math.BigDecimal;
import java.time.LocalDate;

public class Fee {
    private int feeId;
    private String feeCode;
    private int studentId;
    private FeeType feeType;
    private BigDecimal amount;
    private String paymentMethod;
    private String paymentStatus;
    private LocalDate dueDate;
    private LocalDate paymentDate;
    private String description;
    private int contractId;

    
    public Fee(String feeCode, int studentId, FeeType feeType, BigDecimal amount, LocalDate dueDate) {
        this.feeCode = feeCode;
        this.studentId = studentId;
        this.feeType = feeType;
        this.amount = amount;
        this.dueDate = dueDate;
        this.paymentMethod = "CASH";
        this.paymentStatus = "PENDING";
        this.contractId = 0;
    }
    
    // Getters and Setters
    public int getFeeId() { return feeId; }
    public void setFeeId(int feeId) { this.feeId = feeId; }
    
    public String getFeeCode() { return feeCode; }
    public void setFeeCode(String feeCode) { this.feeCode = feeCode; }
    
    public int getStudentId() { return studentId; }
    public void setStudentId(int studentId) { this.studentId = studentId; }
    
    public FeeType getFeeType() { return feeType; }
    public void setFeeType(FeeType feeType) { this.feeType = feeType; }
    
    public BigDecimal getAmount() { return amount; }
    public void setAmount(BigDecimal amount) { this.amount = amount; }
    
    public String getPaymentMethod() { return paymentMethod; }
    public void setPaymentMethod(String paymentMethod) { this.paymentMethod = paymentMethod; }
    
    public String getPaymentStatus() { return paymentStatus; }
    public void setPaymentStatus(String paymentStatus) { this.paymentStatus = paymentStatus; }
    
    public LocalDate getDueDate() { return dueDate; }
    public void setDueDate(LocalDate dueDate) { this.dueDate = dueDate; }
    
    public LocalDate getPaymentDate() { return paymentDate; }
    public void setPaymentDate(LocalDate paymentDate) { this.paymentDate = paymentDate; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    
    public String getType() {
        return feeType.getDisplayName();
    }

    public String getStatus() {
        return paymentStatus;
    }

    public int getContractId() {
        return contractId;
    }

    public void setContractId(int contractId) {
        this.contractId = contractId;
    }
    
    @Override
    public String toString() {
        return String.format("%s - %s: $%.2f", feeCode, feeType.getDisplayName(), amount);
    }
}