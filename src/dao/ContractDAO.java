package dao;

import model.Contract;
import connectionDB.DBConnection;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ContractDAO {
    private final DBConnection dbConnection;
    
    public ContractDAO() {
        this.dbConnection = DBConnection.getInstance();
    }
    
    public List<Contract> getAllContracts() {
        List<Contract> contracts = new ArrayList<>();
        String sql = "SELECT * FROM contracts ORDER BY start_date DESC";
        
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            
            while (rs.next()) {
                contracts.add(mapResultSetToContract(rs));
            }
        } catch (SQLException e) {
            System.err.println("Error retrieving contracts: " + e.getMessage());
        }
        
        return contracts;
    }
    
    public Contract getContractById(int contractId) {
        String sql = "SELECT * FROM contracts WHERE contract_id = ?";
        
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, contractId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToContract(rs);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error retrieving contract: " + e.getMessage());
        }
        
        return null;
    }
    
    public boolean addContract(Contract contract) {
        String sql = "INSERT INTO contracts (contract_code, student_id, room_id, start_date, " +
                    "end_date, room_price, payment_method, contract_status, deposit_amount) " +
                    "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
        
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, contract.getContractCode());
            stmt.setInt(2, contract.getStudentId());
            stmt.setInt(3, contract.getRoomId());
            stmt.setDate(4, Date.valueOf(contract.getStartDate()));
            stmt.setDate(5, Date.valueOf(contract.getEndDate()));
            stmt.setBigDecimal(6, contract.getRoomPrice());
            stmt.setString(7, contract.getPaymentMethod());
            stmt.setString(8, contract.getContractStatus());
            stmt.setBigDecimal(9, contract.getDepositAmount());
            
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error adding contract: " + e.getMessage());
            return false;
        }
    }
    
    public boolean updateContract(Contract contract) {
        String sql = "UPDATE contracts SET contract_code = ?, student_id = ?, room_id = ?, " +
                    "start_date = ?, end_date = ?, room_price = ?, payment_method = ?, " +
                    "contract_status = ?, deposit_amount = ? WHERE contract_id = ?";
        
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, contract.getContractCode());
            stmt.setInt(2, contract.getStudentId());
            stmt.setInt(3, contract.getRoomId());
            stmt.setDate(4, Date.valueOf(contract.getStartDate()));
            stmt.setDate(5, Date.valueOf(contract.getEndDate()));
            stmt.setBigDecimal(6, contract.getRoomPrice());
            stmt.setString(7, contract.getPaymentMethod());
            stmt.setString(8, contract.getContractStatus());
            stmt.setBigDecimal(9, contract.getDepositAmount());
            stmt.setInt(10, contract.getContractId());

              return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error updating contract: " + e.getMessage());
            return false;
        }
    }

    public boolean deleteContract(int contractId) {
        String sql = "DELETE FROM contracts WHERE contract_id = ?";
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, contractId);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
        System.err.println("Error deleting contract: " + e.getMessage());
        return false;
    }
}
public List<Contract> getContractsByStudentId(int student_id) {
        List<Contract> contracts = new ArrayList<>();
        String sql = "SELECT * FROM contracts WHERE student_id = ? ORDER BY start_date DESC";

        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, student_id);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    contracts.add(mapResultSetToContract(rs));
                }
            }
         } catch (SQLException e) {
            System.err.println("Error retrieving contracts by student: " + e.getMessage());
         }
         return contracts;
    
    }

    private Contract mapResultSetToContract(ResultSet rs) throws SQLException {
        Contract contract = new Contract();
        contract.setContractId(rs.getInt("contract_id"));
        contract.setContractCode(rs.getString("contract_code"));
        contract.setStudentId(rs.getInt("student_id"));
        contract.setRoomId(rs.getInt("room_id"));
        
        Date startDate = rs.getDate("start_date");
        if (startDate != null) {
            contract.setStartDate(startDate.toLocalDate());
        }

        Date endDate = rs.getDate("end_date");
        if (endDate != null) {
            contract.setEndDate(endDate.toLocalDate());
        }

        contract.setRoomPrice(rs.getBigDecimal("room_price"));
        contract.setPaymentMethod(rs.getString("payment_method"));
        contract.setDepositAmount(rs.getBigDecimal("deposit_amount"));
        contract.setContractStatus(rs.getString("contract_status"));
        return contract;
  }
}
    