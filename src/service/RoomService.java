package service;

import dao.RoomDAO;
import dao.StudentDAO;
import model.Room;
import model.Student;
import java.util.List;

public class RoomService {
    private final RoomDAO roomDAO;
    private final StudentDAO studentDAO;

    public RoomService() {
        this.roomDAO = new RoomDAO();
        this.studentDAO = new StudentDAO();
    }

    public List<Room> getAllRooms() {
        return roomDAO.getAllRooms();
    }

    public Room getRoomById(int roomId) {
        return roomDAO.getRoomById(roomId);
    }

    public boolean addRoom(Room room) {
        if (!isValidRoomData(room)) {
            System.err.println("Invalid room data provided");
            return false;
        }

        if (isRoomNumberExists(room.getRoomNumber())) {
            System.err.println("Room number already exists: " + room.getRoomNumber());
            return false;
        }

        return roomDAO.addRoom(room);
    }

    public boolean updateRoom(Room room) {
        if (!isValidRoomData(room)) {
            System.err.println("Invalid room data provided");
            return false;
        }

        Room existingRoom = roomDAO.getRoomById(room.getRoomId());
        if (existingRoom == null) {
            System.err.println("Room not found with ID: " + room.getRoomId());
            return false;
        }

        return roomDAO.updateRoom(room);
    }

    public boolean deleteRoom(int roomId) {
        Room room = roomDAO.getRoomById(roomId);
        if (room == null) {
            System.err.println("Room not found with ID: " + roomId);
            return false;
        }

        if (isRoomOccupied(roomId)) {
            System.err.println("Cannot delete room that is currently occupied");
            return false;
        }

        return roomDAO.deleteRoom(roomId);
    }

    public List<Room> searchRooms(String searchTerm) {
        if (searchTerm == null || searchTerm.trim().isEmpty()) {
            return getAllRooms();
        }
        return roomDAO.searchByName(searchTerm.trim());
    }

    public List<Room> getAvailableRooms() {
        return getAllRooms().stream()
            .filter(room -> "AVAILABLE".equals(room.getStatus()))
            .toList();
    }

    public boolean setRoomStatus(int roomId, String status) {
        Room room = roomDAO.getRoomById(roomId);
        if (room == null) {
            System.err.println("Room not found with ID: " + roomId);
            return false;
        }

        room.setStatus(status);
        return roomDAO.updateRoom(room);
    }

    public int getRoomOccupancy(int roomId) {
        List<Student> allStudents = studentDAO.getAllStudents();
        return (int) allStudents.stream()
            .filter(student -> student.getRoomId() == roomId)
            .count();
    }

    private boolean isValidRoomData(Room room) {
        return room != null &&
            room.getRoomNumber() != null && !room.getRoomNumber().trim().isEmpty() &&
            room.getRoomType() != null && !room.getRoomType().trim().isEmpty() &&
            room.getBedCount() > 0 &&
            room.getRoomPrice() != null && room.getRoomPrice().compareTo(java.math.BigDecimal.ZERO) >= 0;
    }

    private boolean isRoomNumberExists(String roomNumber) {
        List<Room> existingRooms = roomDAO.searchByName(roomNumber);
        return existingRooms.stream()
            .anyMatch(r -> roomNumber.equals(r.getRoomNumber()));
    }

    private boolean isRoomOccupied(int roomId) {
        return getRoomOccupancy(roomId) > 0;
    }
}
