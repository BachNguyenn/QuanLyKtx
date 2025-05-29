package model;

import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;
import java.math.BigDecimal;

public class RoomTest {
    private Room room4Person;
    private Room room8Person;

    @Before
    public void setUp() {
        room4Person = new Room(
            "P401",
            4,
            new BigDecimal("120.00")
        );
        
        room8Person = new Room(
            "P801",
            8,
            new BigDecimal("80.00")
        );
    }

    @Test
    public void testRoomCreation() {
        assertNotNull(room4Person);
        assertEquals("P401", room4Person.getRoomNumber());
        assertEquals(4, room4Person.getBedCount());
        assertEquals(new BigDecimal("120.00"), room4Person.getRoomPrice());
        assertEquals("AVAILABLE", room4Person.getStatus());

        assertNotNull(room8Person);
        assertEquals("P801", room8Person.getRoomNumber());
        assertEquals(8, room8Person.getBedCount());
        assertEquals(new BigDecimal("80.00"), room8Person.getRoomPrice());
        assertEquals("AVAILABLE", room8Person.getStatus());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testInvalidBedCount() {
        new Room("P601", 6, new BigDecimal("100.00"));
    }

    @Test
    public void testSetStatus() {
        room4Person.setStatus("MAINTENANCE");
        assertEquals("MAINTENANCE", room4Person.getStatus());

        room8Person.setStatus("OCCUPIED");
        assertEquals("OCCUPIED", room8Person.getStatus());
    }

    @Test
    public void testGetMonthlyFee() {
        assertEquals(new BigDecimal("120.00"), room4Person.getMonthlyFee());
        assertEquals(new BigDecimal("80.00"), room8Person.getMonthlyFee());
    }

    @Test
    public void testToString() {
        assertEquals("Room P401 (4 beds)", room4Person.toString());
        assertEquals("Room P801 (8 beds)", room8Person.toString());
    }

    @Test
    public void testRoomType() {
        assertEquals("4-Person", room4Person.getRoomType());
        assertEquals("8-Person", room8Person.getRoomType());
    }
} 