package model;

import java.math.BigDecimal;

public class Room {
    private int roomId;
    private String roomNumber;
    private int bedCount;
    private BigDecimal roomPrice;
    private String status;
    private int currentOccupancy;

    public Room(String roomNumber, int bedCount, BigDecimal roomPrice) {
        if (bedCount != 4 && bedCount != 8) {
            throw new IllegalArgumentException("Room must be either 4-person or 8-person");
        }
        this.roomNumber = roomNumber;
        this.bedCount = bedCount;
        this.roomPrice = roomPrice;
        this.status = "AVAILABLE";
        this.currentOccupancy = 0;
    }
    
    // Getters and Setters
    public int getRoomId() { return roomId; }
    public void setRoomId(int roomId) { this.roomId = roomId; }
    
    public String getRoomNumber() { return roomNumber; }
    public void setRoomNumber(String roomNumber) { this.roomNumber = roomNumber; }
    
    public int getBedCount() { return bedCount; }
    
    public BigDecimal getRoomPrice() { return roomPrice; }
    public void setRoomPrice(BigDecimal roomPrice) { this.roomPrice = roomPrice; }
    
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    
    public int getCurrentOccupancy() {
        return currentOccupancy;
    }
    
    public void setCurrentOccupancy(int occupancy) {
        this.currentOccupancy = occupancy;
        updateStatus();
    }
    
    public void incrementOccupancy() {
        if (currentOccupancy < bedCount) {
            currentOccupancy++;
            updateStatus();
        }
    }
    
    public void decrementOccupancy() {
        if (currentOccupancy > 0) {
            currentOccupancy--;
            updateStatus();
        }
    }
    
    private void updateStatus() {
        if (currentOccupancy == 0) {
            status = "AVAILABLE";
        } else if (currentOccupancy == bedCount) {
            status = "FULL";
        } else {
            status = "OCCUPIED";
        }
    }
    
    public boolean hasAvailableBeds() {
        return currentOccupancy < bedCount;
    }
    
    public int getAvailableBeds() {
        return bedCount - currentOccupancy;
    }
    
    public String getRoomType() {
        return bedCount + "-Person";
    }
    
    public int getCapacity() {
        return bedCount;
    }
    
    public BigDecimal getMonthlyFee() {
        return roomPrice;
    }
    
    @Override
    public String toString() {
        return "Room " + roomNumber + " (" + bedCount + " beds)";
    }

    public BigDecimal getTotalPrice() {
        return roomPrice;
    }

    public BigDecimal getAdditionalFee() {
        return BigDecimal.ZERO;
    }
}
