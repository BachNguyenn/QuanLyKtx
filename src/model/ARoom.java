package model;

import java.math.BigDecimal;

public class ARoom extends Room {

    public ARoom(String roomNumber, int bedCount, BigDecimal roomPrice, BigDecimal additionalFee) {
        super(roomNumber, "A", bedCount, roomPrice);
        this.additionalFee = additionalFee;
    }
    
    @Override
    public BigDecimal getTotalPrice() {
        return super.getTotalPrice().multiply(BigDecimal.valueOf(1.1)); // 10% premium for A rooms
    }
}