package model;

import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;
import java.math.BigDecimal;
import java.time.LocalDate;

public class FeeTest {
    private Fee fee;
    private final LocalDate dueDate = LocalDate.of(2024, 2, 15);
    private final BigDecimal amount = new BigDecimal("120.00");

    @Before
    public void setUp() {
        fee = new Fee(
            "FEE2024001",
            1, // studentId
            FeeType.ROOM_FEE,
            amount,
            dueDate
        );
    }

    @Test
    public void testFeeCreation() {
        assertNotNull(fee);
        assertEquals("FEE2024001", fee.getFeeCode());
        assertEquals(1, fee.getStudentId());
        assertEquals(FeeType.ROOM_FEE, fee.getFeeType());
        assertEquals(amount, fee.getAmount());
        assertEquals(dueDate, fee.getDueDate());
        assertEquals("CASH", fee.getPaymentMethod());
        assertEquals("PENDING", fee.getPaymentStatus());
    }

    @Test
    public void testSetFeeId() {
        fee.setFeeId(1);
        assertEquals(1, fee.getFeeId());
    }

    @Test
    public void testUpdatePaymentDetails() {
        LocalDate paymentDate = LocalDate.of(2024, 2, 10);
        fee.setPaymentMethod("BANK_TRANSFER");
        fee.setPaymentStatus("PAID");
        fee.setPaymentDate(paymentDate);
        
        assertEquals("BANK_TRANSFER", fee.getPaymentMethod());
        assertEquals("PAID", fee.getPaymentStatus());
        assertEquals(paymentDate, fee.getPaymentDate());
    }

    @Test
    public void testUpdateAmount() {
        BigDecimal newAmount = new BigDecimal("150.00");
        fee.setAmount(newAmount);
        assertEquals(newAmount, fee.getAmount());
    }

    @Test
    public void testSetDescription() {
        String description = "Room fee for February 2024";
        fee.setDescription(description);
        assertEquals(description, fee.getDescription());
    }

    @Test
    public void testGetType() {
        assertEquals("Room Fee", fee.getType());
        
        fee.setFeeType(FeeType.ELECTRICITY);
        assertEquals("Electricity", fee.getType());
    }

    @Test
    public void testGetStatus() {
        assertEquals("PENDING", fee.getStatus());
        
        fee.setPaymentStatus("PAID");
        assertEquals("PAID", fee.getStatus());
    }

    @Test
    public void testToString() {
        String expected = String.format("%s - %s: $%.2f", "FEE2024001", "Room Fee", 120.00);
        assertEquals(expected, fee.toString());
    }

    @Test
    public void testDifferentFeeTypes() {
        for (FeeType type : FeeType.values()) {
            fee.setFeeType(type);
            assertEquals(type.getDisplayName(), fee.getType());
        }
    }
} 