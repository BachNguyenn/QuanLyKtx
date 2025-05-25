package dao;

import model.Fee;
import model.FeeType;
import connectionDB.DBConnection;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class FeeDAO {
    private final DBConnection dbConnection;

    public FeeDAO() {
        this.dbConnection = DBConnection.getInstance();
    }

    public List<Fee> getAllFees() {
        List<Fee> fees = new ArrayList<>();
        String sql = "SELECT * FROM fees ORDER BY due_date DESC";
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                fees.add(mapResultSetToFee(rs));
            }
        } catch (SQLException e) {
            System.err.println("Error retrieving fees: " + e.getMessage());
        }
        return fees;
    }

    public Fee getFeeById(int feeId) {
        String sql = "SELECT * FROM fees WHERE fee_id = ?";
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, feeId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToFee(rs);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error retrieving fee: " + e.getMessage());
        }
        return null;
    }

    public boolean addFee(Fee fee) {
        String sql = "INSERT INTO fees (fee_code, student_id, fee_type, amount, payment_method, " +
                "payment_status, due_date, payment_date, billing_month, description) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, fee.getFeeCode());
            stmt.setInt(2, fee.getStudentId());
            stmt.setString(3, fee.getFeeType().name());
            stmt.setBigDecimal(4, fee.getAmount());
            stmt.setString(5, fee.getPaymentMethod());
            stmt.setString(6, fee.getPaymentStatus());
            stmt.setDate(7, Date.valueOf(fee.getDueDate()));
            if (fee.getPaymentDate() != null) {
                stmt.setDate(8, Date.valueOf(fee.getPaymentDate()));
            } else {
                stmt.setNull(8, Types.DATE);
            }
            if (fee.getBillingMonth() != null) {
                stmt.setDate(9, Date.valueOf(fee.getBillingMonth()));
            } else {
                stmt.setNull(9, Types.DATE);
            }
            stmt.setString(10, fee.getDescription());
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error adding fee: " + e.getMessage());
            return false;
        }
    }

    public boolean updateFee(Fee fee) {
        String sql = "UPDATE fees SET fee_code = ?, student_id = ?, fee_type = ?, amount = ?, " +
                "payment_method = ?, payment_status = ?, due_date = ?, payment_date = ?, " +
                "billing_month = ?, description = ? WHERE fee_id = ?";
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, fee.getFeeCode());
            stmt.setInt(2, fee.getStudentId());
            stmt.setString(3, fee.getFeeType().name());
            stmt.setBigDecimal(4, fee.getAmount());
            stmt.setString(5, fee.getPaymentMethod());
            stmt.setString(6, fee.getPaymentStatus());
            stmt.setDate(7, Date.valueOf(fee.getDueDate()));
            if (fee.getPaymentDate() != null) {
                stmt.setDate(8, Date.valueOf(fee.getPaymentDate()));
            } else {
                stmt.setNull(8, Types.DATE);
            }
            if (fee.getBillingMonth() != null) {
                stmt.setDate(9, Date.valueOf(fee.getBillingMonth()));
            } else {
                stmt.setNull(9, Types.DATE);
            }
            stmt.setString(10, fee.getDescription());
            stmt.setInt(11, fee.getFeeId());
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error updating fee: " + e.getMessage());
            return false;
        }
    }

    public boolean deleteFee(int feeId) {
        String sql = "DELETE FROM fees WHERE fee_id = ?";
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, feeId);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error deleting fee: " + e.getMessage());
            return false;
        }
    }

    public List<Fee> getFeesByStudentId(int studentId) {
        List<Fee> fees = new ArrayList<>();
        String sql = "SELECT * FROM fees WHERE student_id = ? ORDER BY due_date DESC";
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, studentId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    fees.add(mapResultSetToFee(rs));
                }
            }
        } catch (SQLException e) {
            System.err.println("Error retrieving fees by student: " + e.getMessage());
        }
        return fees;
    }

    public List<Fee> getUnpaidFees() {
        List<Fee> fees = new ArrayList<>();
        String sql = "SELECT * FROM fees WHERE payment_status = 'PENDING' ORDER BY due_date";
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                fees.add(mapResultSetToFee(rs));
            }
        } catch (SQLException e) {
            System.err.println("Error retrieving unpaid fees: " + e.getMessage());
        }
        return fees;
    }

    private Fee mapResultSetToFee(ResultSet rs) throws SQLException {
        Fee fee = new Fee();
        fee.setFeeId(rs.getInt("fee_id"));
        fee.setFeeCode(rs.getString("fee_code"));
        fee.setStudentId(rs.getInt("student_id"));
        fee.setFeeType(FeeType.valueOf(rs.getString("fee_type")));
        fee.setAmount(rs.getBigDecimal("amount"));
        fee.setPaymentMethod(rs.getString("payment_method"));
        fee.setPaymentStatus(rs.getString("payment_status"));

        Date dueDate = rs.getDate("due_date");
        if (dueDate != null) {
            fee.setDueDate(dueDate.toLocalDate());
        }

        Date paymentDate = rs.getDate("payment_date");
        if (paymentDate != null) {
            fee.setPaymentDate(paymentDate.toLocalDate());
        }

        Date billingMonth = rs.getDate("billing_month");
        if (billingMonth != null) {
            fee.setBillingMonth(billingMonth.toLocalDate());
        }

        fee.setDescription(rs.getString("description"));
        return fee;
    }
}
