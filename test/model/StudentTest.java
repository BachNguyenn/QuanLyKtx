package model;

import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;
import java.time.LocalDate;

public class StudentTest {
    private Student student;
    private final LocalDate testDate = LocalDate.of(2003, 5, 15);

    @Before
    public void setUp() {
        student = new Student(
            "N21DCCN123",
            "John Smith",
            testDate,
            "Male",
            "0123456789",
            "john.smith@gmail.com",
            "New York"
        );
    }

    @Test
    public void testStudentCreation() {
        assertNotNull(student);
        assertEquals("N21DCCN123", student.getStudentCode());
        assertEquals("John Smith", student.getFullName());
        assertEquals(testDate, student.getDateOfBirth());
        assertEquals("Male", student.getGender());
        assertEquals("0123456789", student.getPhoneNumber());
        assertEquals("john.smith@gmail.com", student.getEmail());
        assertEquals("New York", student.getHometown());
        assertEquals("ACTIVE", student.getStatus());
    }

    @Test
    public void testSetStudentId() {
        student.setStudentId(1);
        assertEquals(1, student.getStudentId());
    }

    @Test
    public void testSetRoomId() {
        student.setRoomId(101);
        assertEquals(101, student.getRoomId());
    }

    @Test
    public void testUpdateStudentInfo() {
        LocalDate newDate = LocalDate.of(2003, 6, 20);
        student.setFullName("James Wilson");
        student.setDateOfBirth(newDate);
        student.setPhoneNumber("0987654321");
        student.setEmail("james.wilson@gmail.com");
        
        assertEquals("James Wilson", student.getFullName());
        assertEquals(newDate, student.getDateOfBirth());
        assertEquals("0987654321", student.getPhoneNumber());
        assertEquals("james.wilson@gmail.com", student.getEmail());
    }

    @Test
    public void testSetStatus() {
        student.setStatus("INACTIVE");
        assertEquals("INACTIVE", student.getStatus());
    }

    @Test
    public void testToString() {
        assertEquals("N21DCCN123 - John Smith", student.toString());
    }
} 