package dao;

import model.Room;
import model.ARoom;
import model.BRoom;
import connectionDB.DBConnection;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class RoomDAO implements Searchable<Room> {
    private final DBConnection dbConnection;
    
    public RoomDAO() {
        this.dbConnection = DBConnection.getInstance();
    }
    
    public List<Room> getAllRooms() {
        List<Room> rooms = new ArrayList<>();
        String sql = "SELECT * FROM rooms ORDER BY room_number";
        
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            
            while (rs.next()) {
                rooms.add(mapResultSetToRoom(rs));
            }
        } catch (SQLException e) {
            System.err.println("Error retrieving rooms: " + e.getMessage());
        }
        
        return rooms;
    }
    
    public Room getRoomById(int roomId) {
        String sql = "SELECT * FROM rooms WHERE room_id = ?";
        
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, roomId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToRoom(rs);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error retrieving room: " + e.getMessage());
        }
        
        return null;
    }
    
    public boolean addRoom(Room room) {
        String sql = "INSERT INTO rooms (room_number, room_type, bed_count, room_price, " +
                    "additional_fee, status) VALUES (?, ?, ?, ?, ?, ?)";
        
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, room.getRoomNumber());
            stmt.setString(2, room.getRoomType());
            stmt.setInt(3, room.getBedCount());
            stmt.setBigDecimal(4, room.getRoomPrice());
            stmt.setBigDecimal(5, room.getAdditionalFee());
            stmt.setString(6, room.getStatus());
            
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error adding room: " + e.getMessage());
            return false;
        }
    }
    
    public boolean updateRoom(Room room) {
        String sql = "UPDATE rooms SET room_number = ?, room_type = ?, bed_count = ?, " +
                    "room_price = ?, additional_fee = ?, status = ? WHERE room_id = ?";
        
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, room.getRoomNumber());
            stmt.setString(2, room.getRoomType());
            stmt.setInt(3, room.getBedCount());
            stmt.setBigDecimal(4, room.getRoomPrice());
            stmt.setBigDecimal(5, room.getAdditionalFee());
            stmt.setString(6, room.getStatus());
            stmt.setInt(7, room.getRoomId());
            
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error updating room: " + e.getMessage());
            return false;
        }
    }
    
    public boolean deleteRoom(int roomId) {
        String sql = "DELETE FROM rooms WHERE room_id = ?";
        
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, roomId);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error deleting room: " + e.getMessage());
            return false;
        }
    }
    
    @Override
    public List<Room> searchByName(String name) {
        List<Room> rooms = new ArrayList<>();
        String sql = "SELECT * FROM rooms WHERE room_number LIKE ?";
        
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, "%" + name + "%");
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    rooms.add(mapResultSetToRoom(rs));
                }
            }
        } catch (SQLException e) {
            System.err.println("Error searching rooms: " + e.getMessage());
        }
        
        return rooms;
    }
    
    private Room mapResultSetToRoom(ResultSet rs) throws SQLException {
        String roomType = rs.getString("room_type");
        Room room;
        
        if ("A".equals(roomType)) {
            room = new ARoom();
        } else {
            room = new BRoom();
        }
        
        room.setRoomId(rs.getInt("room_id"));
        room.setRoomNumber(rs.getString("room_number"));
        room.setRoomType(roomType);
        room.setBedCount(rs.getInt("bed_count"));
        room.setRoomPrice(rs.getBigDecimal("room_price"));
        room.setAdditionalFee(rs.getBigDecimal("additional_fee"));
        room.setStatus(rs.getString("status"));
        
        return room;
    }
}