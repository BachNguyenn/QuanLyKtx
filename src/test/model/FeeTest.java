package test.model;

import model.Fee;
import model.FeeType;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;
import java.math.BigDecimal;
import java.time.LocalDate;

public class FeeTest {
    private Fee fee;
    private LocalDate dueDate;

    @Before
    public void setUp() {
        dueDate = LocalDate.now().plusDays(30);
        fee = new Fee(
            "F001",
            1, // studentId
            FeeType.ROOM_FEE,
            new BigDecimal("1000.00"),
            dueDate
        );
        fee.setDescription("Monthly room fee");
    }

    @Test
    public void testFeeCreation() {
        assertNotNull(fee);
        assertEquals("F001", fee.getFeeCode());
        assertEquals(1, fee.getStudentId());
        assertEquals(FeeType.ROOM_FEE, fee.getFeeType());
        assertEquals(new BigDecimal("1000.00"), fee.getAmount());
        assertEquals(dueDate, fee.getDueDate());
        assertEquals("Monthly room fee", fee.getDescription());
        assertEquals("PENDING", fee.getPaymentStatus());
        assertNull(fee.getPaymentDate());
    }

    @Test
    public void testSetPaymentStatus() {
        fee.setPaymentStatus("PAID");
        assertEquals("PAID", fee.getPaymentStatus());
    }

    @Test
    public void testMarkAsPaid() {
        fee.setPaymentStatus("PAID");
        fee.setPaymentDate(LocalDate.now());
        fee.setPaymentMethod("CASH");
        
        assertEquals("PAID", fee.getPaymentStatus());
        assertNotNull(fee.getPaymentDate());
        assertEquals("CASH", fee.getPaymentMethod());
    }

    @Test
    public void testUpdateAmount() {
        fee.setAmount(new BigDecimal("1200.00"));
        assertEquals(new BigDecimal("1200.00"), fee.getAmount());
    }

    @Test
    public void testUpdateDueDate() {
        LocalDate newDueDate = dueDate.plusDays(15);
        fee.setDueDate(newDueDate);
        assertEquals(newDueDate, fee.getDueDate());
    }
} 