package model;

import java.math.BigDecimal;


public class BRoom extends Room {

    public BRoom(String roomNumber, int bedCount, BigDecimal roomPrice) {
        super(roomNumber, "B", bedCount, roomPrice);
        this.additionalFee = BigDecimal.ZERO;
    }
}