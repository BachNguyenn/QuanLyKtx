package util;

import model.*;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;
import java.math.BigDecimal;
import java.time.LocalDate;

public class DataStorageTest {
    private DataStorage dataStorage;
    private Student testStudent;
    private Room testRoom;
    private Contract testContract;
    private Fee testFee;

    @Before
    public void setUp() {
        dataStorage = DataStorage.getInstance();
        
        testStudent = new Student(
            "TST001",
            "Test Student",
            LocalDate.now(),
            "Male",
            "0123456789",
            "test@example.com",
            "Test City"
        );

        testRoom = new Room(
            "TR101",
            4,
            new BigDecimal("120.00")
        );

        testContract = new Contract(
            "TC001",
            1,
            1,
            LocalDate.now(),
            LocalDate.now().plusMonths(6),
            new BigDecimal("120.00")
        );

        testFee = new Fee(
            "TF001",
            1,
            FeeType.ROOM_FEE,
            new BigDecimal("120.00"),
            LocalDate.now().plusDays(30)
        );
    }

    @Test
    public void testAddAndGetStudent() {
        assertTrue(dataStorage.addStudent(testStudent));
        Student retrieved = dataStorage.getStudentById(testStudent.getStudentId());
        assertNotNull(retrieved);
        assertEquals(testStudent.getStudentCode(), retrieved.getStudentCode());
    }

    @Test
    public void testAddAndGetRoom() {
        assertTrue(dataStorage.addRoom(testRoom));
        Room retrieved = dataStorage.getRoomById(testRoom.getRoomId());
        assertNotNull(retrieved);
        assertEquals(testRoom.getRoomNumber(), retrieved.getRoomNumber());
    }

    @Test
    public void testAddAndGetContract() {
        assertTrue(dataStorage.addContract(testContract));
        Contract retrieved = dataStorage.getContractById(testContract.getContractId());
        assertNotNull(retrieved);
        assertEquals(testContract.getContractCode(), retrieved.getContractCode());
    }

    @Test
    public void testAddAndGetFee() {
        assertTrue(dataStorage.addFee(testFee));
        Fee retrieved = dataStorage.getFeeById(testFee.getFeeId());
        assertNotNull(retrieved);
        assertEquals(testFee.getFeeCode(), retrieved.getFeeCode());
    }

    @Test
    public void testUpdateStudent() {
        dataStorage.addStudent(testStudent);
        testStudent.setFullName("Updated Name");
        assertTrue(dataStorage.updateStudent(testStudent));
        Student updated = dataStorage.getStudentById(testStudent.getStudentId());
        assertEquals("Updated Name", updated.getFullName());
    }

    @Test
    public void testUpdateRoom() {
        dataStorage.addRoom(testRoom);
        testRoom.setRoomPrice(new BigDecimal("150.00"));
        assertTrue(dataStorage.updateRoom(testRoom));
        Room updated = dataStorage.getRoomById(testRoom.getRoomId());
        assertEquals(new BigDecimal("150.00"), updated.getRoomPrice());
    }

    @Test
    public void testAssignStudentToRoom() {
        dataStorage.addStudent(testStudent);
        dataStorage.addRoom(testRoom);
        assertTrue(dataStorage.assignStudentToRoom(testStudent.getStudentId(), testRoom.getRoomId()));
        assertEquals(testRoom.getRoomId(), dataStorage.getStudentById(testStudent.getStudentId()).getRoomId());
    }

    @Test
    public void testRemoveStudentFromRoom() {
        dataStorage.addStudent(testStudent);
        dataStorage.addRoom(testRoom);
        dataStorage.assignStudentToRoom(testStudent.getStudentId(), testRoom.getRoomId());
        assertTrue(dataStorage.removeStudentFromRoom(testStudent.getStudentId()));
        assertEquals(0, dataStorage.getStudentById(testStudent.getStudentId()).getRoomId());
    }

    @Test
    public void testGetAvailableRooms() {
        dataStorage.addRoom(testRoom);
        assertFalse(dataStorage.getAvailableRooms().isEmpty());
    }

    @Test
    public void testIsRoomFull() {
        dataStorage.addRoom(testRoom);
        assertFalse(dataStorage.isRoomFull(testRoom.getRoomId()));
    }
} 