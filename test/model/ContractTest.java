package model;

import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;
import java.math.BigDecimal;
import java.time.LocalDate;

public class ContractTest {
    private Contract contract;
    private final LocalDate startDate = LocalDate.of(2024, 1, 1);
    private final LocalDate endDate = LocalDate.of(2024, 12, 31);
    private final BigDecimal roomPrice = new BigDecimal("120.00");

    @Before
    public void setUp() {
        contract = new Contract(
            "HD2024001",
            1, // studentId
            101, // roomId
            startDate,
            endDate,
            roomPrice
        );
    }

    @Test
    public void testContractCreation() {
        assertNotNull(contract);
        assertEquals("HD2024001", contract.getContractCode());
        assertEquals(1, contract.getStudentId());
        assertEquals(101, contract.getRoomId());
        assertEquals(startDate, contract.getStartDate());
        assertEquals(endDate, contract.getEndDate());
        assertEquals(roomPrice, contract.getRoomPrice());
        assertEquals("MONTHLY", contract.getPaymentMethod());
        assertEquals("ACTIVE", contract.getContractStatus());
        assertEquals(BigDecimal.ZERO, contract.getDepositAmount());
    }

    @Test
    public void testSetContractId() {
        contract.setContractId(1);
        assertEquals(1, contract.getContractId());
    }

    @Test
    public void testUpdateDates() {
        LocalDate newStartDate = LocalDate.of(2024, 2, 1);
        LocalDate newEndDate = LocalDate.of(2025, 1, 31);
        
        contract.setStartDate(newStartDate);
        contract.setEndDate(newEndDate);
        
        assertEquals(newStartDate, contract.getStartDate());
        assertEquals(newEndDate, contract.getEndDate());
    }

    @Test
    public void testUpdatePaymentDetails() {
        BigDecimal newPrice = new BigDecimal("150.00");
        BigDecimal deposit = new BigDecimal("200.00");
        
        contract.setRoomPrice(newPrice);
        contract.setDepositAmount(deposit);
        contract.setPaymentMethod("QUARTERLY");
        
        assertEquals(newPrice, contract.getRoomPrice());
        assertEquals(deposit, contract.getDepositAmount());
        assertEquals("QUARTERLY", contract.getPaymentMethod());
    }

    @Test
    public void testSetStatus() {
        contract.setStatus("TERMINATED");
        assertEquals("TERMINATED", contract.getStatus());
        assertEquals("TERMINATED", contract.getContractStatus());
    }

    @Test
    public void testMonthlyFee() {
        assertEquals(roomPrice, contract.getMonthlyFee());
        
        BigDecimal newFee = new BigDecimal("130.00");
        contract.setMonthlyFee(newFee);
        assertEquals(newFee, contract.getMonthlyFee());
        assertEquals(newFee, contract.getRoomPrice());
    }

    @Test
    public void testToString() {
        String expected = String.format("HD2024001 (%s to %s)", startDate, endDate);
        assertEquals(expected, contract.toString());
    }
} 