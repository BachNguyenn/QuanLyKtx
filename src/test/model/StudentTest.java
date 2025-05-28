package test.model;

import model.Student;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;
import java.time.LocalDate;

public class StudentTest {
    private Student student;

    @Before
    public void setUp() {
        student = new Student(
            "ST001",
            "John Doe",
            LocalDate.of(2000, 1, 1),
            "Male",
            "0123456789",
            "john.doe@example.com",
            "New York"
        );
    }

    @Test
    public void testStudentCreation() {
        assertNotNull(student);
        assertEquals("ST001", student.getStudentCode());
        assertEquals("John Doe", student.getFullName());
        assertEquals(LocalDate.of(2000, 1, 1), student.getDateOfBirth());
        assertEquals("Male", student.getGender());
        assertEquals("0123456789", student.getPhoneNumber());
        assertEquals("john.doe@example.com", student.getGmail());
        assertEquals("New York", student.getHometown());
        assertEquals("ACTIVE", student.getStatus()); // Default status should be ACTIVE
        assertEquals(0, student.getRoomId()); // Default roomId should be 0
    }

    @Test
    public void testSetStatus() {
        student.setStatus("INACTIVE");
        assertEquals("INACTIVE", student.getStatus());
    }

    @Test
    public void testSetRoomId() {
        student.setRoomId(101);
        assertEquals(101, student.getRoomId());
    }

    @Test
    public void testUpdateInformation() {
        student.setFullName("Jane Doe");
        student.setPhoneNumber("9876543210");
        student.setGmail("jane.doe@example.com");
        student.setHometown("Los Angeles");

        assertEquals("Jane Doe", student.getFullName());
        assertEquals("9876543210", student.getPhoneNumber());
        assertEquals("jane.doe@example.com", student.getGmail());
        assertEquals("Los Angeles", student.getHometown());
    }
} 