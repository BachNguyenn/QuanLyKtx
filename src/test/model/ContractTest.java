package test.model;

import model.Contract;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;
import java.math.BigDecimal;
import java.time.LocalDate;

public class ContractTest {
    private Contract contract;
    private LocalDate startDate;
    private LocalDate endDate;

    @Before
    public void setUp() {
        startDate = LocalDate.now();
        endDate = startDate.plusMonths(6);
        contract = new Contract(
            "C001",
            1, // studentId
            101, // roomId
            startDate,
            endDate,
            new BigDecimal("1000.00")
        );
    }

    @Test
    public void testContractCreation() {
        assertNotNull(contract);
        assertEquals("C001", contract.getContractCode());
        assertEquals(1, contract.getStudentId());
        assertEquals(101, contract.getRoomId());
        assertEquals(startDate, contract.getStartDate());
        assertEquals(endDate, contract.getEndDate());
        assertEquals(new BigDecimal("1000.00"), contract.getRoomPrice());
        assertEquals("MONTHLY", contract.getPaymentMethod());
        assertEquals(BigDecimal.ZERO, contract.getDepositAmount());
        assertEquals("ACTIVE", contract.getContractStatus());
    }

    @Test
    public void testSetStatus() {
        contract.setContractStatus("ACTIVE");
        assertEquals("ACTIVE", contract.getContractStatus());
    }

    @Test
    public void testUpdateDates() {
        LocalDate newStartDate = startDate.plusDays(1);
        LocalDate newEndDate = endDate.plusDays(1);
        
        contract.setStartDate(newStartDate);
        contract.setEndDate(newEndDate);
        
        assertEquals(newStartDate, contract.getStartDate());
        assertEquals(newEndDate, contract.getEndDate());
    }

    @Test
    public void testUpdatePaymentDetails() {
        contract.setPaymentMethod("SEMESTER");
        contract.setRoomPrice(new BigDecimal("1200.00"));
        contract.setDepositAmount(new BigDecimal("600.00"));
        
        assertEquals("SEMESTER", contract.getPaymentMethod());
        assertEquals(new BigDecimal("1200.00"), contract.getRoomPrice());
        assertEquals(new BigDecimal("600.00"), contract.getDepositAmount());
    }
} 