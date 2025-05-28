package test.model;

import model.Room;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;
import java.math.BigDecimal;

public class RoomTest {
    private Room room;

    @Before
    public void setUp() {
        room = new Room(
            "R101",
            "A",
            4,
            new BigDecimal("1000.00")
        );
    }

    @Test
    public void testRoomCreation() {
        assertNotNull(room);
        assertEquals("R101", room.getRoomNumber());
        assertEquals("A", room.getRoomType());
        assertEquals(4, room.getCapacity());
        assertEquals(new BigDecimal("1000.00"), room.getRoomPrice());
        assertEquals(BigDecimal.ZERO, room.getAdditionalFee());
        assertEquals("AVAILABLE", room.getStatus());
    }

    @Test
    public void testSetStatus() {
        room.setStatus("MAINTENANCE");
        assertEquals("MAINTENANCE", room.getStatus());
    }

    @Test
    public void testUpdatePrices() {
        room.setRoomPrice(new BigDecimal("1200.00"));
        room.setAdditionalFee(new BigDecimal("150.00"));
        
        assertEquals(new BigDecimal("1200.00"), room.getRoomPrice());
        assertEquals(new BigDecimal("150.00"), room.getAdditionalFee());
    }

    @Test
    public void testTotalPrice() {
        BigDecimal expectedTotal = new BigDecimal("1000.00"); // No additional fee by default
        assertEquals(expectedTotal, room.getTotalPrice());
    }
} 