package model;

import org.junit.Test;
import static org.junit.Assert.*;

public class FeeTypeTest {
    
    @Test
    public void testEnumValues() {
        FeeType[] types = FeeType.values();
        assertNotNull(types);
        assertEquals(6, types.length);
    }
    
    @Test
    public void testRoomFeeExists() {
        FeeType roomFee = FeeType.valueOf("ROOM_FEE");
        assertNotNull(roomFee);
        assertEquals(FeeType.ROOM_FEE, roomFee);
        assertEquals("Room Fee", roomFee.getDisplayName());
    }
    
    @Test
    public void testElectricityFeeExists() {
        FeeType electricityFee = FeeType.valueOf("ELECTRICITY");
        assertNotNull(electricityFee);
        assertEquals(FeeType.ELECTRICITY, electricityFee);
        assertEquals("Electricity", electricityFee.getDisplayName());
    }
    
    @Test
    public void testWaterFeeExists() {
        FeeType waterFee = FeeType.valueOf("WATER");
        assertNotNull(waterFee);
        assertEquals(FeeType.WATER, waterFee);
        assertEquals("Water", waterFee.getDisplayName());
    }
    
    @Test
    public void testCleaningFeeExists() {
        FeeType cleaningFee = FeeType.valueOf("CLEANING");
        assertNotNull(cleaningFee);
        assertEquals(FeeType.CLEANING, cleaningFee);
        assertEquals("Cleaning", cleaningFee.getDisplayName());
    }
    
    @Test
    public void testInternetFeeExists() {
        FeeType internetFee = FeeType.valueOf("INTERNET");
        assertNotNull(internetFee);
        assertEquals(FeeType.INTERNET, internetFee);
        assertEquals("Internet", internetFee.getDisplayName());
    }
    
    @Test
    public void testMaintenanceFeeExists() {
        FeeType maintenanceFee = FeeType.valueOf("MAINTENANCE");
        assertNotNull(maintenanceFee);
        assertEquals(FeeType.MAINTENANCE, maintenanceFee);
        assertEquals("Maintenance", maintenanceFee.getDisplayName());
    }
    
    @Test(expected = IllegalArgumentException.class)
    public void testInvalidFeeType() {
        FeeType.valueOf("INVALID_FEE");
    }
} 