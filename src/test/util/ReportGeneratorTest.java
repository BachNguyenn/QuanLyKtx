package test.util;

import model.*;
import org.junit.Before;
import org.junit.Test;
import util.DataStorage;
import util.ReportGenerator;
import static org.junit.Assert.*;
import java.math.BigDecimal;
import java.time.LocalDate;

public class ReportGeneratorTest {
    private DataStorage dataStorage;

    @Before
    public void setUp() {
        dataStorage = DataStorage.getInstance();
        setupTestData();
    }

    private void setupTestData() {
        // Tạo phòng test
        Room room = new Room(
            "TR101",
            "TEST",
            2,
            new BigDecimal("500.00")
        );
        dataStorage.addRoom(room);

        // Tạo sinh viên test
        Student student = new Student(
            "TST001",
            "Test Student",
            LocalDate.now(),
            "Male",
            "0123456789",
            "test@example.com",
            "Test City"
        );
        dataStorage.addStudent(student);

        // Tạo hợp đồng test
        Contract contract = new Contract(
            "TC001",
            student.getStudentId(),
            room.getRoomId(),
            LocalDate.now(),
            LocalDate.now().plusMonths(6),
            new BigDecimal("500.00")
        );
        dataStorage.addContract(contract);

        // Tạo phí test
        Fee fee = new Fee(
            "TF001",
            student.getStudentId(),
            FeeType.ROOM_FEE,
            new BigDecimal("500.00"),
            LocalDate.now().plusDays(30)
        );
        dataStorage.addFee(fee);
    }

    @Test
    public void testGenerateOccupancyReport() {
        String report = ReportGenerator.generateOccupancyReport();
        assertNotNull(report);
        assertTrue(report.contains("ROOM OCCUPANCY REPORT"));
        assertTrue(report.contains("Total Rooms:"));
        assertTrue(report.contains("Total Beds:"));
    }

    @Test
    public void testGenerateFinancialReport() {
        String report = ReportGenerator.generateFinancialReport();
        assertNotNull(report);
        assertTrue(report.contains("FINANCIAL REPORT"));
    }

    @Test
    public void testGenerateStudentReport() {
        String report = ReportGenerator.generateStudentReport();
        assertNotNull(report);
        assertTrue(report.contains("STUDENT STATISTICS REPORT"));
    }

    @Test
    public void testGenerateContractReport() {
        String report = ReportGenerator.generateContractReport();
        assertNotNull(report);
        assertTrue(report.contains("CONTRACT REPORT"));
    }

    @Test
    public void testGenerateSummaryReport() {
        String report = ReportGenerator.generateSummaryReport();
        assertNotNull(report);
        assertTrue(report.contains("SUMMARY REPORT"));
    }
} 