package model;

import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

public class StatisticsTest {
    private Statistics statistics;
    private final LocalDateTime TEST_START = LocalDateTime.now().minusDays(30);
    private final LocalDateTime TEST_END = LocalDateTime.now();
    private final int TEST_TOTAL_STUDENTS = 100;
    private final int TEST_TOTAL_ROOMS = 25;
    private final int TEST_OCCUPIED_ROOMS = 20;
    private final double TEST_OCCUPANCY_RATE = 80.0;

    @Before
    public void setUp() {
        statistics = new Statistics();
        statistics.setPeriodStart(TEST_START);
        statistics.setPeriodEnd(TEST_END);
        statistics.setTotalStudents(TEST_TOTAL_STUDENTS);
        statistics.setTotalRooms(TEST_TOTAL_ROOMS);
        statistics.setOccupiedRooms(TEST_OCCUPIED_ROOMS);
        statistics.setOccupancyRate(TEST_OCCUPANCY_RATE);
        
        // Set up test status data
        Map<String, Integer> statusData = new HashMap<>();
        statusData.put("Active", 80);
        statusData.put("Inactive", 20);
        statistics.setStudentsByStatus(statusData);
        
        // Set up test financial data
        Map<String, Double> financialData = new HashMap<>();
        financialData.put("Revenue", 50000.0);
        financialData.put("Expenses", 30000.0);
        statistics.setFinancialSummary(financialData);
    }

    @Test
    public void testGetPeriodStart() {
        assertEquals(TEST_START, statistics.getPeriodStart());
    }

    @Test
    public void testGetPeriodEnd() {
        assertEquals(TEST_END, statistics.getPeriodEnd());
    }

    @Test
    public void testGetTotalStudents() {
        assertEquals(TEST_TOTAL_STUDENTS, statistics.getTotalStudents());
    }

    @Test
    public void testGetTotalRooms() {
        assertEquals(TEST_TOTAL_ROOMS, statistics.getTotalRooms());
    }

    @Test
    public void testGetOccupiedRooms() {
        assertEquals(TEST_OCCUPIED_ROOMS, statistics.getOccupiedRooms());
    }

    @Test
    public void testGetOccupancyRate() {
        assertEquals(TEST_OCCUPANCY_RATE, statistics.getOccupancyRate(), 0.01);
    }

    @Test
    public void testGetStudentsByStatus() {
        Map<String, Integer> statusData = statistics.getStudentsByStatus();
        assertEquals(2, statusData.size());
        assertEquals(Integer.valueOf(80), statusData.get("Active"));
        assertEquals(Integer.valueOf(20), statusData.get("Inactive"));
    }

    @Test
    public void testGetFinancialSummary() {
        Map<String, Double> financialData = statistics.getFinancialSummary();
        assertEquals(2, financialData.size());
        assertEquals(Double.valueOf(50000.0), financialData.get("Revenue"));
        assertEquals(Double.valueOf(30000.0), financialData.get("Expenses"));
    }

    @Test
    public void testSetPeriodStart() {
        LocalDateTime newStart = LocalDateTime.now().minusDays(60);
        statistics.setPeriodStart(newStart);
        assertEquals(newStart, statistics.getPeriodStart());
    }

    @Test
    public void testSetPeriodEnd() {
        LocalDateTime newEnd = LocalDateTime.now().plusDays(1);
        statistics.setPeriodEnd(newEnd);
        assertEquals(newEnd, statistics.getPeriodEnd());
    }

    @Test
    public void testSetTotalStudents() {
        int newTotal = 150;
        statistics.setTotalStudents(newTotal);
        assertEquals(newTotal, statistics.getTotalStudents());
    }

    @Test
    public void testSetTotalRooms() {
        int newTotal = 30;
        statistics.setTotalRooms(newTotal);
        assertEquals(newTotal, statistics.getTotalRooms());
    }

    @Test
    public void testSetOccupiedRooms() {
        int newOccupied = 25;
        statistics.setOccupiedRooms(newOccupied);
        assertEquals(newOccupied, statistics.getOccupiedRooms());
    }

    @Test
    public void testSetOccupancyRate() {
        double newRate = 85.5;
        statistics.setOccupancyRate(newRate);
        assertEquals(newRate, statistics.getOccupancyRate(), 0.01);
    }
} 