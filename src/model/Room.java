package model;

import java.math.BigDecimal;

public class Room {
    protected int roomId;
    protected String roomNumber;
    protected String roomType;
    protected int bedCount;
    protected BigDecimal roomPrice;
    protected BigDecimal additionalFee;
    protected String status;
    
    // Constructors
    public Room() {}
    
    public Room(String roomNumber, String roomType, int bedCount, BigDecimal roomPrice) {
        this.roomNumber = roomNumber;
        this.roomType = roomType;
        this.bedCount = bedCount;
        this.roomPrice = roomPrice;
        this.additionalFee = BigDecimal.ZERO;
        this.status = "AVAILABLE";
    }
    
    // Getters and Setters
    public int getRoomId() { return roomId; }
    public void setRoomId(int roomId) { this.roomId = roomId; }
    
    public String getRoomNumber() { return roomNumber; }
    public void setRoomNumber(String roomNumber) { this.roomNumber = roomNumber; }
    
    public String getRoomType() { return roomType; }
    public void setRoomType(String roomType) { this.roomType = roomType; }
    
    public int getBedCount() { return bedCount; }
    public void setBedCount(int bedCount) { this.bedCount = bedCount; }
    
    public BigDecimal getRoomPrice() { return roomPrice; }
    public void setRoomPrice(BigDecimal roomPrice) { this.roomPrice = roomPrice; }
    
    public BigDecimal getAdditionalFee() { return additionalFee; }
    public void setAdditionalFee(BigDecimal additionalFee) { this.additionalFee = additionalFee; }
    
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    
    public BigDecimal getTotalPrice() {
        return roomPrice.add(additionalFee);
    }
    
    @Override
    public String toString() {
        return String.format("%s (%s) - %d beds", roomNumber, roomType, bedCount);
    }


}
